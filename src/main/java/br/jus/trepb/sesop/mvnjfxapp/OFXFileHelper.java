/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.domain.data.MessageSetType;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessage;
import com.webcohesion.ofx4j.domain.data.ResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.banking.BankingResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.domain.data.signon.SignonResponse;
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author roger
 */
public class OFXFileHelper {

    /**
     * @return the inputFile
     */
    public File getInputFile() {
        return inputFile;
    }

    /**
     * Return default output full filename (same folder from input file)
     *
     * @return default output full filename
     */
    public String getStdOutputFilename() {
        //Prepara nome do ofx de saida para o master
        return FilenameUtils.removeExtension(this.getInputFile().getAbsolutePath()) + "_upd.ofx";
    }

    /**
     * Calculate the first transaction at list
     *
     * @return first timely transaction
     * @throws OFXException
     */
    public Date getFirstTransactionTime() throws OFXException {
        Date result = new Date(Long.MAX_VALUE);
        List<Transaction> transList = getTransactionList();
        for (Iterator itT = transList.iterator(); itT.hasNext();) {
            Transaction transaction = (Transaction) itT.next();
            if (transaction.getDatePosted().before(result)) {
                result = transaction.getDatePosted();
            }
        }
        return result;
    }

    /**
     * Calculate the last transaction at list
     *
     * @return last timely transaction
     * @throws OFXException
     */
    public Date getLastTransactionTime() throws OFXException {
        Date result = new Date(Long.MIN_VALUE);
        List<Transaction> transList = this.getTransactionList();
        for (Iterator itT = transList.iterator(); itT.hasNext();) {
            Transaction transaction = (Transaction) itT.next();
            if (transaction.getDatePosted().after(result)) {
                result = transaction.getDatePosted();
            }
        }
        return result;
    }

    /**
     * The list of transaction from this file
     *
     * @return
     * @throws br.jus.trepb.sesop.mvnjfxapp.OFXException
     */
    protected List<Transaction> getTransactionList() throws OFXException {
        return this.getBankSetRerponseTransaction(0).getMessage().getTransactionList().getTransactions();
    }

    /**
     * Flag to indicate this condition
     *
     * @return
     */
    public boolean getIsReaded() {
        return (this.OFXContent != null);
    }

    private final File inputFile;

    /**
     * Construct a instance based on input file
     *
     * @param aInputFile
     */
    public OFXFileHelper(File aInputFile) {
        this.inputFile = aInputFile;
    }

    /**
     * A set of transactions from a bank
     *
     * @param i index from bank
     * @return Container with transaction data BankStatementResponseTransaction
     * @throws br.jus.trepb.sesop.mvnjfxapp.OFXException
     */
    public BankStatementResponseTransaction getBankSetRerponseTransaction(int i) throws OFXException {
        try {
            return (BankStatementResponseTransaction) this.getBankResponses().get(i);
        } catch (OFXParseException | IOException ex) {
            throw new OFXException("Erro recuperando resposta interna do arquivo(resposto do banco)" + ex.getMessage());
        }
    }

    /**
     * A set of messages filtered by class "banking" = "conta corrente"
     *
     * @return
     * @throws IOException
     * @throws OFXParseException
     */
    protected List<ResponseMessage> getBankResponses() throws IOException, OFXParseException {
        ResponseMessageSet message = this.getOFXContent().getMessageSet(MessageSetType.banking);  //pega apenas o conjunto para a classe filtrada acima
        return message.getResponseMessages();
    }

    /**
     * Read the input file to internal memory Split the content into several parts and validate your cardinality, be only one bank
     * and only banking elements
     *
     * @throws IOException
     * @throws OFXParseException
     */
    public void read() throws IOException, OFXParseException {

        FileInputStream fis = new FileInputStream(this.getInputFile());
        try {
            Reader fr = new InputStreamReader(fis, "windows-1252"); //Requerido para interpertrar corretamente os dados
            try {
                AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);  //Adapter para serialização
                this.OFXContent = (ResponseEnvelope) a.unmarshal(fr);
                /*
                //como não existe esse get "BankStatementResponse bsr = re.getBankStatementResponse();"
                //fiz esse codigo para capturar a lista de transações
                MessageSetType type = MessageSetType.banking;
                ResponseMessageSet message = this.OFXContent.getMessageSet(type);
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
                System.out.println("tipo: " + transaction.getTransactionType().name());
                System.out.println("id: " + transaction.getId());
                System.out.println("data: " + transaction.getDatePosted());
                System.out.println("valor: " + transaction.getAmount());
                System.out.println("descricao: " + transaction.getMemo());
                System.out.println("");
                }
                }
                }
                System.out.println("FIM DA EXIBIÇÃO!");
                 */
            } finally {
                fr.close();
            }
        } finally {
            fis.close();
        }
    }

    /**
     * SignonResponse after read the file
     */
    protected SignonResponse getSignonResponse() throws IOException, OFXParseException {
        return this.getOFXContent().getSignonResponse();
    }
    ;

    private ResponseEnvelope OFXContent;

    /**
     * Get the value of OFXContent
     *
     * @return the value of OFXContent
     * @throws java.io.IOException
     * @throws com.webcohesion.ofx4j.io.OFXParseException
     */
    public ResponseEnvelope getOFXContent() throws IOException, OFXParseException {
        if (!this.getIsReaded()) {
            this.read();
        } else {
        }
        return this.OFXContent;
    }

    /**
     * Set the value of OFXContent
     *
     * @param OFXContent new value of OFXContent
     */
    public void setOFXContent(ResponseEnvelope OFXContent) {
        this.OFXContent = OFXContent;
    }

    /**
     * Writes the actual content from OFXContent to the specified filename's Based at link
     *
     * @see <a href=http://javadevtips.blogspot.com.br/2011/11/creating-mock-ofx-server.html </a>
     *
     * @param filename
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     */
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
                a.setConversion(new BBOFXStringConversation());  //Adjuste to Bank of Bostil output
                a.marshal(this.OFXContent, ofxWriter);
            } finally {
                ofxWriter.close();
            }
        }
    }

    /**
     * Export only transaction as CSV
     *
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     * @throws OFXParseException
     */
    public void exportAsCSV(String filename) throws FileNotFoundException, IOException, OFXParseException {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.print(this.toCSVString());
            out.flush();
            out.close();
        }
    }

    /**
     * Find the correspondent transaction based on ID The rules for match 1 - Dates equals 2 - Amount 3 - Check number ends with
     * source account(global config?)
     *
     * @param target
     * @return
     * @throws OFXException
     */
    protected Transaction getMatchTransaction(Transaction target, String sourceAccount) throws OFXException {
        Transaction result = null;
        List<Transaction> tl = this.getTransactionList();
        for (Transaction transaction : tl) {
            if (transaction.getDatePosted().equals(target.getDatePosted())) {
                if (transaction.getAmount() == (target.getAmount() * -1)) { //valor deve ser negativo(dual)
                    if (transaction.getCheckNumber().endsWith(sourceAccount)) {
                        result = transaction;
                        break;
                    }
                }
            }
        }
        return result;
    }

    static private String toCsvRow(BankAccountDetails bankDetail, Date ts, Transaction transaction) {
        String result = "";
        result += bankDetail.getAccountNumber() + ",";
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss[ZZ:zzz]");
        sdf.setTimeZone(TimeZone.getDefault());
        result += sdf.format(ts) + ",";
        result += transaction.getTransactionType().name() + ",";
        result += transaction.getDatePosted() + ",";
        result += transaction.getAmount() + ",";
        result += transaction.getId() + ",";  //BUSCAR DIFERENÇA DTPOSTED
        result += transaction.getCheckNumber() + ",";
        result += "=\"" + transaction.getReferenceNumber() + "\","; // = para ser interpretado forçadamente como texto
        result += transaction.getMemo() + "\r\n";
        return result;
    }

    public String toCSVString() throws IOException, OFXParseException {
        ResponseEnvelope re = this.getOFXContent();
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

    public BankAccountDetails getAccount() throws OFXException {
        return this.getBankSetRerponseTransaction(0).getMessage().getAccount();
    }

}
