package br.jus.trepb.sesop.mvnjfxapp;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class FXMLController implements Initializable {

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TextField edtMasterOFX;
    @FXML
    private TextField edtSlaveOFX;
    @FXML
    private TextField edtCreditCardOFX;
    @FXML
    private Button edtMasterOFXLkp;
    @FXML
    private Button edtCreditCardOFXLkp;
    @FXML
    private Button edtSlaveOFXLkp;
    @FXML
    private Button btnExec;
    @FXML
    private Button btnCancel;
    @FXML
    private CheckBox chkBoxIgnoreAutoInvestments;
    @FXML
    private Spinner<Integer> edtCreditCardTresholdDay;
    @FXML
    private Label label;

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
            if (btn.getId().equals(this.edtCreditCardOFXLkp.getId())) { //buscar por arquivo com informações de cartão de crédito
                System.out.println("Buscando por arquivo SLAVE!");
                this.edtCreditCardOFX.setText(this.choiceFilename());
            } else { //Buscar por valor para OFX Slave
                System.out.println("Buscando por arquivo SLAVE!");
                this.edtSlaveOFX.setText(this.choiceFilename());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Carregar os parametros iniciais da aplicação
        this.btnExec.setMnemonicParsing(true);
        this.btnCancel.setMnemonicParsing(true);
        if (GlobalConfig.DEBUG) {
            this.lastUsedDir = Paths.get(System.getProperty("user.home"), "\\Google Drive\\10.Privado\\Financeiro\\_swap").toString();
        } else {
            this.lastUsedDir = System.getProperty("user.dir");
        }
        //this.chkBoxIgnoreAutoInvestments.setSelected(true); //apenas para teste de inicialização
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
            return ""; //null pode dar problemas posteriores
        }

    }

    @FXML
    private void handleButtonExecute(ActionEvent event) {

        System.out.println("Iniciando operação!");

        try {
            //Cria arquivo para o apontado como master
            OFXFileHelper masterOFX = null;
            String filename = this.edtMasterOFX.getText();
            if (!filename.isEmpty()) {
                File masterOFXFile = new File(filename);
                //Instancia o OFX master
                masterOFX = new OFXFileHelper(masterOFXFile);
                masterOFX.read();
            }

            //Cria arquivo slave
            OFXFileHelper slaveOFX = null;
            filename = this.edtSlaveOFX.getText();
            if (!filename.isEmpty()) {
                File slaveOFXFile = new File(filename);
                //Instancia o OFX master
                slaveOFX = new OFXFileHelper(slaveOFXFile);
                slaveOFX.read();
            } else {
            }

            //Gera arquivos fake
            OFXMasterOperation controller = new OFXMasterOperation(masterOFX, slaveOFX);
            boolean ignoreAutoInvestments = this.chkBoxIgnoreAutoInvestments.isSelected(); //todo: Criar controle para a leitura desta flag
            java.awt.Rectangle ret = controller.saveUpdatedOFX(ignoreAutoInvestments);  //altera os valores reais pelos fakes e salva com sufixo

            //verifica se há processamento de fatura de cartão de crédito e gera txt com exportação
            filename = this.edtCreditCardOFX.getText();
            if (!filename.isEmpty()) {
                OFXCreditCardBilling ccBill = new OFXCreditCardBilling(filename);
                ccBill.read();
                //Object obj = this.edtCreditCardTresholdDay.getValue();
                //ccBill.writeTo("C:\\temp\\out.ofx");
                int deltaDays = -1 * this.edtCreditCardTresholdDay.getValueFactory().getValue();
                ccBill.exportTo(ccBill.getDefaultExportFilename(), deltaDays);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Operação finalizada com sucesso!\n\r");
            sb.append(String.format("Foram %d transações master, destas %d foram ignoradas.\n\r", ret.x, ret.y));
            sb.append(String.format("Foram %d transações slave, destas foram %d ignoradas.\n\r", ret.width, ret.height));
            showAlert("Aviso:", sb.toString());

        } catch (Exception exception) {
            showAlert("Alerta de erro", exception.getLocalizedMessage());
        }

    }

    static public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information Alert");
        alert.setContentText(message);
        alert.show();
    }

}
