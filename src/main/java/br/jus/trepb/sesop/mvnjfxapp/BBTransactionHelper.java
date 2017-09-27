/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import java.text.Format;

/**
 *
 * @author roger
 */
public class BBTransactionHelper {

    /**
     * @return the targetBranch
     */
    public String getTargetBranch() {
        return targetBranch;
    }

    /**
     * @param targetBranch the targetBranch to set
     */
    public void setTargetBranch(String targetBranch) {
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

    public BBTransactionHelper(String targetBranch, String targetAccount) {
        this.targetAccount = targetAccount;
        this.targetBranch = targetBranch;
    }

    public String getRefNum() {
        //todo confirmado apenas para transferencisa normais, para saque poupança e afins a montagem é diferente
        String tempCode = String.format("%02d", this.operationCode);
        String tempBranch = String.format("%04s", this.targetBranch);
        String tempAccount = String.format("%07s", this.targetAccount);
        String tempVariant = String.format("%02d", this.variantCode);
        String result = tempCode + tempBranch + tempVariant + tempAccount;
        result = regularTokenizer(result, ".", 3, false);
        return result;
    }

    public String getCheckNum() {
        String tempCode = String.format("%02d", this.operationCode); //nâo usado
        String tempBranch = String.format("%04s", this.targetBranch);
        String tempAccount = String.format("%07s", this.targetAccount);
        String tempVariant = String.format("%02d", this.variantCode);
        String result = tempBranch.substring(1) + tempVariant + tempAccount;
        return result;
    }

    static private String regularTokenizer(String clearString, String tokenStr, int leapSize, boolean normalDirection) {
        // TODO Deslocar para biblioteca
        int deltaS = tokenStr.length();
        StringBuilder result = new StringBuilder(clearString);
        if (normalDirection) {
            int point = leapSize;
            while (point < result.length()) {
                result.insert(point, tokenStr);
                point += (deltaS + leapSize);
            }
        } else {
            int point = clearString.length() - leapSize;
            while (point > 0) {
                result.insert(point, tokenStr);
                point -= (deltaS + leapSize - 1);
            }
        }
        return result.toString();
    }

    public int accountVerifierDigit() {
        int result = BankDigitVerifier.getAccountVerifier(1, this.targetBranch);
        return result;
    }
}
