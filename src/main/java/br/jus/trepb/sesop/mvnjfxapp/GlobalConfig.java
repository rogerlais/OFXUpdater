/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

/**
 *
 * @author Rogerlais
 */
public class GlobalConfig {

    public static final boolean DEBUG = true;

    //Dados originais conta master
    public static final String OLD_MASTER_BRANCH = "3612";

    public static final String OLD_MASTER_BRANCH_DV = "9";

    public static final String OLD_MASTER_ACCOUNT = "55898";

    public static final String OLD_MASTER_ACCOUNT_DV = "2";

    public static final String MASTER_ACCOUNT_ALIAS = "CONTA AMOR";

    public static final String MASTER_CASH_OUT_ACCOUNT = "9516565";

    //Dados originais conta slave
    public static final String OLD_SLAVE_BRANCH = "3501";

    public static final String OLD_SLAVE_BRANCH_DV = "7";

    public static final String OLD_SLAVE_ACCOUNT = "21038";

    public static final String OLD_SLAVE_ACCOUNT_DV = "2";

    public static final String SLAVE_ACCOUNT_ALIAS = "CONTA PAIXAO";

    public static final String SLAVE_CASH_OUT_ACCOUNT = "7977137";

    //Dados para a conta simulada master
    public static final String NEW_MASTER_BRANCH = "123";

    public static final String NEW_MASTER_BRANCH_DV = "6";

    public static final String NEW_MASTER_ACCOUNT = "495";

    public static final String NEW_MASTER_ACCOUNT_DV = "2";

    //Dados para a conta simulada slave
    public static final String NEW_SLAVE_ACCOUNT = "321";

    public static final String NEW_SLAVE_ACCOUNT_DV = "2";

    public static final String NEW_SLAVE_BRANCH = "6682";

    public static final String NEW_SLAVE_BRANCH_DV = "6";

    public static final int ACCOUNT_BB_LENGTH = 7;  //tamanho da conta do BB

    public static final int REFNUM_TRANSFER_LENGTH = 19;
    public static final int REFNUM_TRANSFER_SAVINGS = 17;

    public static final String CC_AMEX_MASTER_ACCOUNT_SUFIX = "X73008";

    public static String trimChar(String str, char c) {
        //TODO: Levar para biblioteca
        int index = 0;
        int maxL = str.length();
        String result = "";
        while (index < maxL) {
            if (str.charAt(index) == c) {
                index++;
            } else {
                result = str.substring(index);
                return result;
            }
        }
        return result;
    }

}
