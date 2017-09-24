package br.jus.trepb.sesop.mvnjfxapp;

import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.io.OFXParseException;
import java.io.File;
import java.io.IOException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
            this.lastUsedDir = Paths.get(System.getProperty("user.home"), "\\Google Drive\\10.Privado\\Financeiro\\2017\\01").toString();
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

        //PRE: 2 filenames and valids ofx files
        System.out.println("Iniciando operação!");

        try {
            //Cria arquivo para o apontado como master
            File masterOFXFile = new File(this.edtMasterOFX.getText());
            //Instancia o OFX master
            OFXFileHelper masterOFX = new OFXFileHelper(masterOFXFile);
            masterOFX.read();

            //Cria arquivo slave
            File slaveOFXFile = new File(this.edtSlaveOFX.getText());
            //Instancia o OFX master
            OFXFileHelper slaveOFX = new OFXFileHelper(slaveOFXFile);
            slaveOFX.read();

            OFXMasterOperation controller = new OFXMasterOperation(masterOFX, slaveOFX);
            controller.exportCSVTransactionPairs("D:\\Temp\\TransPairs.csv");

            //Salva como outro ofx no mesmo caminho com sufixo alterado
            BankAccountDetails account = masterOFX.getBankSetRerponseTransaction(0).getMessage().getAccount();
            account.setAccountNumber("123456");
            masterOFX.exportAsCSV("D:\\Temp\\Out.csv");
            masterOFX.writeTo(masterOFX.getStdOutputFilename());

        } catch (Exception exception) {
            showAlert("Alerta de erro", exception.getMessage());
        }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information Alert");
        alert.setContentText(message);
        alert.show();
    }

}
