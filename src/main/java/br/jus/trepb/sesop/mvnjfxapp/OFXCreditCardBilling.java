/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.domain.data.MessageSetType;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessage;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardStatementResponse;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

    public void exportTo(String destFilename) throws OFXException {
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
        ( * duas
        contas presentes - lembre - se *
        )
        List<ResponseMessage> list = this.OFXContent.getMessageSet(MessageSetType.creditcard).getResponseMessages();
        for (Iterator<ResponseMessage> it = list.iterator(); it.hasNext();) {
            CreditCardStatementResponseTransaction responseMessage = (CreditCardStatementResponseTransaction) it.next();
            CreditCardStatementResponse itemX = responseMessage.getMessage();
        }

        CreditCardResponseMessageSet items = (CreditCardResponseMessageSet) this.OFXContent.getMessageSet(MessageSetType.creditcard);
        List<ResponseMessage> transList = this.OFXContent.getMessageSet(MessageSetType.creditcard).getResponseMessages();
        for (ResponseMessage item : transList) {
            item.getResponseMessageName();
        }
    }

}
