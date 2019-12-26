/*
 *
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.google.common.base.Strings;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    private final Transaction originalTransaction;
    private boolean isInternalTransaction;

    private FakeRegister getFakeData() throws OFXException {
        if (this.fakeData != null) {
            return this.fakeData;
        } else {
            FakeRegister result = null;
            if (this.targetBranch != null) {
                for (FakeRegister reg : FAKELIST) {
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

    private static final List<FakeRegister> FAKELIST = new ArrayList<FakeRegister>();

    private static final Map<String, String> MEMO_DICTIONARY = new HashMap<String, String>();

    private static final List<PreLoadCellInfo> PRELOADED_CELLPHONESNUMBERS = new ArrayList<PreLoadCellInfo>();

    static public void loadPreLoadedCellPhones() {
        PRELOADED_CELLPHONESNUMBERS.clear();
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(83, "996909016", "999999901", "Roger-TIM-PB", "ARUAH-TIM-PB"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(83, "996880930", "999999902", "MV-TIM-PB", "PATROA-TIM-PB"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(83, "988035232", "999999903", "MV-OI-PB(das antigas)", "MV(ANTIGO)-OI-PB"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(81, "997636329", "999999904", "Apolo-TIM-PE", "MISTER-M-TIM-PE"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(83, "998638007", "999999905", "Lucas-TIM-PB", "FERIAS-TIM-PB"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(83, "988806954", "999999906", "MV-OI-PB(desativado)", "PATROA(DESATIVADO)-OI-PB"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(81, "996089270", "999999907", "Radler-TIM-PE", "MACGYVER-TIM-PE"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(83, "999273714", "999999908", "Millenna-TIM-PB", "CABECUDA-TIM-PB"));
        PRELOADED_CELLPHONESNUMBERS.add(new PreLoadCellInfo(81, "996987414", "999999909", "Vo Maria-TIM-PE", "V-MARIA-TIM-PE"));
    }

    static public void loadMemoDictionary() {
        MEMO_DICTIONARY.clear();
        //pagador da merreca
        MEMO_DICTIONARY.put("SOP-TRE", "SOP-SENZALA");
        MEMO_DICTIONARY.put("TRIBUNAL REGIONAL ELEITORAL DA PARA", "ESTABULO"); //[1] - Sempre antes de (2)
        MEMO_DICTIONARY.put("Recebimento de Proventos", "Recebimento de Merreca");
        //Apolo
        MEMO_DICTIONARY.put("81997636329", "(MISTER-M)");
        //Roger
        MEMO_DICTIONARY.put("83996909016", "(CELULAR MANE)");
        //Patroa
        MEMO_DICTIONARY.put("83996880930", "(CELULAR PATROA)");
        //Perguntar a patroa
        MEMO_DICTIONARY.put("83998638007", "(ETERNAS.FERIAS)");
        //ordem bancária(diárias e afins)
        MEMO_DICTIONARY.put("Ordem Banc 12 Sec Tes Nac", "Ajuda de custo");
        MEMO_DICTIONARY.put("060177980001-60", "(CNPJ-Senzala)");
        MEMO_DICTIONARY.put("TRIBUNAL REGIONAL ELEI", "(Senzala)"); //[2] - Sempre depois de (1)
    }

    static public void loadFakeList() {
        FAKELIST.clear();
        //todo: Buscar ler dados de arquivo de configuração
        //Fumo (Neco Biu Junior)
        FAKELIST.add(
                new FakeRegister(
                        GlobalConfig.OLD_MASTER_BRANCH, GlobalConfig.OLD_MASTER_BRANCH_DV, GlobalConfig.OLD_MASTER_ACCOUNT,
                        GlobalConfig.OLD_MASTER_ACCOUNT_DV, "ROGERLAIS ANDR", GlobalConfig.NEW_MASTER_BRANCH,
                        GlobalConfig.NEW_MASTER_BRANCH_DV, GlobalConfig.NEW_MASTER_ACCOUNT, GlobalConfig.NEW_MASTER_ACCOUNT_DV,
                        GlobalConfig.MASTER_ACCOUNT_ALIAS, GlobalConfig.MASTER_CASH_OUT_ACCOUNT, "master")
        );
        //MV
        FAKELIST.add(
                new FakeRegister(
                        GlobalConfig.OLD_SLAVE_BRANCH, GlobalConfig.OLD_SLAVE_BRANCH_DV, GlobalConfig.OLD_SLAVE_ACCOUNT,
                        GlobalConfig.OLD_SLAVE_ACCOUNT_DV, "MERCIA VIEIRA", GlobalConfig.NEW_SLAVE_BRANCH,
                        GlobalConfig.NEW_SLAVE_BRANCH_DV, GlobalConfig.NEW_SLAVE_ACCOUNT, GlobalConfig.NEW_SLAVE_ACCOUNT_DV,
                        GlobalConfig.SLAVE_ACCOUNT_ALIAS, GlobalConfig.SLAVE_CASH_OUT_ACCOUNT, "slave")
        );
        //Pai
        FAKELIST.add(new FakeRegister("1138", "X", "2560", "7", "MANOEL S DA SI", "3221", "2", "1257", "2", "CONTA OLIMPO",
                null, "PAI(DOACAO UNIVERSAL - Aleluia!)"));
        //Irmã
        FAKELIST.add(new FakeRegister("1138", "X", "17235", "9", "FABIANA ANDRAD", "3221", "2", "489520", "7", "CONTA PEGASUS",
                null, "Fabiana(Instituto cancer Dr. Arnaldo"));

        //Zé Antonio(BSB)
        FAKELIST.add(new FakeRegister("1312", "X", "5248758", "x", "JOSE ANTONIO C", "1512", "1", "7107", "2", "FUGIU DE SARNEY",
                null, "co-cunhado(Igreja mundial poder de deus"));

        //Irmã Márcia(BSB)
        FAKELIST.add(new FakeRegister("3380", "X", "20245", "2", "MARCIA VIEIRA", "1614", "4", "170000", "6", "PARAIBA MASCULINA",
                null, "CUNHADA(lagoinha.com/dizimos)"));
    }

    public BBTransactionHelper(Transaction sourceTransaction, String sourceBranch, String sourceAccount) throws OFXException {
        //todo: criar instancia a partir de dados reais
        //<CHECKNUM>138000017235</CHECKNUM>
        //<REFNUM>521.138.000.017.235</REFNUM>
        String refNum = sourceTransaction.getReferenceNumber();
        String chkNum = sourceTransaction.getCheckNumber();

        this.targetAccount = GlobalConfig.trimChar(sourceAccount, '0');
        this.targetBranch = GlobalConfig.trimChar(sourceBranch, '0');
        this.originalTransaction = sourceTransaction;  //usada para remontar carga de celular pré-pago
        this.preloadCellInfo = this.findCellInfo(chkNum, refNum);
        this.scheduledTransferRegister = this.GetScheduledTransferRegister(refNum, chkNum, sourceAccount);  //localiza trfs agendadas cujos dados são "canonicos"

        if (chkNum.endsWith(sourceAccount)) { //transação interna c/c <-> poupança ou saque da conta
            this.selfTransferAdjust(refNum, chkNum, sourceAccount);
        } else {
            if ((null != this.preloadCellInfo) //recarga de celular
                    //|| //ou
                    //chkNum.endsWith(sourceAccount) //age sobre própria conta
                    || //Movimentação interna vinculada a mesma conta
                    (chkNum.endsWith(this.getFakeData().getCashOutAccount())) //Existe referência ao caso acima
                    ) {
                //todo: coleta de dados para operação interna
                this.operationCode = 0;
                //TODO: throw new OFXException("Operação não tratada ainda");
            } else {
                transferAdjust(refNum, chkNum, sourceAccount);  //!parada para caso de tarifa de servico
            }
        }
    }

    private void selfTransferAdjust(String refNum, String chkNum, String sourceAccount) throws OFXException, NumberFormatException {
        this.isInternalTransaction = true;
        switch (refNum.length()) {
            case GlobalConfig.REFNUM_TRANSFER_SAVINGS: { //Saque de puopança para conta corrente
                this.operationCode = 0;  //Apenas dados da própria conta presentes
                this.setTargetAccount(GlobalConfig.trimChar(chkNum.substring(chkNum.length() - GlobalConfig.ACCOUNT_BB_LENGTH), '0'));
                this.setTargetAccountDV(getModulo11(this.getTargetAccount()));
                this.setTargetBranch(refNum.substring(0, 5).replace(".", ""));  //pega 5 e exclui o ponto
                this.setVariantCode(Integer.parseInt(chkNum.substring(3, 5)));
                break;
            }
            case GlobalConfig.REFNUM_TRANSFER_LENGTH: {  //operação de transferencia = recarga de pré-pago
                //TODO: Tratar recarga de pré-pago
                this.operationCode = Integer.parseInt(refNum.substring(0, 2));
                switch (this.operationCode) {
                    //case 51: //?
                    //case 52: //?
                    case 50:
                    case 60: {  //Depósito ou saque poupança<-> C/C
                        this.setTargetAccount(GlobalConfig.trimChar(chkNum.substring(chkNum.length() - GlobalConfig.ACCOUNT_BB_LENGTH), '0'));
                        this.setTargetAccountDV(getModulo11(this.getTargetAccount()));
                        this.setTargetBranch(refNum.substring(2, 7).replace(".", ""));  //pega 5 e exclui o ponto
                        this.setVariantCode(Integer.parseInt(chkNum.substring(3, 5)));
                        break;
                    }
                    //case 80:
                    //case 88:
                    case 89: {  //pacote de serviços(demais dados não mapeaados e sempre se alteram)
                        this.setVariantCode(Integer.parseInt(chkNum.substring(3, 5)));
                        if (this.variantCode != 0) {
                            throw new OFXException(String.format("Variação da operação(%d) incompatível com seu código(%d)", this.operationCode, this.variantCode));
                        }
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

    private void transferAdjust(String refNum, String chkNum, String sourceAccount) throws OFXException, NumberFormatException {
        //movimentação de entrada/saida ocorrida
        switch (refNum.length()) {
            case GlobalConfig.REFNUM_TRANSFER_LENGTH: {  //operação de transferencia = recarga de pré-pago
                this.operationCode = Integer.parseInt(refNum.substring(0, 2));
                switch (this.operationCode) {
                    case 22:  //transferência online recebimento/crédito(mais uma)
                    case 33: //transf. agendada entre cpfs distintos
                    case 48: //Depósito em envelope ou por TAA
                    case 51:
                    case 52:  //transferência online entre contas
                    case 55:  //transferência online entre contas(oriundas do exterior)
                    case 60: {  //Transferência online
                        this.setTargetAccount(GlobalConfig.trimChar(chkNum.substring(chkNum.length() - GlobalConfig.ACCOUNT_BB_LENGTH), '0'));
                        this.setTargetAccountDV(getModulo11(this.getTargetAccount()));
                        this.setTargetBranch(refNum.substring(2, 7).replace(".", ""));  //pega 5 e exclui o ponto
                        break;
                    }
                    case 80:
                    case 84: //Empréstimo eletrônico
                    case 87:
                    case 88:
                    case 89: {  //pacote de serviços(demais dados não mapeados e sempre se alteram)
                        this.setVariantCode(Integer.parseInt(chkNum.substring(3, 5)));
                        if (this.variantCode != 0) {
                            throw new OFXException(String.format("Variação da operação(%d) incompatível com seu código(%d)", this.operationCode, this.variantCode));
                        }
                        break;
                    }
                    case 82: //tarifa sobre transferência além do pacote da franquia
                    case 83: //Depósito em boca de caixa
                    case 85: //Tarifa sobre servico avulso
                    case 86: { //Pagto de financiamento com o banco
                        this.setVariantCode(Integer.parseInt(chkNum.substring(3, 5)));
                        //Comparação feita com magic number  DOC Eletronico observado até agora apenas para MV e sem saldo para débito imediato qdo passou do limite mensal
                        //pode também incluir:
                        //Boca de caixa(apenas MV)
                        //20 = debitado imediatamente, 
                        //80 = posteriormente e para pagamento de CDC 0 = ???Desconhecido
                        Integer[] temporalCodes = new Integer[]{0, 15, 20, 80};
                        if (!Arrays.asList(temporalCodes).contains(this.variantCode)) {
                            throw new OFXException(String.format("Variação da operação(%d) incompatível com seu código(%d)", this.operationCode, this.variantCode));
                        }
                        break;
                    }
                    case 10: //restituição IRPF
                    case 99: {  //pagamento de convenio(agua, luz, telefone, etc)
                        break;
                    }
                    default: {
                        if (this.scheduledTransferRegister != null) {
                            //Montagem de saida será alterada para esta operação
                            this.setTargetAccount(scheduledTransferRegister.getTrueAccount());
                            this.setTargetBranch(scheduledTransferRegister.getTrueBranch());
                            this.setTargetAccountDV(getModulo11(scheduledTransferRegister.getTrueAccount()));
                        } else {
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
                }
                break;
            }
            default: {
                if (this.getIsTEDOperation()) {
                    FXMLController.showAlert("Aviso:", "Encontrado TED, potencialmente irrelevante, não mapeada:\n\r"
                            + "Número do cheque = " + this.originalTransaction.getCheckNumber());
                }
                this.operationCode = 0;  //anula unica não nula anteriormente
            }
        } //end switch para tamanho do código informado por refnum
    }

    private FakeRegister GetScheduledTransferRegister(String refNum, String chkNum, String sourceAccount) {
        /*Verificar se o formato de checknum e refnum obedece o padrão
        <CHECKNUM>100000021038</CHECKNUM>
        <REFNUM>350.100.000.021.038</REFNUM>
        <MEMO>Transferência Agendada - 25/12 3501      21038-2 MERCIA VIEIRA</MEMO>
         */
        int refLimit = Integer.min(5, refNum.length());  //por aparecimento de valor muito curto em TED falho
        String branchByRefNum = GlobalConfig.trimChar(refNum.substring(0, refLimit).replace(".", ""), '0');
        String accountByCheckNum = GlobalConfig.trimChar(chkNum.substring(6, 12), '0');
        for (FakeRegister reg : BBTransactionHelper.FAKELIST) {
            if (reg.getTrueBranch().equals(branchByRefNum) & reg.getTrueAccount().equals(accountByCheckNum)) {
                return reg;
            }
        }
        return null;
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

    private String targetBranch; //Agência fonte da operação
    private String targetAccount;  //Conta fonte da operação
    private int variantCode;  //Valor padrão para transferencia entre contas
    private int operationCode;  //Valor padrão para transferncia entre contas
    private final PreLoadCellInfo preloadCellInfo;  //Mapeamento de operação de recarga de celular
    private final FakeRegister scheduledTransferRegister; //Mapeameamento de transferencia agendada para conta de destino

    public String getRefNum() {
        //todo confirmado apenas para transferencisa normais, para saque poupança e afins a montagem é diferente
        String tempCode = String.format("%02d", this.operationCode);
        String tempBranch = Strings.padStart(this.targetBranch, 4, '0');
        String tempAccount = Strings.padStart(this.targetAccount, GlobalConfig.ACCOUNT_BB_LENGTH, '0');
        String tempVariant = String.format("%02d", this.variantCode);
        String result = tempCode + tempBranch + tempVariant + tempAccount;
        result = BBDigitVerifier.regularTokenizer(result, ".", 3, false);
        return result;
    }

    public String getCheckNum() {
        String tempCode = String.format("%02d", this.operationCode); //nâo usado
        String tempBranch = Strings.padStart(this.targetBranch, 4, '0');
        String tempAccount = Strings.padStart(this.targetAccount, GlobalConfig.ACCOUNT_BB_LENGTH, '0');
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

    protected String getFakeMemo() throws OFXException {
        String result = null;
        PreLoadCellInfo cellInfo = this.getPreLoadCellInfo();
        if (cellInfo != null) {
            result = getFakeMemoByCellInfo(cellInfo);
        } else {
            FakeRegister fData = this.getFakeData();
            if (fData == null) {  //Sem registro de mapeamento(isso pode ser perigoso)
                result = this.originalTransaction.getMemo();
                FXMLController.showAlert("Aviso:", "Encontrada operação, potencialmente irrelevante, não mapeada:\n\r" + result);
            } else {
                result = getFakeMemoByFakeData(fData, result);
            }
        }
        return BBTransactionHelper.finalFilterMemo(result.trim());
    }

    private String getFakeMemoByFakeData(FakeRegister fData, String result) throws UnsupportedOperationException, OFXException {
        String impactedAccountFull = BBDigitVerifier.padLeftString(fData.getFakeAccount(), GlobalConfig.ACCOUNT_BB_LENGTH, ' ') + "-" + fData.getFakeAccountVD();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.originalTransaction.getDatePosted());
        String prefix;
        if (isInternalTransaction) {
            switch (this.operationCode) {
                case 0: {
                    prefix = "Transferido da poupança - ";
                    break;
                }
                //case 52: //?
                case 60: { //Depósito poupança pela C/C
                    impactedAccountFull = String.format("%2d", this.variantCode)
                            + BBDigitVerifier.padLeftString(fData.getFakeAccount(), GlobalConfig.ACCOUNT_BB_LENGTH, '0')
                            + "-" + fData.getFakeAccountVD();
                    prefix = "Aplicação Poupança - ";
                    break;
                }
                case 51: { //Saque poupança pela C/C
                    //"Transferido da poupança - 19/08 3501      21038-2 MERCIA VIEIRA" --- PQP comprimento diferente da operação 60
                    prefix = "Transferido da poupança - ";
                    break;
                }
                default: {
                    prefix = null;
                }
            }
        } else {
            switch (this.operationCode) {
                case 22:
                case 52:
                case 60: { //Depósito poupança pela C/C
                    if (this.isInternalTransaction) {
                        impactedAccountFull = String.format("%d00", this.operationCode) + impactedAccountFull; //altera conta impactada apenas para pouança
                        prefix = "Aplicação Poupança - ";
                    } else {
                        prefix = "Transferência on line - ";
                    }
                    break;
                }
                case 51: { //Saque poupança pela C/C
                    //"Transferido da poupança - 19/08 3501      21038-2 MERCIA VIEIRA" --- PQP comprimento diferente da operação 60
                    prefix = "Transferido da poupança - ";
                    break;
                }
                default: {
                    if (this.scheduledTransferRegister != null) {
                        prefix = "Transferência Agendada - ";
                    } else {
                        prefix = null;
                    }
                }
            }
        }
        if (prefix == null) {
            if (this.operationCode == 0) { //Operação não tratada/mapeada
                result = this.originalTransaction.getMemo();
            } else {
                Integer[] BANK_OPERATION_CODES = new Integer[]{10, 80, 82, 83, 84, 85, 86, 87, 88, 89}; //Lista de operações bancarias ou saques
                Integer[] TEMPORAL_CODES = new Integer[]{0, 15, 20, 80}; //VariantCodes(0 = debito para cedente, 15 = Boca caixa, 20 = debito imediato para banco, 80 = débito posterior)
                if (BBTransactionHelper.contains(BANK_OPERATION_CODES, this.operationCode) && (Arrays.asList(TEMPORAL_CODES).contains(this.variantCode))) {
                    result = this.originalTransaction.getMemo();
                } else {
                    throw new UnsupportedOperationException(
                            String.format("Código da operação (%d) não suportado para a geração de informação textual da transação.",
                                    this.operationCode));
                }
            }
        } else {
            String dateStr = String.format("%1$02d/%2$02d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1);
            prefix += dateStr;
            result = prefix + " " + Strings.padEnd(this.getFakeTargetBranch(), 4, ' ')
                    + " " + impactedAccountFull + " " + this.getFakeShortName(); //?? como pegar o complemento
        }
        return result;
    }

    private String getFakeMemoByCellInfo(PreLoadCellInfo cellInfo) {
        return "Telefone Pre-Pago - " + Integer.toString(cellInfo.getDDD()) + cellInfo.getFakeCellNumber() + " " + cellInfo.getFakeDescription();
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
    }

    public String getFakeRefNum() throws OFXException {
        //todo confirmado apenas para transferencisa normais, para saque poupança e afins a montagem é diferente
        String result = null;
        if (this.preloadCellInfo == null) {
            if (this.scheduledTransferRegister != null) {
                result = getFakeRefNumByScheduledTransfer(this.scheduledTransferRegister);
            } else {

                if (this.getFakeData() != null) {
                    String tempCode = String.format("%02d", this.operationCode);
                    String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
                    String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
                    String tempVariant = String.format("%02d", this.variantCode);
                    if (this.isInternalTransaction) {
                        switch (this.operationCode) {
                            case 0: {
                                result = tempBranch + tempVariant + tempAccount;
                                break;
                            }
                            case 60: {
                                result = tempCode + tempBranch + tempVariant + tempAccount;
                                break;
                            }
                            default: {
                                throw new OFXException(String.format("Código de operação(%d) não nulo para transferência interna", this.operationCode));
                            }
                        }
                    } else {
                        result = tempCode + tempBranch + tempVariant + tempAccount;
                    }
                    result = BBDigitVerifier.regularTokenizer(result, ".", 3, false);
                }
            }
        } else {
            //numero do celular + 6 digitos finais do numero do cheknum
            result = this.preloadCellInfo.getFakeCellNumber() + this.originalTransaction.getCheckNumber().substring(6);
            result = BBDigitVerifier.regularTokenizer(result, ".", 3, false);
        }
        return result;
    }

    public String getFakeCheckNum() throws OFXException {
        String result = null;
        if (this.isInternalTransaction) {
            result = getFakeCheckNumByInternalTransaction(result);
        } else {
            if (this.preloadCellInfo == null) {
                if (this.scheduledTransferRegister != null) {
                    //100000021038
                    result = this.getFakeTargetAccountDV() + "000" + Strings.padStart(this.getFakeTargetAccount(), GlobalConfig.ACCOUNT_BB_LENGTH, '0');
                } else {
                    switch (this.operationCode) {
                        case 0:  //indeterminada
                        case 10: //Restituição IRPF(mantem dados)
                        case 80: //Pacote de serviços(demais dados não mapeados e sempre se alteram)
                        case 88: //Pacote de serviços(demais dados não mapeados e sempre se alteram)
                        case 89: //Pacote de serviços(demais dados não mapeados e sempre se alteram)
                        case 99: //pagto convênio(agua, luz, telefone)
                        {
                            return (String) null;
                        }
                        default: {
                            if ((this.targetBranch != null) && (this.targetAccount != null)) {
                                if (this.getFakeData() != null) {
                                    String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
                                    String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
                                    String tempVariant = String.format("%02d", this.variantCode);
                                    return tempBranch.substring(1) + tempVariant + tempAccount;
                                }
                            } else {
                                return (String) null;
                            }
                        }
                    }
                }
            } else {  //composição para recarga de celular
                //6 finais da transação original mais os 6 últimos do celular
                String oldCheckNum = this.originalTransaction.getCheckNumber();
                if (oldCheckNum.length() > 11) {
                    result = this.preloadCellInfo.getFakeCellNumber().substring(3, 9) + oldCheckNum.substring(6, 12);
                } else {
                    throw new OFXException("CheckNum original incompatível com operação de recarga de celular.");
                }
            }
        }
        return result;
    }

    private String getFakeCheckNumByInternalTransaction(String result) throws OFXException {
        switch (this.operationCode) {
            case 0: {
                String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
                String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
                String tempVariant = String.format("%02d", this.variantCode);
                result = tempBranch.substring(1) + tempVariant + tempAccount;
                break;
            }
            case 60: {
                String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
                String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
                String tempVariant = String.format("%02d", this.variantCode);
                result = tempBranch.substring(1) + tempVariant + tempAccount;
                break;
            }
            default: {
                throw new OFXException("Erro montando CheckNum com dados atuais para operação interna");
            }
        }
        return result;
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
        //TODO: caso a ser resolvido  "Ordem Banc 12 Sec Tes Nac - 060177980001-60 TRIBUNAL REGIONAL ELEI"
        //infelizmente para contemplar casos como acima temos de varrer toda a lista sempre
        String result = memo;
        for (String key : MEMO_DICTIONARY.keySet()) {
            if (result.contains(key)) {
                String value = MEMO_DICTIONARY.get(key);
                result = result.replace(key, value);
            }
        }
        return result;
    }

    private PreLoadCellInfo findCellInfo(String chkNum, String refNum) {
        PreLoadCellInfo result = null;
        String lookupPhone = refNum.replace(".", "");  //remove os pontos
        if (lookupPhone.length() >= 9) {
            lookupPhone = lookupPhone.substring(0, 9);  //assume-se sempre 9 digitos a partir de agora
            for (PreLoadCellInfo cellPhone : PRELOADED_CELLPHONESNUMBERS) {
                if (cellPhone.getCellNumber().equals(lookupPhone)) {
                    result = cellPhone;
                    break;
                }
            }
        }
        return result;
    }

    public PreLoadCellInfo getPreLoadCellInfo() {
        return this.preloadCellInfo;
    }

    /**
     * Search through an object array for a specific element. The conditions to match are the same instance or same value( by
     * "equals" evaluation)
     *
     * @param <T>
     * @param array
     * @param v
     * @return
     */
    public static <T> boolean contains(final T[] array, final T v) {
        //TODO: Levar para biblioteca
        if (v == null) {
            for (final T e : array) {
                if (e == null) {
                    return true;
                }
            }
        } else {
            for (final T e : array) {
                if (e == v || v.equals(e)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getFakeRefNumByScheduledTransfer(FakeRegister schdFakeReg) {
        String result = Strings.padStart(schdFakeReg.getFakeBranch(), 4, '0')
                + schdFakeReg.getFakeBranchVD()
                + "000" //TODO: Composição ainda incerta
                + Strings.padStart(schdFakeReg.getFakeAccount(), GlobalConfig.ACCOUNT_BB_LENGTH, '0'); //350.100.000.021.038
        result = BBDigitVerifier.regularTokenizer(result, ".", 3, false);
        return result;
    }

    private boolean getIsTEDOperation() {
        String refStr = this.originalTransaction.getReferenceNumber().replace(".", "");
        if (this.originalTransaction.getCheckNumber().endsWith(refStr)) {
            return this.originalTransaction.getMemo().startsWith("TED");
        } else {
            return false;
        }
    }

}
