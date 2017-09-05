package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.io.OFXParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;

public class FXMLController implements Initializable {

    @FXML
    private TextField edtMasterOFX;
    @FXML
    private TextField edtSlaveOFX;
    @FXML
    private Button edtMasterOFXLkp;
    @FXML
    private Button edtSlaveOFXLkp;
    @FXML
    private Button btnExec;
    @FXML
    private Button btnCancel;
    private String lastUsedDir;

    @FXML
    private void handleButtonCancel(ActionEvent event) {
        System.out.println("Terminando aplicação!");
        System.exit(0);
    }

    @FXML
    private void handleButtonLkp(ActionEvent event) {
        System.out.println("Buscando por arquivo de entrada!");
        Button btn = (Button) event.getSource();
        if (btn.getId().equals(this.edtMasterOFXLkp.getId())) {
            //Buscar por valor para OFX master
            System.out.println("Buscando por arquivo MASTER!");
            this.edtMasterOFX.setText(this.choiceFilename());
        } else {
            //Buscar por valor para OFX Slave
            System.out.println("Buscando por arquivo SLAVE!");
            this.edtSlaveOFX.setText(this.choiceFilename());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Carregar os parametros iniciais da aplicação
        this.btnExec.setMnemonicParsing(true);
        this.btnCancel.setMnemonicParsing(true);
        if (GlobalConfig.DEBUG) {
            this.lastUsedDir = Paths.get(System.getProperty("user.home"), "\\Google Drive\\10.Privado\\Financeiro\\2016\\12").toString();
        } else {
            this.lastUsedDir = "c:\\";
        }
    }

    private String choiceFilename() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione arquivo OFX");
        fileChooser.setInitialDirectory(new File(this.lastUsedDir));
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Arquivo OFX", "*.ofx"));
        File selectedFile = fileChooser.showOpenDialog(this.btnCancel.getScene().getWindow());
        if (selectedFile != null) {
            this.lastUsedDir = selectedFile.getParent();
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }

    }

    @FXML
    private void handleButtonExecute(ActionEvent event) throws IOException, OFXParseException {
        System.out.println("Iniciando operação!");
        //Cria arquivo para o apontado como master
        File masterOFX = new File(this.edtMasterOFX.getText());
        //Prepara nome do ofx de saida para o master
        String outMasterOFXName = FilenameUtils.removeExtension(masterOFX.getAbsolutePath()) + "_upd.ofx";
        
        //Instancia o OFX
        OFXFileHelper ofxF = new OFXFileHelper(masterOFX);

        //Exibe transações no console
        // TODO remover posteriormente isso    
        OFXFileIO.displayContent(ofxF);
        
        this.saveAsCSV("D:\\temp\\out.csv", OFXFileIO.toCSVString(ofxF));

        //Salva como outro ofx no mesmo camainho com sufixo alterado
        BankAccountDetails account = ofxF.getBankSetRerponseTransaction(0).getMessage().getAccount();
        account.setAccountNumber("123456");
        ofxF.writeTo(outMasterOFXName);
    }

    private void saveAsCSV(String filename, String content) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.print(content);
            out.flush();
            out.close();
        }
    }

}
