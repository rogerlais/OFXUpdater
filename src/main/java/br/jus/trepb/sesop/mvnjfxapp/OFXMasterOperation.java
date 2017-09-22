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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.Calendar;
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
     * Validate the window time from both files Both files must have a list of transactions at a only one month
     */
    private void checkWindowTime() throws IOException, OFXParseException, Exception {

        ZoneId zid = ZoneId.systemDefault();

        LocalDateTime masterStartDate = LocalDateTime.ofInstant(master.getFirstTransactionTime().toInstant(), zid);
        LocalDateTime masterEndDate = LocalDateTime.ofInstant(master.getLastTransactionTime().toInstant(), zid);
        LocalDateTime slaveStartDate = LocalDateTime.ofInstant(master.getFirstTransactionTime().toInstant(), zid);
        LocalDateTime slaveEndDate = LocalDateTime.ofInstant(master.getLastTransactionTime().toInstant(), zid);

        int masterStartMonth = masterStartDate.plusDays(1).getMonthValue();
        int masterEndMonth = masterEndDate.minusDays(1).getMonthValue();
        int slaveStartMonth = slaveStartDate.plusDays(1).getMonthValue();
        int slaveEndMonth = slaveEndDate.minusDays(1).getMonthValue();

        // TODO validar a janela de tempo dos arquivos(único mês de operação)
        if ((masterEndMonth != masterStartMonth) || (slaveEndMonth != slaveStartMonth) || (masterStartMonth != slaveEndMonth)) {
            throw new Exception("janela de tempo incompatível para os arquivos informados");
        }

    }

    public OFXMasterOperation(OFXFileHelper master, OFXFileHelper slave) throws Exception {
        this.master = master;
        this.slave = slave;
        this.checkIntegrity();
    }

    protected void checkIntegrity() throws OFXParseException, Exception {
        // TODO Colocar todos os casos de checagem de integridade para o par de OFX informados
        this.checkWindowTime(); //Todas as transações dentro do mesmo mês
        this.checkOwners();
    }

    /**
     * Debug only method to track the process
     *
     * @param filename
     * @throws IOException
     * @throws OFXParseException
     */
    protected void exportCSVTransactionPairs(String filename) throws OFXException {
        StringBuilder result = new StringBuilder();
        result.append("ACCOUNT-MASTER,ACCOUNT-SLAVE,OFX-DATE-MASTER,OFX-DATE-SLAVE,TRNTYPE-MASTER,TRNTYPE-SLAVE,"
                + "DTPOSTED-MASTER,DTPOSTED-SLAVE,TRNAMT-MASTER,TRNAMT-SLAVE,FITID-MASTER,FITID-SLAVE,CHECKNUM-MASTER,"
                + "CHECKNUM-SLAVE,REFNUM-MASTER,REFNUM-SLAVE,MEMO-MASTER,MEMO-SLAVE\n\r");
        List<Transaction> masterList = this.master.getTransactionList();
        for (Iterator itT = masterList.iterator(); itT.hasNext();) {
            Transaction mTrans = (Transaction) itT.next();
            if (mTrans.getCheckNumber().endsWith(GlobalConfig.OLD_SLAVE_ACCOUNT)) {
                Transaction sTrans = this.slave.getMatchTransaction(mTrans);
                if (sTrans != null) {
                    result.append(mTrans.getBankAccountTo() + ","); //ACCOUNT-MASTER
                   /*
                        + "ACCOUNT-SLAVE"
                            + "OFX-DATE-MASTER"
                            + "OFX-DATE-SLAVE"
                            + "TRNTYPE-MASTER"
                            + "TRNTYPE-SLAVE"
                            + "DTPOSTED-MASTER"
                            + "DTPOSTED-SLAVE"
                            + "TRNAMT-MASTER"
                            + "TRNAMT-SLAVE"
                            + "FITID-MASTER"
                            + "FITID-SLAVE"
                            + "CHECKNUM-MASTER"
                            + "CHECKNUM-SLAVE"
                            + "REFNUM-MASTER"
                            + "REFNUM-SLAVE"
                            + "MEMO-MASTER"
                            + "MEMO-SLAVE\n\r"
                     */
                } else {
                    throw new OFXException("Transação não pareada encontrada: " + mTrans.getId());
                }
            }
        }
    }

    private void checkOwners() {
        //TODO roger para master e MV para slave, numero de agencia e conta correspondentes
    }
}
