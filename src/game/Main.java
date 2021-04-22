package game;

import com.sun.javafx.scene.control.WebColorField;
import com.sun.javafx.scene.control.skin.WebColorFieldSkin;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;


public class Main extends Application {

    final static double WIDTH = 800, HEIGHT = 800;
    final static String GAME_NAME = "Rifle Range";

    private Group getTarget(double width, double height){
        return null;
    }

    private Scene makeEntryScene(double width, double height){

        Group root = new Group();

        VBox menu = new VBox(10);

        String name = "Welcome to " + Main.GAME_NAME + "\n\n";
        Text gameName = new Text(name);
        gameName.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 30));
        gameName.setTextAlignment(TextAlignment.CENTER);
        gameName.setFill(Color.web("#ffff"));


        TextField numberOfLevels = new TextField("4");
        numberOfLevels.setAlignment(Pos.CENTER);


        Text next = new Text("Press Enter to start a new Game\n\n");
        next.setTextAlignment(TextAlignment.CENTER);
        next.setFill(Color.web("#99aab5"));
        next.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.REGULAR, 19));

        menu.setAlignment(Pos.CENTER);


        menu.getChildren().addAll(gameName, next, numberOfLevels);
        root.getChildren().addAll(menu);

        root.getTransforms().addAll(
                new Translate((width-30*12)/2,height/4)
        );

        Scene entryScene = new Scene(root, width, height);
        entryScene.setFill(Color.web("#23272a"));

        return entryScene;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Scene entryScene = this.makeEntryScene(Main.WIDTH, Main.HEIGHT);
        entryScene.addEventHandler(KeyEvent.KEY_PRESSED, (event)->{

        });
        primaryStage.setTitle(Main.GAME_NAME);
        primaryStage.setScene(entryScene);
        primaryStage.setWidth(Main.WIDTH);
        primaryStage.setHeight(Main.HEIGHT);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
