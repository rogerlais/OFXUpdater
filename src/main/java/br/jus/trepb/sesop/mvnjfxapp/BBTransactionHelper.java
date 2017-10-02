/*
 *
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.google.common.base.Strings;
import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    protected FakeRegister getFakeData() {
        if (this.fakeData != null) {
            return this.fakeData;
        } else {
            FakeRegister result = null;
            for (FakeRegister reg : fakeList) {
                if ( // test all attrs
                    Integer.parseInt(this.targetBranch) == Integer.parseInt(reg.getTrueBranch()) //branch
                    & Integer.parseInt(this.targetAccount) == Integer.parseInt(reg.getTrueAccount())) //account
                { //todo * realizar a comparação pelo valor numerico
                    result = reg;
                    break;
                }
            }
            this.fakeData = result;
            return this.fakeData;
        }
    }

    static private List<FakeRegister> fakeList;

    static public void loadFakeList() {

        fakeList = new ArrayList<FakeRegister>();
        //todo: Buscar ler dados de arquivo de configuração
        //Mané
        fakeList.add(
            new FakeRegister(
                GlobalConfig.OLD_MASTER_BRANCH, GlobalConfig.OLD_MASTER_BRANCH_DV, GlobalConfig.OLD_MASTER_ACCOUNT,
                GlobalConfig.OLD_MASTER_ACCOUNT_DV, "ROGERLAIS ANDR", GlobalConfig.NEW_MASTER_BRANCH,
                GlobalConfig.NEW_MASTER_BRANCH_DV, GlobalConfig.NEW_MASTER_BRANCH, GlobalConfig.NEW_MASTER_BRANCH_DV,
                GlobalConfig.MASTER_ACCOUNT_ALIAS, "master")
        );
        //MV
        fakeList.add(
            new FakeRegister(
                GlobalConfig.OLD_SLAVE_BRANCH, GlobalConfig.OLD_SLAVE_BRANCH_DV, GlobalConfig.OLD_SLAVE_ACCOUNT,
                GlobalConfig.OLD_SLAVE_ACCOUNT_DV, "MERCIA VIEIRA", GlobalConfig.NEW_SLAVE_BRANCH,
                GlobalConfig.NEW_SLAVE_BRANCH_DV, GlobalConfig.NEW_SLAVE_BRANCH, GlobalConfig.NEW_SLAVE_BRANCH_DV,
                GlobalConfig.SLAVE_ACCOUNT_ALIAS, "slave")
        );
        //Pai
        fakeList.add(new FakeRegister("1138", "X", "2560", "7", "MANOEL S DA SI", "3221", "2", "1257", "2", "CONTA OLIMPO", "PAI(DOACAO UNIVERSAL - Aleluia!)"));
        //Irmã
        fakeList.add(new FakeRegister("1138", "X", "17235", "9", "FABIANA ANDRAD", "3221", "2", "489520", "7", "CONTA PEGASUS", "Fabiana(Instituto ccancer Dr. Arnaldo"));
    }

    public BBTransactionHelper(BankAccountDetails sourceAccount, Transaction trans) throws OFXException {
        //todo: criar instancia a partir de dados reais
        //<CHECKNUM>138000017235</CHECKNUM>
        //<REFNUM>521.138.000.017.235</REFNUM>
        String refNum = trans.getReferenceNumber();
        String chkNum = trans.getCheckNumber();

        if (chkNum.endsWith(sourceAccount.getAccountNumber())) { //transação interna c/c <-> poupança
            //todo: coleta de dados para operação interna
            throw new OFXException("Operação não tratada ainda");
        } else {
            //movimentação de entrada/saida ocorrida
            switch (refNum.length()) {
                case GlobalConfig.REFNUM_TRANSFER_LENGTH: {  //operação de transferencia
                    this.operationCode = Integer.parseInt(refNum.substring(0, 2));
                    switch (this.operationCode) {
                        case 51:
                        case 52:
                        case 60: {
                            this.targetAccount = chkNum.substring(chkNum.length() - GlobalConfig.ACCOUNT_BB_LENGTH);
                            this.targetAccountDV = getModulo11(this.targetAccount);
                            this.targetBranch = refNum.substring(2, 7).replace(".", "");  //pega 5 e exclui o ponto
                            break;
                        }
                        case 99: {  //pagamento de convenio(agua, luz, telefone, etc)
                            break;
                        }
                        default: {
                            throw new OFXException("Código de operação não suportado.");
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
    private int variantCode = 0;  //Valor padrão para transferencia entre contas
    private int operationCode = 60;  //Valor padrão para transferncia entre contas

    public BBTransactionHelper(String targetBranch, String targetAccount, String targetAccountDV) {
        this.targetAccount = targetAccount;
        this.targetBranch = targetBranch;
        this.targetAccountDV = targetAccountDV;
    }

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

    String getMemo(Date datePosted, String detail) {
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
        String sufix = Strings.padEnd(detail, 14, ' ');
        result = prefix + " " + Strings.padEnd(this.getTargetBranch(), 4, ' ') + Strings.padStart(this.targetAccount, 11, ' ') + '-'
            + this.targetAccountDV + " " + sufix; //?? como pegar o complemento
        return result.trim();
    }

    public String getFakeTargetAccount() {
        FakeRegister reg = this.getFakeData();
        if (reg != null) {
            return reg.getFakeAccount();
        } else {
            return null;
        }
    }

    public String getFakeTargetBranch() {
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

    public String getFakeRefNum() {
        //todo confirmado apenas para transferencisa normais, para saque poupança e afins a montagem é diferente
        String tempCode = String.format("%02d", this.operationCode);
        String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
        String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
        String tempVariant = String.format("%02d", this.variantCode);
        String result = tempCode + tempBranch + tempVariant + tempAccount;
        result = BBDigitVerifier.regularTokenizer(result, ".", 3, false);
        return result;
    }

    public String getFakeCheckNum() {
        if (this.targetBranch != null) {
            String tempCode = String.format("%02d", this.operationCode); //nâo usado
            String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
            String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
            String tempVariant = String.format("%02d", this.variantCode);
            String result = tempBranch.substring(1) + tempVariant + tempAccount;
            return result;
        } else {
            return null;
        }
    }

}
