/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.domain.data.MessageSetType;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessage;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Rogerlais
 */
public class OFXCreditCardBilling {

    private final String sourceFile;
    private ResponseEnvelope OFXContent;

    public OFXCreditCardBilling(String sourceFilename) {
        this.sourceFile = sourceFilename;
    }

    public String getDefaultExportFilename() {
        File f = new File(this.sourceFile);
        String ret = f.getParentFile().getAbsolutePath() + f.getName().replace(".ofx", "_upd.txt");
        return ret;
    }

    public void exportTo(String destFilename) throws OFXException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try {
            try (
                    FileInputStream fis = new FileInputStream(this.sourceFile);
                    Reader fr = new InputStreamReader(fis, "windows-1252") //Requerido para interpertrar corretamente os dados
                    ) //allocation
            {
                AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);  //Adapter para serialização
                this.OFXContent = (ResponseEnvelope) a.unmarshal(fr);
            }
        } catch (Exception e) {
            throw new OFXException("Erro lendo OFX de origem:" + e.getLocalizedMessage());
        }
        //(CreditCardStatementResponseTransaction)
        List<ResponseMessage> accountList = this.OFXContent.getMessageSet(MessageSetType.creditcard).getResponseMessages();
        for (Iterator<ResponseMessage> it = accountList.iterator(); it.hasNext();) {
            CreditCardStatementResponseTransaction responseMessage = (CreditCardStatementResponseTransaction) it.next();
            List<Transaction> transList = responseMessage.getMessage().getTransactionList().getTransactions();
            for (Transaction transaction : transList) {
                sb.append(transaction.getDatePosted());
                sb.append("\t");
                sb.append(transaction.getAmount()); //inverter sinal para obter significado
                sb.append("\t");
                sb.append(transaction.getMemo());
                sb.append("\r\n");
                'saida a corrigir abaixo'
                /*

                Mon Aug 21 00:00:01 GMT-03:00 2017	3013.54	SALDO ANTERIOR
                Sat Nov 25 00:00:01 GMT-03:00 2017	160.55	CARREFOUR.COM 10/1010/10
                Fri Jan 13 00:00:01 GMT-03:00 2017	18.14	EXTRA.COM 8/88/8
                Sat Mar 18 00:00:01 GMT-03:00 2017	318.98	WALMART COM ELETRONICO 6/96/9
                Thu May 04 00:00:01 GMT-03:00 2017	129.89

                 */
            }
        }
        if (sb.length() > 1) {
            try (PrintWriter out = new PrintWriter(destFilename)) {
                out.println(sb.toString());
            }
        }

        /*
        CreditCardResponseMessageSet items = (CreditCardResponseMessageSet) this.OFXContent.getMessageSet(MessageSetType.creditcard);
        List<ResponseMessage> transList = this.OFXContent.getMessageSet(MessageSetType.creditcard).getResponseMessages();
        for (ResponseMessage item : transList) {
            item.getResponseMessageName();
        }
         */
    }

}
