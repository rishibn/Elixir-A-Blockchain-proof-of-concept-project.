package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//This class serves as the About window of the ATM Simulator.
public class AboutBox {
    public static void display(String title, String message){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(550);
        window.setMinHeight(350);

        Label label1 = new Label();
        label1.setText(message);

        Button closeButton = new Button("Close the window");
        closeButton.setOnAction(e ->window.close());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20,30,30,20));
        layout.getChildren().addAll(label1,closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
