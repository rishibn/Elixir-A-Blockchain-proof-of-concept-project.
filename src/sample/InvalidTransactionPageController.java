package sample;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;


public class InvalidTransactionPageController implements  Initializable{

    @FXML
    TextArea invalidTransactionsTextArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String str = StringUtil.getInvalidTransactions();
        invalidTransactionsTextArea.setText(str);
    }
}
