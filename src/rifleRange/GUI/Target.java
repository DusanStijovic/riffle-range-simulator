package rifleRange.GUI;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import rifleRange.game.GameSetting;
import rifleRange.game.Main;
import rifleRange.utility.Utility;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Target extends Group {

    final static Color[] targetColors = {
            Color.PINK,
            Color.YELLOW,
            Color.GREEN,
            Color.VIOLET,
            Color.RED,
            Color.BLUE
    };

    final static int[] points = {
            20, 40, 60, 80, 100, 120, 150
    };

    final static int MIN_NUMBER_OF_TARGET_CIRCLE = 3;
    final static int MAX_NUMBER_OF_TARGET_CIRCLE = 7;


    final static Random random = new Random();


    List<Node> targetsPart = new LinkedList<>();
    TargetDisappearingEffects disappearingEffects;
    double lifeTime;
    double factor = 1;


    public static Group getStartTargets(int levelNumber) {
        Group root = new Group();
        return root;
    }


    public void makeCircle(double radius, double startRadius, Color color, Color textColor, int number) {
        Circle circle = new Circle(radius);
        circle.setFill(color);

        Text pointsText = new Text(String.valueOf(points[number]));
        pointsText.getTransforms().addAll(
                new Translate(-(radius), startRadius / 4)
        );
        pointsText.setFill(textColor);

        targetsPart.add(circle);
        targetsPart.add(pointsText);
        this.getChildren().addAll(circle, pointsText);

        circle.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {

            System.out.println(points[number]);
            targetHit(new Point2D(event.getSceneX(), event.getSceneY()), textColor, points[number]);
        });
        pointsText.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {

            System.out.println(points[number]);
            targetHit(new Point2D(event.getSceneX(), event.getSceneY()), textColor, points[number]);
        });

    }

    private void targetHit(Point2D hitLocation, Color color, double numberOfPoints) {
        Main.getCurrentRoot().getChildren().remove(this);
        disappearingEffects.stop();
        targetsPart.clear();

        final double[] startTime = {Calendar.getInstance().getTimeInMillis()};

        Text numberOfPointsText = new Text(String.valueOf(numberOfPoints));
        numberOfPointsText.getTransforms().addAll(
                new Translate(hitLocation.getX(), hitLocation.getY())
        );
        numberOfPointsText.setFont(Font.font(GameSetting.TEXT_FONT, 40));
        numberOfPointsText.setFill(color);

        Main.getCurrentRoot().getChildren().addAll(numberOfPointsText);
        AnimationTimer targetHitAnimation = new AnimationTimer() {
            @Override
            public void handle(long l) {
                double elapsed = Calendar.getInstance().getTimeInMillis() - startTime[0];
                if (elapsed > GameSetting.AFTER_HIT_EFFECT) {
                    numberOfPointsText.setOpacity(numberOfPointsText.getOpacity() - GameSetting.OPACITY_CHANGE);
                    startTime[0] = Calendar.getInstance().getTimeInMillis();
                    numberOfPointsText.getTransforms().addAll(
                            new Scale(0.8, 0.8)
                    );
                    if (numberOfPointsText.getOpacity() <= 0) {
                        this.stop();
                        Main.getCurrentRoot().getChildren().remove(numberOfPointsText);
                    }
                }
            }
        };
        targetHitAnimation.start();
        boolean endLevel1 = Main.gameStats.updateTargetLeft();
        boolean endLevel2 = Main.gameStats.updateMissileLeft();
        Main.gameStats.numOfParallelTargetsOnScreen--;
        Main.gameStats.targetHit(numberOfPoints, factor);
        Main.checkGameEnd();
    }

    public Target(double width, double height) {
        double START_RADIUS = Math.min(width, height) * 0.02;
        double RADIUS_INCREMENT = Math.min(width, height) * 0.03;

        int NUM_OF_TARGET_CIRCLE = Utility.getRandomInt(MIN_NUMBER_OF_TARGET_CIRCLE, MAX_NUMBER_OF_TARGET_CIRCLE) - 1;


        final Color centerColor = targetColors[Utility.getRandomInt(0, targetColors.length - 1)];

        final Color[] circleColor = {Color.BLACK};
        IntStream.range(0, NUM_OF_TARGET_CIRCLE).forEach((number) -> {
                    double currentRadius = START_RADIUS + (NUM_OF_TARGET_CIRCLE - number) * RADIUS_INCREMENT;
                    makeCircle(currentRadius, START_RADIUS, circleColor[0], centerColor, number);
                    circleColor[0] = (circleColor[0] == Color.BLACK ? Color.WHITE : Color.BLACK);
                }
        );

        makeCircle(START_RADIUS, START_RADIUS, centerColor, Color.BLACK, NUM_OF_TARGET_CIRCLE);

        double BORDER = START_RADIUS + (NUM_OF_TARGET_CIRCLE + 1) * RADIUS_INCREMENT;

        double xOffset = Utility.getRandomDouble(0 + BORDER, width - BORDER);
        double yOffset = Utility.getRandomDouble(GameSetting.OFFSET_FOR_STATS + BORDER, height - BORDER);

        this.getTransforms().addAll(
                new Translate(xOffset, yOffset)
        );

        disappearingEffects = new TargetDisappearingEffects();
        disappearingEffects.start();

        lifeTime = Utility.getRandomDouble(GameSetting.MIN_TARGET_LIFETIME, GameSetting.MAX_TARGET_LIFETIME);

    }

    class TargetDisappearingEffects extends AnimationTimer {

        long startTime = 0;

        TargetDisappearingEffects() {
            startTime = Calendar.getInstance().getTimeInMillis();
        }

        @Override
        public void handle(long l) {
            long elapsedTime = Calendar.getInstance().getTimeInMillis() - startTime;

            boolean end = false;
            if (elapsedTime > Target.this.lifeTime) {
                for (Node node : Target.this.targetsPart) {
                    node.setOpacity(node.getOpacity() - GameSetting.OPACITY_CHANGE);
                    if (node.getOpacity() <= 0) {
                        Main.gameStats.numOfParallelTargetsOnScreen--;
                        Main.gameStats.updateTargetLeft();
                        Main.checkGameEnd();
                        end = true;
                        break;
                    }
                }
                startTime = Calendar.getInstance().getTimeInMillis();
            }

            if (end) {
                Target.this.targetsPart.clear();
                Main.getCurrentRoot().getChildren().remove(Target.this);
                this.stop();
            }
        }
    }

    static TargetFactory targetFactory = new TargetFactory();


    static class TargetFactory extends AnimationTimer {


        @Override
        public void handle(long l) {
            while (Main.gameStats.numOfParallelTargetsOnScreen <= Main.gameStats.maxNumOfParallelTargetsOnScreen && Main.gameStats.currentNumberOfGeneretedTargersInLevel<Main.gameStats.numOfTargetsInLevel) {
                Target newTarget = new Target(GameSetting.WIDTH, GameSetting.HEIGHT);
                Main.gameStats.numOfParallelTargetsOnScreen++;
                Main.getCurrentRoot().getChildren().addAll(newTarget);
                Main.gameStats.currentNumberOfGeneretedTargersInLevel++;
            }
        }
    }

}
