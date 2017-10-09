/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.google.common.base.Strings;
import java.util.Arrays;

/**
 *
 * @author roger
 */
public class BBDigitVerifier {

    /// <summary>Valida conta corrente
    /// </summary>
    ///<param name="banco">Passar o número (código) do banco</param>
    /// <param name="conta">Passar o número da conta sem o digito</param>
    /// <param name="digito">Passar o digito da conta</param>
    static public boolean checkAccountVerifier(String branchId, int digToValidate) {
        String pesoBradesco = "32765432";
        String pesoSantander = "97310097131973";
        String pesoBB = "98765432";
        String pesoHSBC = "8923456789";

        int cycleSum = 0;
        int remainder;
        int calcDigit;
        int x = pesoBB.length() - branchId.length();
        branchId = Strings.padStart(branchId, 8, '0');

        for (int i = 0; i < pesoBB.length(); i++) {
            if (i < x) {
                cycleSum += Integer.parseInt((pesoBB.substring(i, i + 1))) * 0;
            } else {
                cycleSum += Integer.parseInt((pesoBB.substring(i, i + 1))) * Integer.parseInt(branchId.substring(i, i + 1).toString());
            }
        }

        remainder = cycleSum % 11;
        calcDigit = 11 - remainder;
        if (calcDigit == 11) {
            calcDigit = 0;
        } else if (calcDigit == 10) {
            calcDigit = 20;
        }

        if (digToValidate == 0 && calcDigit == 10) {
            return true;
        } else {
            return digToValidate == calcDigit;
        }

    }

    /// <summary>Valida agência bancaria
    /// </summary>
    ///<param name="banco">Passar o número (código) do banco</param>
    /// <param name="agencia">Passar o número da agência sem o digito</param>
    /// <param name="digito">Passar o digito da conta</param>
    static public boolean validateBranch(String branchId, int informedDigit) {
        String pesoBradesco = "5432";
        String pesoBB = "5432";

        int cycleSum = 0;
        int remaind;
        int calcDigit;

        for (int i = 0; i < pesoBB.length(); i++) {
            cycleSum += Integer.parseInt(pesoBB.substring(i, i + 1)) * Integer.parseInt(branchId.substring(i, i + 1));
        }

        remaind = cycleSum % 11;
        calcDigit = 11 - remaind;

        if (calcDigit == 11) {
            calcDigit = 0;
        } else if (calcDigit == 10) {
            calcDigit = 20;
        }

        return (informedDigit == calcDigit);
    }

    static int getAccountVerifier(String branchId) {
        /*
        String pesoBradesco = "32765432";
        String pesoSantander = "97310097131973";
        String pesoHSBC = "8923456789";
         */
        String pesoBB = "98765432";

        int cycleSum = 0;
        int remaind;
        int calcDigit;
        int x;

        x = pesoBB.length() - branchId.length();
        branchId = Strings.padStart(branchId, 8, '0');

        for (int i = 0; i < pesoBB.length(); i++) {
            if (i < x) {
                cycleSum += Integer.parseInt((pesoBB.substring(i, i + 1))) * 0;
            } else {
                cycleSum += Integer.parseInt((pesoBB.substring(i, i + 1))) * Integer.parseInt(branchId.substring(i, i + 1));
            }
        }

        remaind = cycleSum % 11;
        calcDigit = 11 - remaind;
        if (calcDigit == 11) {
            calcDigit = 0;
        } else if (calcDigit == 10) {
            calcDigit = 20;
        }

        if (calcDigit == 10) {
            return 0;
        } else {
            return calcDigit;
        }
    }

    static int getBranchVeririfer(String branchId) {
        //String pesoBradesco = "5432";
        String pesoBB = "5432";

        int cycleSum = 0;
        int remaind;
        int calcDigit;

        for (int i = 0; i < pesoBB.length(); i++) {
            cycleSum += Integer.parseInt(pesoBB.substring(i, i + 1)) * Integer.parseInt(branchId.substring(i, i + 1));
        }

        remaind = cycleSum % 11;
        calcDigit = 11 - remaind;

        if (calcDigit == 11) {
            calcDigit = 0;
        } else if (calcDigit == 10) {
            calcDigit = 20;
        }
        return calcDigit;
    }

    static String regularTokenizer(String clearString, String tokenStr, int leapSize, boolean normalDirection) {
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

    /**
     * Pads a String <code>s</code> to take up <code>n</code> characters, padding with char <code>c</code>
     *
     * @param s
     * @param n
     * @param c
     * @return <code>null</code> if passed a <code>null</code> String.
     */
    public static String padRightString(String s, int n, char c) {
        //TODO exportar para biblioteca ( ver Strings.padEnd(s, n, c) antes )
        if (s == null) {
            return s;
        }
        int add = n - s.length(); // may overflow int size... should not be a problem in real life
        if (add <= 0) {
            return s;
        }
        StringBuilder str = new StringBuilder(s);
        char[] ch = new char[add];
        Arrays.fill(ch, c);
        str.append(ch);
        return str.toString();
    }

    /**
     * Pads a String <code>s</code> to take up <code>n</code> characters, padding with char <code>c</code>
     *
     * @param s
     * @param n
     * @param c
     * @return <code>null</code> if passed a <code>null</code> String.
     */
    public static String padLeftString(String s, int n, char c) {
        //TODO exportar para biblioteca ( ver Strings.padStart(s, n, c) antes )
        if (s == null) {
            return s;
        }
        int add = n - s.length(); // may overflow int size... should not be a problem in real life
        if (add <= 0) {
            return s;
        }
        StringBuilder str = new StringBuilder(s);
        char[] ch = new char[add];
        Arrays.fill(ch, c);
        str.insert(0, ch);
        return str.toString();
    }

}
