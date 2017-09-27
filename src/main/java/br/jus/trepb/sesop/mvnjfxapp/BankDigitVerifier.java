/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

/**
 *
 * @author roger
 */
public class BankDigitVerifier {

    /// <summary>Valida conta corrente
    /// </summary>
    ///<param name="banco">Passar o número (código) do banco</param>
    /// <param name="conta">Passar o número da conta sem o digito</param>
    /// <param name="digito">Passar o digito da conta</param>
    static public boolean checkAccountVerifier(int banco, String conta, int digito) {
        String pesoBradesco = "32765432";
        String pesoSantander = "97310097131973";
        String pesoBB = "98765432";
        String pesoHSBC = "8923456789";

        int soma = 0;
        int resto = 0;
        int Digito = 0;
        int x = 0;

        switch (banco) {
            case 1: //BCO DO BRASIL S/A
                x = pesoBB.length() - conta.length();
                conta = String.format("%08s", conta);

                for (int i = 0; i < pesoBB.length(); i++) {
                    if (i < x) {
                        soma += Integer.parseInt((pesoBB.substring(i, i + 1))) * 0;
                    } else {
                        soma += Integer.parseInt((pesoBB.substring(i, i + 1))) * Integer.parseInt(conta.substring(i, i + 1).toString());
                    }
                }

                resto = soma % 11;
                Digito = 11 - resto;
                if (Digito == 11) {
                    Digito = 0;
                } else if (Digito == 10) {
                    Digito = 20;
                }

                if (digito == 0 && Digito == 10) {
                    return true;
                } else if (digito != Digito) {
                    return false;
                } else {
                    return true;
                }
            case 33: //SANTANDER
            /*
                    x = pesoSantander.Length - conta.Length;

                    for (int j = 0; j < x; j++)
                    {
                        conta = conta.Insert(0, "0");
                    }

                    if (x == 0) { x = conta.Length; }

                    for (int i = 0; i < pesoSantander.Length; i++)
                    {
                        if (i < x && pesoSantander.Length != x)
                        {
                            soma += int.Parse(pesoSantander.Substring(i, 1)) * 0;
                        }
                        else
                        {
                            soma += int.Parse(pesoSantander.Substring(i, 1)) * int.Parse(conta.Substring(i, 1).ToString());
                        }
                    }

                    resto = soma % 10;
                    if (resto == 0)
                    {
                        Digito = 0;
                    }
                    else
                    {
                        Digito = 10 - resto;
                    }

                    if (digito != Digito)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }

                    break;
             */
            case 104: //CAIXA ECONOMICA FEDERAL
                break;
            case 237: //BRADESCO S/A
            /*
                    x = pesoBradesco.Length - conta.Length;
                    for (int j = 0; j < x; j++)
                    {
                        conta = conta.Insert(0, "0");
                    }

                    for (int i = 0; i < pesoBradesco.Length; i++)
                    {
                        if (i < x)
                        {
                            soma += int.Parse(pesoBradesco.Substring(i, 1)) * 0;
                        }
                        else
                        {
                            soma += int.Parse(pesoBradesco.Substring(i, 1)) * int.Parse(conta.Substring(i, 1).ToString());
                        }
                    }

                    resto = soma % 11;

                    if (resto == 0)
                    {
                        Digito = 0;
                    }
                    else
                    {
                        Digito = 11 - resto;
                    }
                    if (Digito == 10 || Digito == 11)
                    {
                        Digito = 0;
                    }

                    if (digito != Digito)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }

                    break;
             */
            case 341: //BCO ITAU S/A
                break;
            case 399: //HSBC BANK BRASIL S.A.
                /*
                    x = pesoHSBC.Length - conta.Length;

                    for (int i = 0; i < conta.Length; i++)
                    {
                        soma += int.Parse(pesoHSBC.Substring(i, 1)) * int.Parse(conta.Substring(i, 1));
                    }

                    Digito = soma % 11;

                    if (Digito == digito)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                 */
                break;
            default:
                return false;
        }
        return false;
    }

    /// <summary>Valida agência bancaria
    /// </summary>
    ///<param name="banco">Passar o número (código) do banco</param>
    /// <param name="agencia">Passar o número da agência sem o digito</param>
    /// <param name="digito">Passar o digito da conta</param>
    static public boolean validateBranch(String banco, String agencia, int digito) {
        String pesoBradesco = "5432";
        String pesoBB = "5432";

        int soma = 0;
        int resto = 0;
        int Digito = 0;

        switch (banco) {
            case "001": //Banco do Brasil
                for (int i = 0; i < pesoBB.length(); i++) {
                    soma += Integer.parseInt(pesoBB.substring(i, i + 1)) * Integer.parseInt(agencia.substring(i, i + 1));
                }

                resto = soma % 11;
                Digito = 11 - resto;

                if (Digito == 11) {
                    Digito = 0;
                } else if (Digito == 10) {
                    Digito = 20;
                }

                if (digito != Digito) {
                    return false;
                } else {
                    return true;
                }
            case "237": //Bradesco
            /*
                for (int i = 0; i < pesoBradesco.Length; i++) {
                    soma += int.Parse
                    (pesoBradesco.Substring(i, 1)) * int.Parse
                    (agencia.Substring(i, 1)

                );
                    }

                    resto = soma % 11;
                Digito = 11 - resto;

                if (digito != Digito) {
                    return false;
                } else {
                    return true;
                }
             */
            default:
                return false;
        }
    }

    static int getAccountVerifier(int banco, String conta) {
        String pesoBradesco = "32765432";
        String pesoSantander = "97310097131973";
        String pesoBB = "98765432";
        String pesoHSBC = "8923456789";

        int soma = 0;
        int resto = 0;
        int Digito = 0;
        int x = 0;

        switch (banco) {
            case 1: //BCO DO BRASIL S/A
                x = pesoBB.length() - conta.length();
                conta = String.format("%08s", conta);

                for (int i = 0; i < pesoBB.length(); i++) {
                    if (i < x) {
                        soma += Integer.parseInt((pesoBB.substring(i, i + 1))) * 0;
                    } else {
                        soma += Integer.parseInt((pesoBB.substring(i, i + 1))) * Integer.parseInt(conta.substring(i, i + 1).toString());
                    }
                }

                resto = soma % 11;
                Digito = 11 - resto;
                if (Digito == 11) {
                    Digito = 0;
                } else if (Digito == 10) {
                    Digito = 20;
                }

                if (Digito == 10) {
                    return 0;
                } else {
                    return Digito;
                }
            case 33: //SANTANDER
            /*
                    x = pesoSantander.Length - conta.Length;

                    for (int j = 0; j < x; j++)
                    {
                        conta = conta.Insert(0, "0");
                    }

                    if (x == 0) { x = conta.Length; }

                    for (int i = 0; i < pesoSantander.Length; i++)
                    {
                        if (i < x && pesoSantander.Length != x)
                        {
                            soma += int.Parse(pesoSantander.Substring(i, 1)) * 0;
                        }
                        else
                        {
                            soma += int.Parse(pesoSantander.Substring(i, 1)) * int.Parse(conta.Substring(i, 1).ToString());
                        }
                    }

                    resto = soma % 10;
                    if (resto == 0)
                    {
                        Digito = 0;
                    }
                    else
                    {
                        Digito = 10 - resto;
                    }

                    if (digito != Digito)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }

                    break;
             */
            case 104: //CAIXA ECONOMICA FEDERAL
                break;
            case 237: //BRADESCO S/A
            /*
                    x = pesoBradesco.Length - conta.Length;
                    for (int j = 0; j < x; j++)
                    {
                        conta = conta.Insert(0, "0");
                    }

                    for (int i = 0; i < pesoBradesco.Length; i++)
                    {
                        if (i < x)
                        {
                            soma += int.Parse(pesoBradesco.Substring(i, 1)) * 0;
                        }
                        else
                        {
                            soma += int.Parse(pesoBradesco.Substring(i, 1)) * int.Parse(conta.Substring(i, 1).ToString());
                        }
                    }

                    resto = soma % 11;

                    if (resto == 0)
                    {
                        Digito = 0;
                    }
                    else
                    {
                        Digito = 11 - resto;
                    }
                    if (Digito == 10 || Digito == 11)
                    {
                        Digito = 0;
                    }

                    if (digito != Digito)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }

                    break;
             */
            case 341: //BCO ITAU S/A
                break;
            case 399: //HSBC BANK BRASIL S.A.
                /*
                    x = pesoHSBC.Length - conta.Length;

                    for (int i = 0; i < conta.Length; i++)
                    {
                        soma += int.Parse(pesoHSBC.Substring(i, 1)) * int.Parse(conta.Substring(i, 1));
                    }

                    Digito = soma % 11;

                    if (Digito == digito)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                 */
                break;
            default:
                return -1;
        }
        return -1;
    }

}
