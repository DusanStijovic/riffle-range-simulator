package rifleRange.game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rifleRange.GUI.SceneGenerator;
import rifleRange.GUI.Target;

import java.util.Calendar;


public class Main extends Application {


    private final static Object forSynchronization = new Object();
    public static GameStats gameStats = new GameStats();
    public static Stage primaryStage = null;
    public static Group currentRoot = null;
    static boolean gameEnd = false;

    public static void startGamePreview(int levelNumber) {
        primaryStage.setScene(SceneGenerator.levelPreview(levelNumber));
    }

    public static void startGamePlay(int levelNumber) {
        primaryStage.setScene(SceneGenerator.getGameSceneLevel(gameStats, levelNumber));
        gameStats.setStartTime(Calendar.getInstance().getTimeInMillis());
    }

    public static void initializeGame(int numOfLevels) {
        GameStats.numOfLevels = numOfLevels;
        startGamePreview(1);
    }


    public static void checkGameEnd() {
        synchronized (forSynchronization) {
            if (gameEnd) return;
            boolean endLevel1 = Main.gameStats.getTargetsLeft() == 0;
            boolean endLevel2 = Main.gameStats.getMissilesLeft() == 0;
            if (endLevel1 || endLevel2) {
                gameEnd = Main.gameStats.updateLevel();
                Target.removeEffectsToAllTargets();
                if (gameEnd) {
                    Main.showEndGameScreen();
                } else {
                    Main.gameStats.resetLevelDone();
                    Main.startGamePreview(Main.gameStats.getCurrentLevel());
                }

            }
        }
    }

    private static void showEndGameScreen() {
        primaryStage.setScene(SceneGenerator.getGameEndScreen(gameStats));

    }

    public static Scene getCurrentScene() {
        return primaryStage.getScene();
    }

    public static Group getCurrentRoot() {
        return currentRoot;
    }

    public static void main(String[] args) {
        launch(args);
    }


    public static void startGame() {
        Scene entryScene = SceneGenerator.makeEntryScene();
        gameStats = new GameStats();
        gameEnd = false;
        GameStats.gameStatsGUI = SceneGenerator.makeGameStatsGUI(gameStats);

        Main.primaryStage.setTitle(GameSetting.GAME_NAME);
        Main.primaryStage.setScene(entryScene);
        Main.primaryStage.setWidth(GameSetting.WIDTH);
        Main.primaryStage.setHeight(GameSetting.HEIGHT);
        Main.primaryStage.setResizable(false);
        Main.primaryStage.show();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        startGame();
    }
}
