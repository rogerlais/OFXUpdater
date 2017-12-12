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
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import com.webcohesion.ofx4j.io.AggregateMarshaller;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import com.webcohesion.ofx4j.io.OFXParseException;
import com.webcohesion.ofx4j.io.v1.OFXV1Writer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        Date tresholdDate = getTresholdDate(tresholdDay); //usa this.OFXContent para identificar dia(info HC)
        List<ResponseMessage> accountList = this.OFXContent.getMessageSet(MessageSetType.creditcard).getResponseMessages();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        sb.append("Data limite calculada = " + fmt.format(tresholdDate));
        sb.append("\r\n");
        for (Iterator<ResponseMessage> it = accountList.iterator(); it.hasNext();) {
            CreditCardStatementResponseTransaction responseMessage = (CreditCardStatementResponseTransaction) it.next();
            List<Transaction> transList = responseMessage.getMessage().getTransactionList().getTransactions();
            for (Transaction transaction : transList) {
                sb.append(fmt.format(transaction.getDatePosted()));
                sb.append("\t");
                Double ammount = -1 * transaction.getAmount();//inverter sinal para obter significado
                sb.append(ammount.toString().replace(".", ",")); //usar virgula para importador não frescar
                sb.append("\t");
                String desc = this.getTranslatedMemo(transaction, fmt, tresholdDate);
                sb.append(desc);
                sb.append("\r\n");
            }
        }
        if (sb.length() > 1) {
            try (PrintWriter out = new PrintWriter(destFilename)) {
                out.println(sb.toString());
            }
        }
    }

    private Date getTresholdDate(int decrementDays) throws OFXException {
        //Abre arquivo e dependendo do emissor (BB ou Amex) infere uma data de compra ótima.
        //Quando houver entrada posterior a registrada no importador(info HC ), alerta com prefixo no mesmo
        CreditCardResponseMessageSet response = (CreditCardResponseMessageSet) this.OFXContent.getMessageSet(MessageSetType.creditcard);
        CreditCardStatementResponseTransaction masterTransList = (CreditCardStatementResponseTransaction) response.getResponseMessages().get(0);  //assume 0 = primeiro e master
        String AN = masterTransList.getMessage().getAccount().getAccountNumber();
        Date resultDate = null;
        if (AN.endsWith(GlobalConfig.CC_AMEX_MASTER_ACCOUNT_MASK)) {  //Amex
            resultDate = masterTransList.getMessage().getTransactionList().getStart();  //para BB inicia em data anterior as vezes
        } else {  //Ourocard
            if (AN.contains(GlobalConfig.CC_BB_MASTER_ACCOUNT_MASK)) {
                //BB ourocard
                resultDate = masterTransList.getMessage().getLedgerBalance().getAsOfDate();
            } else {
                throw new OFXException("Cartão não localizado na lista de cartões conhecidos para cáculo da data limite");
            }
        }
        if (resultDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(resultDate);
            if (decrementDays == 0) {
                decrementDays = -40;
            } else {
                decrementDays -= 30;  //retrocede sempre 30 além do informado(máxima variação 30 + 30 = -60 dias)
            }
            cal.add(Calendar.DATE, decrementDays);
            resultDate = cal.getTime();
        }
        return resultDate;
    }

    private String getTranslatedMemo(Transaction transaction, SimpleDateFormat fmt, Date tresholdDate) {
        String desc = transaction.getMemo();
        if (desc.contains("/")) {  //Parcelada, será lançada na mão mesmo
            return desc;
        } else {
            if (tresholdDate.compareTo(transaction.getDatePosted()) > 0) {
                desc += "RefDate=" + fmt.format(transaction.getDatePosted());
            }
        }
        return desc;
    }

    public void writeTo(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            Writer out = new OutputStreamWriter(fos, "windows-1252"); //Requerido para interpretrar corretamente os dados
            OFXV1Writer ofxWriter = new OFXV1Writer(out);
            // todo para AggregateMarshaller.writeAggregateAttributes() necessario fork para forçar identação e fechamento de tag

            ofxWriter.setWriteAttributesOnNewLine(true);
            ofxWriter.setAllwaysCloseElement(true);
            ofxWriter.setTabLength(3);
            ofxWriter.setWriteValuesOnNewLine(false);
            try {
                AggregateMarshaller a = new AggregateMarshaller();  //Adapter para serialização
                a.setConversion(new BBOFXStringConvertion());  //Adjuste to Bank of Bostil output
                a.marshal(this.OFXContent, ofxWriter);
            } finally {
                ofxWriter.close();
            }
        }
    }
    /**
     * Read the input file to internal memory Split the content into several parts and validate your cardinality, be only one bank
     * and only banking elements
     *
     * @throws IOException
     * @throws OFXParseException
     */
    public void read() throws IOException, OFXParseException {
        try (
                FileInputStream fis = new FileInputStream(this.sourceFile);
                Reader fr = new InputStreamReader(fis, "windows-1252") //Requerido para interpertrar corretamente os dados
                ) //allocation
        {
            AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);  //Adapter para serialização
            this.OFXContent = (ResponseEnvelope) a.unmarshal(fr);
        }
    }

}
