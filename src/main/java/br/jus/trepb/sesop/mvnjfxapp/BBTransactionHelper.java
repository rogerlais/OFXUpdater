/*
 *
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * NOTAS: Operations with DVs = "X" not fully tested
 *
 * @author roger
 */
public class BBTransactionHelper {

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
        //TODO: Pegar conta fake para pai e fabiana
        //TODO realizar a busca por lista registrada para extensiblidade e talvez persistencia externa ao runtime
        switch (this.targetAccount) {

            case GlobalConfig.OLD_MASTER_ACCOUNT: {
                return GlobalConfig.NEW_MASTER_ACCOUNT;
            }

            case GlobalConfig.OLD_SLAVE_ACCOUNT: {
                return GlobalConfig.NEW_SLAVE_ACCOUNT;
            }

            case "2560": {  //2560-7(pai)
                return "2560";
            }

            case "17235": {  //17235-9 (Fabiana)
                return "17235";
            }

            default: {
                return this.targetAccount;  //retorna a própria
            }
        }
    }

    public String getFakeTargetBranch() {
        //TODO: pegar agencia para fake de fabiana e pai
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
        String tempCode = String.format("%02d", this.operationCode); //nâo usado
        String tempBranch = Strings.padStart(this.getFakeTargetBranch(), 4, '0');
        String tempAccount = Strings.padStart(this.getFakeTargetAccount(), 7, '0');
        String tempVariant = String.format("%02d", this.variantCode);
        String result = tempBranch.substring(1) + tempVariant + tempAccount;
        return result;
    }

}
