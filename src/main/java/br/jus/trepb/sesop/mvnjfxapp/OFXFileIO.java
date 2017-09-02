/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.domain.data.MessageSetType;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.banking.BankingResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.domain.data.signon.SignonResponse;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import com.webcohesion.ofx4j.io.OFXParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import static jdk.nashorn.internal.objects.NativeJava.type;

/**
 *
 * @author Rogerlais
 */
public class OFXFileIO {

    static public void displayContent(OFXFileHelper ofxH) throws IOException, OFXParseException {

        ResponseEnvelope re = ofxH.getOFXContent();

        //objeto contendo informações como instituição financeira, idioma, data da conta.
        SignonResponse sr = re.getSignonResponse();

        //como não existe esse get "BankStatementResponse bsr = re.getBankStatementResponse();"
        //fiz esse codigo para capturar a lista de transações
        MessageSetType type = MessageSetType.banking;
        ResponseMessageSet message = re.getMessageSet(type);  //pega apenas o conjunto para a classe filtrada acima

        if (message != null) {
            List bank = ((BankingResponseMessageSet) message).getStatementResponses();
            for (Iterator it = bank.iterator(); it.hasNext();) {
                BankStatementResponseTransaction b = (BankStatementResponseTransaction) it.next();
                System.out.println("cc: " + b.getMessage().getAccount().getAccountNumber());
                System.out.println("ag: " + b.getMessage().getAccount().getBranchId());
                System.out.println("balanço final: " + b.getMessage().getLedgerBalance().getAmount());
                System.out.println("dataDoArquivo: " + b.getMessage().getLedgerBalance().getAsOfDate());
                List list = b.getMessage().getTransactionList().getTransactions();
                System.out.println("TRANSAÇÕES\n");
                for (Iterator itT = list.iterator(); itT.hasNext();) {
                    Transaction transaction = (Transaction) itT.next();
                    System.out.println("TYPE:	" + transaction.getTransactionType().name());
                    System.out.println("DTPOSTED:	" + transaction.getDatePosted());
                    System.out.println("TRNAMT:	" + transaction.getAmount());
                    System.out.println("FITID:	" + transaction.getId());  //BUSCAR DIFERENÇA DTPOSTED
                    System.out.println("CHECKNUM:	" + transaction.getCheckNumber());
                    System.out.println("REFNUM:	" + transaction.getReferenceNumber());
                    System.out.println("MEMO:	" + transaction.getMemo());
                    System.out.println("-------------------------------------");
                }
            }
        }
        System.out.println("FIM DA EXIBIÇÃO!");
    }

    static public String toCSVString(OFXFileHelper ofxH) throws IOException, OFXParseException {
        ResponseEnvelope re = ofxH.getOFXContent();
        //objeto contendo informações como instituição financeira, idioma, data da conta.
        SignonResponse sr = re.getSignonResponse();
        //como não existe esse get "BankStatementResponse bsr = re.getBankStatementResponse();"
        //fiz esse codigo para capturar a lista de transações
        MessageSetType type = MessageSetType.banking;
        ResponseMessageSet message = re.getMessageSet(type);  //pega apenas o conjunto para a classe filtrada acima
        if (message != null) {
            List bank = ((BankingResponseMessageSet) message).getStatementResponses();
            StringBuilder result = new StringBuilder();
            result.append("ACCOUNT,OFX-DATE,TRNTYPE,DTPOSTED,TRNAMT,FITID,CHECKNUM,REFNUM,MEMO\n\r");
            for (Iterator it = bank.iterator(); it.hasNext();) {
                BankStatementResponseTransaction b = (BankStatementResponseTransaction) it.next();
                List list = b.getMessage().getTransactionList().getTransactions();
                for (Iterator itT = list.iterator(); itT.hasNext();) {
                    Transaction transaction = (Transaction) itT.next();                    
                    result.append(toCsvRow(b.getMessage().getAccount(), sr.getTimestamp(), transaction));
                }
            }
            return result.toString();
        } else {
            return "";
        }
    }

    static private String toCsvRow(BankAccountDetails bankDetail , Date ts, Transaction transaction) {
        String result = "";        
        result += bankDetail.getAccountNumber() +  ",";
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss[ZZ:zzz]" );
        //sdf.setTimeZone(TimeZone.getTimeZone("BRT"));
        result += sdf.format(ts)+  ",";
        result += transaction.getTransactionType().name() + ",";
        result += transaction.getDatePosted() + ",";
        result += transaction.getAmount() + ",";
        result += transaction.getId() + ",";  //BUSCAR DIFERENÇA DTPOSTED
        result += transaction.getCheckNumber() + ",";
        result += "\"" + transaction.getReferenceNumber() + "\",";
        result += transaction.getMemo() + "\r\n";
        return result;
    }

    public void displayContentFromFile(File ofxFile) throws FileNotFoundException, IOException, OFXParseException {
        AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);
        FileInputStream fis = new FileInputStream(ofxFile);
        Reader fr = new InputStreamReader(fis, "Windows-1252");
        ResponseEnvelope re = (ResponseEnvelope) a.unmarshal(fr);

        //objeto contendo informações como instituição financeira, idioma, data da conta.
        SignonResponse sr = re.getSignonResponse();

        //como não existe esse get "BankStatementResponse bsr = re.getBankStatementResponse();"
        //fiz esse codigo para capturar a lista de transações
        MessageSetType type = MessageSetType.banking;
        ResponseMessageSet message = re.getMessageSet(type);

        if (message != null) {
            List bank = ((BankingResponseMessageSet) message).getStatementResponses();
            for (Iterator it = bank.iterator(); it.hasNext();) {
                BankStatementResponseTransaction b = (BankStatementResponseTransaction) it.next();
                System.out.println("cc: " + b.getMessage().getAccount().getAccountNumber());
                System.out.println("ag: " + b.getMessage().getAccount().getBranchId());
                System.out.println("balanço final: " + b.getMessage().getLedgerBalance().getAmount());
                System.out.println("dataDoArquivo: " + b.getMessage().getLedgerBalance().getAsOfDate());
                List list = b.getMessage().getTransactionList().getTransactions();
                System.out.println("TRANSAÇÕES\n");
                for (Iterator itT = list.iterator(); itT.hasNext();) {
                    Transaction transaction = (Transaction) itT.next();
                    System.out.println("TYPE:	" + transaction.getTransactionType().name());
                    System.out.println("DTPOSTED:	" + transaction.getDatePosted());
                    System.out.println("TRNAMT:	" + transaction.getAmount());
                    System.out.println("FITID:	" + transaction.getId());  //BUSCAR DIFERENÇA DTPOSTED
                    System.out.println("CHECKNUM:	" + transaction.getCheckNumber());
                    System.out.println("REFNUM:	" + transaction.getReferenceNumber());
                    System.out.println("MEMO:	" + transaction.getMemo());
                    System.out.println("-------------------------------------");
                }
            }
        }
        System.out.println("FIM DA EXIBIÇÃO!");
    }
}
