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
import com.webcohesion.ofx4j.domain.data.banking.BankStatementResponseTransaction;
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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author roger
 */
public class OFXFileHelper {

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
     * @throws IOException
     * @throws OFXParseException
     */
    public BankStatementResponseTransaction getBankSetRerponseTransaction(int i) throws IOException, OFXParseException {
        return (BankStatementResponseTransaction) this.getBankResponses().get(i);
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

        FileInputStream fis = new FileInputStream(this.inputFile);
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
}
