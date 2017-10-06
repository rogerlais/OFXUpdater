/*
 *
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.google.common.base.Strings;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * NOTAS: Operations with DVs = "X" not fully tested
 *
 * @author roger
 */
public class BBTransactionHelper {

    private static String getModulo11(String campo) {
        //Modulo 11 - 23456789 (type = 9)

        int multiplicador = 9;
        int multiplicacao;
        int soma_campo = 0;

        for (int i = campo.length(); i > 0; i--) {
            multiplicacao = Integer.parseInt(campo.substring(i - 1, i)) * multiplicador;

            soma_campo = soma_campo + multiplicacao;

            multiplicador--;
            if (multiplicador < 2) {
                multiplicador = 9;
            }
        }
        int dac = (soma_campo % 11);
        if (dac == 10) {
            return "X";
        }
        return ((Integer) dac).toString();
    }

    private FakeRegister fakeData;

    protected FakeRegister getFakeData() throws OFXException {
        if (this.fakeData != null) {
            return this.fakeData;
        } else {
            FakeRegister result = null;
            if (this.targetBranch != null) {
                for (FakeRegister reg : fakeList) {
                    if ( // test all attrs
                            Integer.parseInt(this.targetBranch) == Integer.parseInt(reg.getTrueBranch()) //branch
                            & Integer.parseInt(this.targetAccount) == Integer.parseInt(reg.getTrueAccount())) //account
                    {
                        result = reg;
                        break;
                    }
                }
            } else {
                throw new OFXException("Chave(agência/conta) não encontrada na lista de substituições!");
            }
            this.fakeData = result;
            return this.fakeData;
        }
    }

    static private List<FakeRegister> fakeList = new ArrayList<FakeRegister>();

    static private Map<String, String> memoDictionary = new HashMap<String, String>();

    static public void loadMemoDictionary() {
        //pagador da merreca
        memoDictionary.put("TRIBUNAL REGIONAL ELEITORAL DA PARA", "ESTABULO");
        //Apolo
        memoDictionary.put("81997636329", "(MISTER-M)");
        //Roger
        memoDictionary.put("83996909016", "(CELUAR MANE)");
        //Patroa
        memoDictionary.put("83996880930", "(CELUAR PATROA)");
        //Perguntar a patroa
        memoDictionary.put("83998638007", "(DESCONHECIDO-MV)");

    }

    static public void loadFakeList() {
        //todo: Buscar ler dados de arquivo de configuração
        //Fumo (Neco Biu Junior)
        fakeList.add(
                new FakeRegister(
                        GlobalConfig.OLD_MASTER_BRANCH, GlobalConfig.OLD_MASTER_BRANCH_DV, GlobalConfig.OLD_MASTER_ACCOUNT,
                        GlobalConfig.OLD_MASTER_ACCOUNT_DV, "ROGERLAIS ANDR", GlobalConfig.NEW_MASTER_BRANCH,
                        GlobalConfig.NEW_MASTER_BRANCH_DV, GlobalConfig.NEW_MASTER_ACCOUNT, GlobalConfig.NEW_MASTER_ACCOUNT_DV,
                        GlobalConfig.MASTER_ACCOUNT_ALIAS, "9516565", "master")
        );
        //MV
        fakeList.add(
                new FakeRegister(
                        GlobalConfig.OLD_SLAVE_BRANCH, GlobalConfig.OLD_SLAVE_BRANCH_DV, GlobalConfig.OLD_SLAVE_ACCOUNT,
                        GlobalConfig.OLD_SLAVE_ACCOUNT_DV, "MERCIA VIEIRA", GlobalConfig.NEW_SLAVE_BRANCH,
                        GlobalConfig.NEW_SLAVE_BRANCH_DV, GlobalConfig.NEW_SLAVE_ACCOUNT, GlobalConfig.NEW_SLAVE_ACCOUNT_DV,
                        GlobalConfig.SLAVE_ACCOUNT_ALIAS, "7977137", "slave")
        );
        //Pai
        fakeList.add(new FakeRegister("1138", "X", "2560", "7", "MANOEL S DA SI", "3221", "2", "1257", "2", "CONTA OLIMPO",
                null, "PAI(DOACAO UNIVERSAL - Aleluia!)"));
        //Irmã
        fakeList.add(new FakeRegister("1138", "X", "17235", "9", "FABIANA ANDRAD", "3221", "2", "489520", "7", "CONTA PEGASUS",
                null, "Fabiana(Instituto cancer Dr. Arnaldo"));

        //Zé Antonio(BSB)
        fakeList.add(new FakeRegister("1312", "X", "5248758", "x", "JOSE ANTONIO C", "1512", "1", "7107", "2", "DA TERRA SARNEY",
                null, "c-cunhado(Igreja mundial poder de deus"));

    }

    public BBTransactionHelper(Transaction trans, String sourceBranch, String sourceAccount) throws OFXException {
        //todo: criar instancia a partir de dados reais
        //<CHECKNUM>138000017235</CHECKNUM>
        //<REFNUM>521.138.000.017.235</REFNUM>
        String refNum = trans.getReferenceNumber();
        String chkNum = trans.getCheckNumber();

        this.targetAccount = GlobalConfig.trimChar(sourceAccount, '0');
        this.targetBranch = GlobalConfig.trimChar(sourceBranch, '0');

        //transação interna c/c <-> poupança ou saque da conta
        if (chkNum.endsWith(sourceAccount) || (chkNum.endsWith(this.getFakeData().getCashOutAccount()))) {
            //todo: coleta de dados para operação interna
            this.operationCode = 0;
            //TODO: throw new OFXException("Operação não tratada ainda");
        } else {
            //movimentação de entrada/saida ocorrida
            switch (refNum.length()) {
                case GlobalConfig.REFNUM_TRANSFER_LENGTH: {  //operação de transferencia
                    this.operationCode = Integer.parseInt(refNum.substring(0, 2));
                    switch (this.operationCode) {
                        case 51:
                        case 52:
                        case 60: {  //Transferência online
                            this.setTargetAccount(GlobalConfig.trimChar(chkNum.substring(chkNum.length() - GlobalConfig.ACCOUNT_BB_LENGTH), '0'));
                            this.setTargetAccountDV(getModulo11(this.getTargetAccount()));
                            this.setTargetBranch(refNum.substring(2, 7).replace(".", ""));  //pega 5 e exclui o ponto
                            break;
                        }
                        case 80: {  //pacote de serviços
                            break;
                        }
                        case 99: {  //pagamento de convenio(agua, luz, telefone, etc)
                            break;
                        }
                        default: {
                            this.targetBranch = sourceAccount; //Usar o valor de referência
                            if ( //operação de saque conta master
                                    (this.targetBranch.equals(GlobalConfig.OLD_MASTER_ACCOUNT)
                                    && //bate como sendo saque
                                    chkNum.endsWith(this.getFakeData().getCashOutAccount())) //conta saque
                                    || ( //ou conta slave
                                    (this.targetBranch.equals(GlobalConfig.OLD_SLAVE_ACCOUNT)
                                    && //bate como sendo saque
                                    chkNum.endsWith(this.getFakeData().getCashOutAccount())))) {
                                break;
                            } else {
                                throw new OFXException(String.format("Código de operação(%d) não suportado.", this.operationCode));
                            }
                        }
                    }
                    break;
                }
                default: {
                    this.operationCode = 0;  //anula unica não nula anteriormente
                }
            }
        }
    }

    private String targetAccountDV;

    /**
     * @return the targetBranch
     */
    public String getTargetBranch() {
        return targetBranch;
    }

    /**
     * @param targetBranch the targetBranch to set
     * @throws br.jus.trepb.sesop.mvnjfxapp.OFXException
     */
    public void setTargetBranch(String targetBranch) throws OFXException {
        if (targetBranch.length() > 4) {
            throw new OFXException("Digitos da identificação da agência com comprimento superior ao máximo possível");
        }
        this.fakeData = null;
        this.targetBranch = targetBranch;
    }

    /**
     * @return the targetAccount
     */
    public String getTargetAccount() {
        return targetAccount;
    }

    /**
     * @param targetAccount the targetAccount to set
     */
    public void setTargetAccount(String targetAccount) {
        this.fakeData = null;
        this.targetAccount = targetAccount;
    }

    /**
     * @return the variantCode
     */
    public int getVariantCode() {
        return variantCode;
    }

    /**
     * @param variantCode the variantCode to set
     */
    public void setVariantCode(int variantCode) {
        this.variantCode = variantCode;
    }

    /**
     * @return the operationCode
     */
    public int getOperationCode() {
        return operationCode;
    }

    /**
     * @param operationCode the operationCode to set
     */
    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    private String targetBranch;
    private String targetAccount;
    private int variantCode;  //Valor padrão para transferencia entre contas
    private int operationCode;  //Valor padrão para transferncia entre contas

    public String getRefNum() {
        //todo confirmado apenas para transferencisa normais, para saque poupança e afins a montagem é diferente
        String tempCode = String.format("%02d", this.operationCode);
        String tempBranch = Strings.padStart(this.targetBranch, 4, '0');
        String tempAccount = Strings.padStart(this.targetAccount, 7, '0');
        String tempVariant = String.format("%02d", this.variantCode);
        String result = tempCode + tempBranch + tempVariant + tempAccount;
        result = BBDigitVerifier.regularTokenizer(result, ".", 3, false);
        return result;
    }

    public String getCheckNum() {
        String tempCode = String.format("%02d", this.operationCode); //nâo usado
        String tempBranch = Strings.padStart(this.targetBranch, 4, '0');
        String tempAccount = Strings.padStart(this.targetAccount, 7, '0');
        String tempVariant = String.format("%02d", this.variantCode);
        String result = tempBranch.substring(1) + tempVariant + tempAccount;
        return result;
    }

    public int getAccountDVCalculated() {
        int result = BBDigitVerifier.getAccountVerifier(this.targetBranch);
        return result;
    }

    public int getBranchDVCalculated() {
        int result = BBDigitVerifier.getBranchVeririfer(this.targetBranch);
        return result;
    }

    String getFakeMemo(Date datePosted, String detail) throws OFXException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datePosted);
        String result = "";
        String prefix;
        switch (this.operationCode) {
            case 60: {
                //"Transferência on line - 07/01 3501      21038-2 MERCIA VIEIRA"
                //"Transferência on line - 10/01 1138       2560-7 MANOEL S DA SI"
                //"Transferência on line - 07/01 3612      55898-2 ROGERLAIS ANDR"
                //"Transferência on line - 15/01 1312    5248758-X JOSE ANTONIO C"
                //"Transferido da poupança - 19/08 3501      21038-2 MERCIA VIEIRA" --- PQP comprimento diferente da operação 60
                prefix = "Transferência on line - ";
                break;
            }
            case 51: {
                //"Transferido da poupança - 19/08 3501      21038-2 MERCIA VIEIRA" --- PQP comprimento diferente da operação 60
                prefix = "Transferido da poupança - ";
                break;
            }
            default: {
                prefix = null;
            }
        }
        if (prefix == null) {
            throw new UnsupportedOperationException(
                    String.format("Código da operação (%d) não suportado para a geração de informação textual da transação.",
                            this.operationCode));
        }
        String dateStr = String.format("%1$02d/%2$02d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1);
        prefix += dateStr;
        result = prefix + " " + Strings.padEnd(this.getFakeTargetBranch(), 4, ' ') + Strings.padStart(this.getFakeTargetAccount(), 11, ' ') + '-'
                + this.getFakeTargetAccountDV() + " " + this.getFakeShortName(); //?? como pegar o complemento
        return result.trim();
    }

    public String getFakeTargetAccount() throws OFXException {
        FakeRegister reg = this.getFakeData();
        if (reg != null) {
            return reg.getFakeAccount();
        } else {
            return null;
        }
    }

    public String getFakeShortName() throws OFXException {
        FakeRegister reg = this.getFakeData();
        if (reg != null) {
            return reg.getFakeShortName();
        } else {
            return null;
        }
    }

    public String getFakeTargetAccountDV() throws OFXException {
        FakeRegister reg = this.getFakeData();
        if (reg != null) {
            return reg.getFakeAccountVD();
        } else {
            return null;
        }
    }

    public String getFakeTargetBranch() throws OFXException {
        //TODO: pegar agencia para fake de fabiana e pai
        FakeRegister reg = this.getFakeData();
        if (reg != null) {
            return reg.getFakeBranch();
        } else {
            return null;
        }
        /*
        switch (this.targetBranch) {
            case GlobalConfig.OLD_MASTER_BRANCH: {
                return GlobalConfig.NEW_MASTER_BRANCH;
            }
            case GlobalConfig.OLD_SLAVE_BRANCH: {
                return GlobalConfig.NEW_SLAVE_BRANCH;
            }
            case "1138": {  //Pai e Fabiana
                return "1138";
            }
            default: {
                return this.targetBranch;
            }
        }
         */
    }

    public String getFakeRefNum() throws OFXException {
        //todo confirmado apenas para transferencisa normais, para saque poupança e afins a montagem é diferente
        String result = null;
        if (this.getFakeData() != null) {
            String tempCode = String.format("%02d", this.operationCode);
            String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
            String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
            String tempVariant = String.format("%02d", this.variantCode);
            result = tempCode + tempBranch + tempVariant + tempAccount;
            result = BBDigitVerifier.regularTokenizer(result, ".", 3, false);
        }
        return result;
    }

    public String getFakeCheckNum() throws OFXException {
        switch (this.operationCode) {
            case 0:  //indeterminada
            case 80: //Pacote de serviços
            case 99: //pagto convênio(agua, luz, telefone)
            {
                return null;
            }
            default: {
                if ((this.targetBranch != null) && (this.targetAccount != null)) {
                    String result = null;
                    if (this.getFakeData() != null) {
                        String tempCode = String.format("%02d", this.operationCode); //nâo usado
                        String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
                        String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
                        String tempVariant = String.format("%02d", this.variantCode);
                        result = tempBranch.substring(1) + tempVariant + tempAccount;
                    }
                    return result;
                } else {
                    return null;
                }
            }
        }

    }

    /**
     * @return the targetAccountDV
     */
    public String getTargetAccountDV() {
        return targetAccountDV;
    }

    /**
     * @param targetAccountDV the targetAccountDV to set
     */
    public void setTargetAccountDV(String targetAccountDV) {
        this.fakeData = null;
        this.targetAccountDV = targetAccountDV;
    }

    public static String finalFilterMemo(String memo) {
        //TODO: A exemplo das contas registrar lista com os filtros para os memos contendo chave e novo valor
        //TODO: caso a ser resolvido  "Ordem Banc 12 Sec Tes Nac - 060177980001-60 TRIBUNAL REGIONAL ELEI"
        String result = memo;
        for (String key : memoDictionary.keySet()) {
            if (memo.contains(key)) {
                String value = memoDictionary.get(key);
                result = memo.replace(key, value);
                break;
            }
        }
        return result;
    }

}
