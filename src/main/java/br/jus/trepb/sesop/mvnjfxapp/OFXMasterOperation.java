/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.google.common.base.Strings;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import javafx.util.Pair;

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
     *
     * @param ofxFile
     * @throws FileNotFoundException
     * @throws IOException
     * @throws OFXParseException Source:
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
        result.append("ACCOUNT-MASTER,ACCOUNT-SLAVE,DTPOSTED-MASTER,DTPOSTED-SLAVE,TRNTYPE-MASTER,TRNTYPE-SLAVE,"
                + "DTAVAILABLE-MASTER,DTAVAILABLE-SLAVE,TRNAMT-MASTER,TRNAMT-SLAVE,FITID-MASTER,FITID-SLAVE,CHECKNUM-MASTER,"
                + "CHECKNUM-SLAVE,REFNUM-MASTER,REFNUM-SLAVE,MEMO-MASTER,MEMO-SLAVE\n\r");
        List<Transaction> masterList = this.master.getTransactionList();
        for (Iterator itT = masterList.iterator(); itT.hasNext();) {
            Transaction mTrans = (Transaction) itT.next();
            if (mTrans.getCheckNumber().endsWith(GlobalConfig.OLD_SLAVE_ACCOUNT)) {
                Transaction sTrans = this.slave.getMatchTransaction(mTrans, GlobalConfig.OLD_MASTER_ACCOUNT);
                if (sTrans != null) {
                    result.append(mTrans.getBankAccountTo()).append(","); //ACCOUNT-MASTER
                    result.append(sTrans.getBankAccountTo()).append(","); //"ACCOUNT-SLAVE"
                    result.append(mTrans.getDatePosted()).append(","); //"DTPOSTED-MASTER"
                    result.append(sTrans.getDatePosted()).append(",");//"DTPOSTED-SLAVE"
                    result.append(mTrans.getTransactionType()).append(","); //"TRNTYPE-MASTER"
                    result.append(sTrans.getTransactionType()).append(","); //"TRNTYPE-SLAVE"
                    result.append(mTrans.getDateAvailable()).append(",");   //"DTAVAILABLE-MASTER"
                    result.append(sTrans.getDateAvailable()).append(",");   //"DTAVAILABLE-SLAVE"
                    result.append(mTrans.getAmount()).append(",");  //"TRNAMT-MASTER"
                    result.append(sTrans.getAmount()).append(",");  //"TRNAMT-SLAVE"
                    result.append(mTrans.getId()).append(",");  //"FITID-MASTER"
                    result.append(sTrans.getId()).append(",");  //"FITID-SLAVE"
                    result.append(mTrans.getCheckNumber()).append(",");   //"CHECKNUM-MASTER"
                    result.append(sTrans.getCheckNumber()).append(",");   //"CHECKNUM-SLAVE"
                    result.append("=\"").append(mTrans.getReferenceNumber()).append("\","); //"REFNUM-MASTER"
                    result.append("=\"").append(sTrans.getReferenceNumber()).append("\","); //"REFNUM-SLAVE"
                    result.append(mTrans.getMemo()).append(",");        //"MEMO-MASTER"
                    result.append(sTrans.getMemo()).append("\n\r");    //"MEMO-SLAVE\n\r"
                    //result += "=\"" + transaction.getReferenceNumber() + "\","; // = para ser interpretado forçadamente como texto
                } else {
                    throw new OFXException("Transação não pareada encontrada: " + mTrans.getId());
                }
            }
        }

        try {
            Writer fw = new FileWriter(filename);
            fw.write(result.toString());
            fw.close();
        } catch (IOException iOException) {
            throw new OFXException("Erro salvando arquivo CSV com os batimentos das transações:\r\n" + iOException.getMessage());
        }
    }

    private void checkOwners() throws OFXException {
        //TODO roger para master e MV para slave, numero de agencia e conta correspondentes
        //Dados da conta master
        String masterFullAgency = GlobalConfig.OLD_MASTER_AGENCY + "-" + GlobalConfig.OLD_MASTER_AGENCY_DV;
        String masterFullAccount = GlobalConfig.OLD_MASTER_ACCOUNT + "-" + GlobalConfig.OLD_MASTER_ACCOUNT_DV;
        if ( //teste de origem
                (!this.master.getAccount().getBranchId().equals(masterFullAgency)) //agencia
                | //or
                (!this.master.getAccount().getAccountNumber().equals(masterFullAccount)) //conta
                ) {
            throw new OFXException("Dados para arquivo master inconsistentes");
        }

        //Dados da conta slave
        String slaveFullAgency = GlobalConfig.OLD_SLAVE_AGENCY + "-" + GlobalConfig.OLD_SLAVE_AGENCY_DV;
        String slaveFullAccount = GlobalConfig.OLD_SLAVE_ACCOUNT + "-" + GlobalConfig.OLD_SLAVE_ACCOUNT_DV;
        if ( //teste de origem
                (!this.slave.getAccount().getBranchId().equals(slaveFullAgency)) //agencia
                | //or
                (!this.slave.getAccount().getAccountNumber().equals(slaveFullAccount)) //conta
                ) {
            throw new OFXException("Dados para arquivo slave inconsistentes");
        }

    }

    /**
     *
     */
    public void saveUpdatedOFX() throws OFXException {
        //Dados da conta master
        BankAccountDetails masterAccount = this.master.getAccount();
        //Altera para os novos valores
        masterAccount.setBranchId(GlobalConfig.NEW_MASTER_AGENCY + "-" + GlobalConfig.NEW_MASTER_AGENCY_DV);
        masterAccount.setAccountNumber(GlobalConfig.NEW_MASTER_ACCOUNT + "-" + GlobalConfig.NEW_MASTER_ACCOUNT_DV);
        //Dados da conta slave
        BankAccountDetails slaveAccount = this.slave.getAccount();
        //Altera para os novos valores
        slaveAccount.setBranchId(GlobalConfig.NEW_SLAVE_AGENCY + "-" + GlobalConfig.NEW_SLAVE_AGENCY_DV);
        slaveAccount.setAccountNumber(GlobalConfig.NEW_SLAVE_ACCOUNT + "-" + GlobalConfig.NEW_SLAVE_ACCOUNT_DV);

        //Enumera e trata todas as transações da conta master
        List<Transaction> mTL = this.master.getTransactionList();
        for (Transaction masterTransaction : mTL) {
            this.updateTransaction(masterTransaction, true);
        }

        //Varre as transações do slave, exceto as já atualizadas
        List<Transaction> sTL = this.slave.getTransactionList();
        for (Transaction slaveTrans : sTL) {
            if (slaveTrans.getTempId() == null) {  //Não relacionado com transação em master, assim requer ajustes
                this.updateTransaction(slaveTrans, false);
            }
        }
    }

    private void createChekNumRefNumPair(Transaction masterTrans, Transaction slaveTrans) throws OFXException {

        //todo ver abaixo
        //marter os 2 digitos iniciais do refnum(geralmente 60). Foi encontrado 52 uma vez roger recebendo de ze
        // 22 para amiga de lauricio
        //todo ver abaixo
        //caso os 4 digitos de refnum sejam a propria conta, implica em operação com formatação diferente
        //MasterAG = 3612-9
        //MasterAccount = 55898-2
        //SlaveAg = 3501-7
        //SlaveAccount = 21038-2

        //Para operação de master-> slave foi gerado:
        //<CHECKNUM>501000021038</CHECKNUM>  // 501000 021038  3[501000] 021038V
        //<REFNUM>603.501.000.021.038</REFNUM> //603.501.000.021.038

        // Para operação de slave-> master foi gerado:
        //<CHECKNUM>612000055898</CHECKNUM>
        //<REFNUM>603.612.000.055.898</REFNUM>
        //Busca literal por checknum e refnum que indiquem transferencia
        if (masterTrans.getCheckNumber().equals("501000021038") && slaveTrans.getCheckNumber().equals("612000055898")) {
            if (masterTrans.getReferenceNumber().equals("603.501.000.021.038") && slaveTrans.getReferenceNumber().equals("603.612.000.055.898")) {
                //Novos dados para a transação master
                masterTrans.setCheckNumber("5010000" + GlobalConfig.NEW_SLAVE_ACCOUNT);
                String dotedString = "60" + GlobalConfig.NEW_SLAVE_AGENCY + "0000" + GlobalConfig.NEW_SLAVE_ACCOUNT;
                dotedString = this.regularTokenizer(dotedString, ".", 3, false);  //sentido reverso como em nossa cultura
                masterTrans.setReferenceNumber(dotedString);
                //Novos dados para a transação slave
                slaveTrans.setCheckNumber("60" + GlobalConfig.NEW_MASTER_AGENCY_DV + "0000" + GlobalConfig.NEW_MASTER_ACCOUNT);
                dotedString = "60" + GlobalConfig.NEW_MASTER_AGENCY + "0000" + GlobalConfig.NEW_MASTER_ACCOUNT;
                dotedString = this.regularTokenizer(dotedString, ".", 3, false);
                slaveTrans.setReferenceNumber(dotedString);
            } else {
                throw new OFXException("pares para \"RefNum\" divergem para esta transação");
            }
        } else {
            throw new OFXException("pares para \"CheckNum\" divergem para esta transação");
        }
    }

    private void updateTransaction(Transaction trans, boolean isMaster) throws OFXException {
        if (isMaster) {
            //gatilhos para necessidade de alterações
            // 1 - Checknum(transf para conta slave ou vice-versa)
            // 2 - Memo(contem palavras reservadas)

            if ( //havendo envolvimento de qualquer uma das contas obrigatoriamente há alteração
                    trans.getCheckNumber().endsWith(GlobalConfig.OLD_MASTER_ACCOUNT) //transação envolve master
                    | //or
                    trans.getCheckNumber().endsWith(GlobalConfig.OLD_SLAVE_ACCOUNT)) //transação envolve slave
            {
                BBTransactionHelper originalBBTrans = new BBTransactionHelper(GlobalConfig.OLD_SLAVE_AGENCY, GlobalConfig.OLD_SLAVE_ACCOUNT);
                Transaction sTrans = this.slave.getMatchTransaction(trans, GlobalConfig.OLD_MASTER_ACCOUNT);
                if (sTrans != null) {
                    this.createChekNumRefNumPair(trans, sTrans);
                } else {
                    throw new OFXException("Erro pareando transação para atualização");
                }
            }
        } else { //tratamento para transações do slave
                //Busca pelo correspondente no master - até agora sem utilidade, pois as alterações serão realizadas apenas no caso positivo
        }
    }
}
