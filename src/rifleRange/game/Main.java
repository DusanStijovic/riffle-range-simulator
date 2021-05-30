package rifleRange.game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rifleRange.GUI.SceneGenerator;


public class Main extends Application {


    public static Stage primaryStage = null;
    public static Group currentRoot = null;

    public static GameStats gameStats = new GameStats();


    public static void startGamePreview(int levelNumber) {
        primaryStage.setScene(SceneGenerator.levelPreview(levelNumber));
    }

    public static void startGame(int levelNumber) {

        primaryStage.setScene(SceneGenerator.getGameSceneLevel(gameStats, levelNumber));
    }

    public static void initializeGame(int numOfLevels) {
        GameStats.numOfLevels = numOfLevels;
        startGamePreview(1);
    }

    public static void checkGameEnd() {
        boolean endLevel1 = Main.gameStats.getTargetsLeft() == 0;
        boolean endLevel2 = Main.gameStats.getMissilesLeft() == 0;
        if (endLevel1 || endLevel2) {
            Main.gameStats.updateLevel();
            Main.startGamePreview(Main.gameStats.getCurrentLevel());
        }
    }

    private Group getTarget(double width, double height) {
        return null;
    }


    public void startGame() {

    }


    public static Scene getCurrentScene() {
        return primaryStage.getScene();
    }

    public static Group getCurrentRoot() {
        return currentRoot;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene entryScene = SceneGenerator.makeEntryScene();
        Main.primaryStage = primaryStage;
        GameStats.gameStatsGUI = SceneGenerator.makeGameStatsGUI(gameStats);

        primaryStage.setTitle(GameSetting.GAME_NAME);
        primaryStage.setScene(entryScene);
        primaryStage.setWidth(GameSetting.WIDTH);
        primaryStage.setHeight(GameSetting.HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
