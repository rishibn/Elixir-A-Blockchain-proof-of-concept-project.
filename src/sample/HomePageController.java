package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class HomePageController {

    //Regestering Controllers
    @FXML
    MenuItem aboutButton;
    @FXML
    Button startElixirButton;

    //Layout
    Stage stage = new Stage();
    Scene scene;


    //This button launches a new window showing the about section of the project.
    public void aboutButtonClicked(){
        System.out.println("About button clicked.");
        AboutBox.display("About","This is a Blockchain proof of concept project.\nMade by Arun Kumar and B N Rishi." +
                "\n");
    }


    //This Listener launches the main window of the Elixir user interface.
    public void startElixirButtonClicked(ActionEvent event){
        System.out.println("Start elixir button clicked.");
        try{
            Node node = (Node)event.getSource();
            stage = (Stage) node.getScene().getWindow();
            stage.close();
            scene = new Scene(FXMLLoader.load(getClass().getResource("MainPage.fxml")));
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception exception){
            exception.printStackTrace();
        }

    }

}
