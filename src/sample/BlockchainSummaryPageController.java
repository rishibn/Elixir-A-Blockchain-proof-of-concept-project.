package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class BlockchainSummaryPageController implements Initializable {

    //Regestering controllers
    @FXML
    TextArea blockchainSummaryTextArea;

    //Initializing the newly launched window with data of Blockchain summary.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String str = StringUtil.printBlockChain();
        blockchainSummaryTextArea.setText(str);
    }
}
