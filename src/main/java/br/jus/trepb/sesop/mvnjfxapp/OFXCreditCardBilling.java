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
import java.text.SimpleDateFormat;
import java.util.Date;
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
        String ret = f.getParentFile().getAbsolutePath() + "\\" + f.getName().replace(".ofx", "_upd.txt");
        return ret;
    }

    public void exportTo(String destFilename, int tresholdDay) throws OFXException, FileNotFoundException {
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
        Date tresholdDate = getTresholdDate(); //usa this.OFXContent para identificar dia(info HC)
        List<ResponseMessage> accountList = this.OFXContent.getMessageSet(MessageSetType.creditcard).getResponseMessages();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        for (Iterator<ResponseMessage> it = accountList.iterator(); it.hasNext();) {
            CreditCardStatementResponseTransaction responseMessage = (CreditCardStatementResponseTransaction) it.next();
            List<Transaction> transList = responseMessage.getMessage().getTransactionList().getTransactions();
            for (Transaction transaction : transList) {
                sb.append(fmt.format(transaction.getDatePosted()));
                sb.append("\t");
                Double ammount = -1 * transaction.getAmount();//inverter sinal para obter significado
                sb.append(ammount.toString().replace(".", ",")); //usar virgula para importador não frescar
                sb.append("\t");
                String desc = this.getTranslatedMemo(transaction, tresholdDate);
                sb.append("\r\n");
            }
        }
        if (sb.length() > 1) {
            try (PrintWriter out = new PrintWriter(destFilename)) {
                out.println(sb.toString());
            }
        }
    }

    private Date getTresholdDate() {
        //Abre arquivo e dependendo do emissor (BB ou Amex) infere uma data de compra ótima.
        //Quando houver entrada posterior a registrada no importador(info HC ), alerta com prefixo no mesmo
        return null;
    }

    private String getTranslatedMemo(Transaction transaction, Date tresholdDate) {
        String desc = transaction.getMemo();
        if (desc.contains("/")) {  //Parcelada, será lançada na mão mesmo
            return desc;
        } else {
            if (tresholdDate.compareTo(transaction.getDatePosted()) >= 1) {

            } else {

            }
        }
        return null;
    }

}
