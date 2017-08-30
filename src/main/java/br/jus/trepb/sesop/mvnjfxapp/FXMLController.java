package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.io.OFXParseException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.nio.file.Paths;


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

    @FXML
    private void handleButtonCancel(ActionEvent event) {
        System.out.println("Terminando aplicação!");
        System.exit(0);
    }

    @FXML
    private void handleButtonLkp(ActionEvent event) {
        System.out.println("Buscando por arquivo de entrada!");
        Button btn = (Button) event.getSource();
        if (btn.getId() == this.edtMasterOFXLkp.getId()) {
            //TODO: Buscar por valor para OFX master
            System.out.println("Buscando por arquivo MASTER!");
            this.edtMasterOFX.setText(this.choiceFilename());
        } else {
            //TODO: Buscar por valor para OFX Slave
            System.out.println("Buscando por arquivo SLAVE!");
            this.edtSlaveOFX.setText(this.choiceFilename());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.btnExec.setMnemonicParsing(true);
        this.btnCancel.setMnemonicParsing(true);
        if (GlobalConfig.DEBUG) {
            this.lastUsedDir = Paths.get(System.getProperty("user.home"), "\\Google Drive\\10.Privado\\Financeiro\\2016\\12").toString();
        } else {
            this.lastUsedDir = "c:\\";
        }
    }

    private String lastUsedDir;

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
    private void handleButtonExecute(ActionEvent event) {
        System.out.println("Iniciando operação!");
        File inFile = new File(this.edtMasterOFX.getText());
        OFXFileHelper ofxF = new OFXFileHelper(inFile);
        this.displayTransactions(ofxF);   //Leitura do conteudo do OFX
    }

    private void saveAs( ){
        
    }
    
    private void displayTransactions(OFXFileHelper ofxF) {
        try {
            OFXFileIO.displayContent(ofxF);
        } catch (IOException | OFXParseException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
