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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Rogerlais
 */
public class OFXMasterOperation {

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
    private final OFXFileHelper master;
    private final OFXFileHelper slave;

    /**
     * Show OFX content
     * @param ofxFile
     * @throws FileNotFoundException
     * @throws IOException
     * @throws OFXParseException
     * Source:  
     * @see <a href = https://comunidadecc.blogspot.com.br/2010/08/lendo-arquivo-ofx-com-ofx4j.html>
     */
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

    /**
     * Validate the window time from both files
     */
    public void checkWindowTime() throws IOException, OFXParseException {
        Date masterStartDate = this.master.getBankSetRerponseTransaction(0).getMessage().getTransactionList().getStart();
        Date masterEndDate = this.master.getBankSetRerponseTransaction(0).getMessage().getTransactionList().getEnd();

        Date slaveStartDate = this.slave.getBankSetRerponseTransaction(0).getMessage().getTransactionList().getStart();
        Date slaveEndDate = this.slave.getBankSetRerponseTransaction(0).getMessage().getTransactionList().getEnd();

        GregorianCalendar c = GregorianCalendar.from(zdt);
        c.setTime(slaveEndDate);

        // TODO validar a janela de tempo dos arquivos
        if (masterStartDate.get) {

        }

    }

    public OFXMasterOperation(OFXFileHelper master, OFXFileHelper slave) {
        this.master = master;
        this.slave = slave;
        this.checkIntegrity();
    }

    private void checkIntegrity() {
        this.checkWindowTime();
    }
}
