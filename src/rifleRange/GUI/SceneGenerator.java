package rifleRange.GUI;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.scene.transform.Translate;
import rifleRange.error.NumOfLevelsException;
import rifleRange.game.GameSetting;
import rifleRange.game.GameStats;
import rifleRange.game.Main;

import java.util.Calendar;

public class SceneGenerator {
    private SceneGenerator() {
    }

    public static Scene makeEntryScene() {

        Group root = new Group();

        VBox menu = new VBox(10);

        String name = "Welcome to " + GameSetting.GAME_NAME + "\n\n";
        Text gameName = new Text(name);
        gameName.setFont(Font.font(GameSetting.TEXT_FONT, FontWeight.BOLD, FontPosture.REGULAR, 30));
        gameName.setTextAlignment(TextAlignment.CENTER);
        gameName.setFill(Color.web("#ffff"));


        TextField numberOfLevels = new TextField("4");
        numberOfLevels.setAlignment(Pos.CENTER);


        Text next = new Text("Press Enter to start a new Game\n\n");
        next.setTextAlignment(TextAlignment.CENTER);
        next.setFill(Color.web("#99aab5"));
        next.setFont(Font.font(GameSetting.TEXT_FONT, FontWeight.BOLD, FontPosture.REGULAR, 19));

        Text error = new Text();
        error.setFill(Color.TRANSPARENT);
        error.setFont(Font.font(GameSetting.TEXT_FONT, FontWeight.BOLD, FontPosture.REGULAR, 19));

        menu.setAlignment(Pos.CENTER);


        menu.getChildren().addAll(gameName, next, numberOfLevels, error);
        Scene entryScene = addBoxToScene(root, menu);


        entryScene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            error.setFill(Color.TRANSPARENT);
            if (event.getCode() != KeyCode.ENTER) return;
            try {
                int numOfLevels = Integer.parseInt(numberOfLevels.getText().strip());
                if (numOfLevels < 1) throw new NumOfLevelsException("Minimal number of levels is 1");
                Main.initializeGame(numOfLevels);
            } catch (NumberFormatException numberFormatException) {
                error.setFill(Color.RED);
                error.setText("Enter number");
            } catch (NumOfLevelsException e) {
                error.setFill(Color.RED);
                error.setText("Enter number greater than od 0");
            }
        });

        return entryScene;
    }

    public static Scene levelPreview(int levelNumber) {
        Group root = new Group();
        VBox menu = new VBox(10);

        Text levelText = new Text(String.format("Level %d", levelNumber));
        levelText.setFill(Color.WHITE);
        levelText.setFont(Font.font(GameSetting.TEXT_FONT, FontWeight.BOLD, FontPosture.REGULAR, 100));

        levelText.setTextAlignment(TextAlignment.CENTER);

        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(levelText);

        Scene scene = addBoxToScene(root, menu);


        long levelPreviewStartTime = Calendar.getInstance().getTimeInMillis();
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                long elapsed = Calendar.getInstance().getTimeInMillis() - levelPreviewStartTime;
                if (elapsed > GameSetting.LEVEL_PREVIEW_TIME) {
                    Main.startGamePlay(levelNumber);
                    this.stop();
                }

            }
        };
        animationTimer.start();
        return scene;

    }

    public static Group makeGameStatsGUI(GameStats gameStats) {
        Group root = new Group();
        GridPane grid = new GridPane();

        //Targets left
        Text targetsLeftText = new Text("targets  ");
        grid.add(targetsLeftText, 1, 1);
        grid.add(gameStats.getTargetsLeftLabel(), 1, 2);

        //Missile left
        Text missileLeftText = new Text("missile  ");
        grid.add(missileLeftText, 2, 1);
        grid.add(gameStats.getMissileLeftLabel(), 2, 2);

        //Level number
        Text levelNumberText = new Text("level  ");
        grid.add(levelNumberText, 3, 1);
        grid.add(gameStats.getLevelInfoLabel(), 3, 2);

        //Score
        Text scoreText = new Text("score  ");
        grid.add(scoreText, 4, 1);
        grid.add(gameStats.getScoreLabel(), 4, 2);

        root.getChildren().addAll(grid);

        return root;
    }

    public static Scene getGameSceneLevel(GameStats gameStats, int levelNumber) {
        Target.targetFactory.stop();
        Group root = new Group();

        root.getChildren().add(GameStats.gameStatsGUI);
        gameStats.setGameStatsInfo(levelNumber);
        Target.targetFactory.start();


        Main.currentRoot = root;
        Scene scene = new Scene(root, GameSetting.WIDTH, GameSetting.HEIGHT);
        SceneGenerator.addBackgroundToScene(scene, levelNumber);
        SceneGenerator.setCursorScene(scene, GameSetting.CURSOR_PICTURE);

        int i[] = {0};
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            boolean endLevel2 = Main.gameStats.updateMissileLeft();
            System.out.println("CAO" + i[0]++);
            Main.checkGameEnd();
            event.consume();
        });

        return scene;
    }

    private static Scene addBoxToScene(Group root, VBox menu) {
        root.getChildren().addAll(menu);

        root.getTransforms().addAll(
                new Translate((GameSetting.WIDTH - 30 * 12) / 2, GameSetting.HEIGHT / 4)
        );

        Scene scene = new Scene(root, GameSetting.WIDTH, GameSetting.HEIGHT);
        scene.setFill(Color.web(GameSetting.BACKGROUND_COLOR));
        return scene;
    }

    private static void setRootScene(Scene scene, Group root) {
        Main.currentRoot = root;
        scene.setRoot(root);
    }

    public static void setCursorScene(Scene scene, String cursorPictureName) {
        Image image = new Image(cursorPictureName, 40, 40, true, true);  //pass in the image path
        ImageCursor imageCursor = new ImageCursor(image, 20, 20);
        final double KEYBOARD_MOVEMENT_DELTA = GameSetting.KEYBOARD_MOVEMENT_DELTA;
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            Robot robot = new Robot();

            if (event.getCode() == KeyCode.SPACE) {
                robot.mouseClick(MouseButton.PRIMARY);
            } else {
                Point2D cursorPosition = robot.getMousePosition();
                double currentX = cursorPosition.getX();
                double currentY = cursorPosition.getY();

                Point2D nextPosition = switch (event.getCode()) {
                    case UP -> new Point2D(currentX, currentY - KEYBOARD_MOVEMENT_DELTA);
                    case RIGHT -> new Point2D(currentX + KEYBOARD_MOVEMENT_DELTA, currentY);
                    case DOWN -> new Point2D(currentX, currentY + KEYBOARD_MOVEMENT_DELTA);
                    case LEFT -> new Point2D(currentX - KEYBOARD_MOVEMENT_DELTA, currentY);
                    default -> new Point2D(currentX, currentY);
                };
                robot.mouseMove(nextPosition);
            }
        });
        scene.setCursor(imageCursor);
    }

    public static void addBackgroundToScene(Scene scene, int levelNumber) {
        levelNumber = levelNumber % (GameSetting.NUM_OF_DIFFERENT_PICTURES + 1);
        if (levelNumber == 0) levelNumber = 1;
        String pictureName = String.format(GameSetting.BACKGROUND_PICTURE_FORMAT, levelNumber);
        System.out.println(pictureName);
        Image image = new Image(pictureName);
        ImagePattern backgroundImage = new ImagePattern(image, 0, 0, 1, 1, true);
        Rectangle background = new Rectangle(0, GameSetting.OFFSET_FOR_STATS, GameSetting.WIDTH, GameSetting.HEIGHT);
        background.setFill(backgroundImage);

        Group root = new Group();
        root.getChildren().addAll(background);
        root.getChildren().addAll(scene.getRoot().getChildrenUnmodifiable());
        setRootScene(scene, root);
    }


    public static Scene getGameEndScreen(GameStats gameStats) {
        System.out.println("OVDE SAM");
        Group root = new Group();
        Rectangle rectangle = new Rectangle(0, 0, 100, 100);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Report");
        sceneTitle.setFont(Font.font(GameSetting.TEXT_FONT, FontWeight.NORMAL, 20));
        sceneTitle.setFill(Color.WHITE);
        grid.add(sceneTitle, 0, 0, 2, 1);

        setKeyAndValueOnGrid(grid, "targets hit", String.valueOf(gameStats.getHitTargets()), 2);
        setKeyAndValueOnGrid(grid, "point", String.valueOf(gameStats.getScore()), 3);
        setKeyAndValueOnGrid(grid, "time", String.valueOf(GameStats.getElapsedTimeNiceFormat(gameStats.getElapsedTime())), 4);

        Button newGameButton = new Button("Start new game");
        grid.add(newGameButton, 0, 5, 2, 1);

        newGameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            Main.startGame();
        });

        root.getChildren().addAll(grid);

//        root.getTransforms().addAll(
//                new Translate(GameSetting.WIDTH / 2 -, 0)
//        );

        Scene scene = new Scene(root, GameSetting.WIDTH, GameSetting.HEIGHT);
        scene.setFill(Color.web(GameSetting.BACKGROUND_COLOR));
        return scene;
    }

    private static void setKeyAndValueOnGrid(GridPane grid, String key, String value, int row) {
        Label keyLabel = new Label(key);
        keyLabel.setTextFill(Color.WHITE);
        grid.add(keyLabel, 0, row);

        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.WHITE);
        grid.add(valueLabel, 1, row);
    }
}
