package rifleRange.game;

import javafx.scene.Group;
import javafx.scene.control.Label;
import rifleRange.utility.Utility;

public class GameStats {
    public static Group gameStatsGUI = new Group();

    Label targetsLeftLabel = new Label();
    Label missileLeftLabel = new Label();

    public Label getTargetsLeftLabel() {
        return targetsLeftLabel;
    }

    public Label getMissileLeftLabel() {
        return missileLeftLabel;
    }

    public Label getLevelInfoLabel() {
        return levelInfoLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    Label levelInfoLabel = new Label();
    Label scoreLabel = new Label();

    int targetsLeft = 0;
    int missilesLeft = 0;
    int currentLevel = 0;
    double score = 0;

    public int getCurrentLevel() {
        return currentLevel;
    }

    int numOfTargetsHit = 0;
    public static int numOfLevels;

    public int numOfParallelTargetsOnScreen;
    public int maxNumOfParallelTargetsOnScreen;

    public int numOfTargetsInLevel;
    public int currentNumberOfGeneretedTargersInLevel;


    public void setGameStatsInfo(int levelNumber) {
        targetsLeft = calculateNumberOfTargets(levelNumber);
        numOfTargetsInLevel = targetsLeft;
        currentNumberOfGeneretedTargersInLevel = 0;
        missilesLeft = calculateNumberOfMissile(targetsLeft, levelNumber);
        numOfParallelTargetsOnScreen = 0;
        maxNumOfParallelTargetsOnScreen = levelNumber % GameSetting.MAX_NUMBER_OF_PARALLEL_TARGETS;

        targetsLeftLabel.setText(String.valueOf(targetsLeft));
        missileLeftLabel.setText(String.valueOf(missilesLeft));
        scoreLabel.setText(String.valueOf(score));
        levelInfoLabel.setText(String.format("%d / %d", levelNumber, numOfLevels));
    }

    public void targetHit(double points, double factor) {
        score += calculateGains(points, factor);
        numOfTargetsHit++;
        scoreLabel.setText(String.valueOf(score));
    }

    private double calculateGains(double points, double factor) {
        return points + points * factor;
    }

    public boolean updateMissileLeft() {
        if (missilesLeft > 0) {
            missilesLeft--;
            missileLeftLabel.setText(String.valueOf(missilesLeft));
            return missilesLeft == 0;
        } else
            return true;

    }

    public int getTargetsLeft() {
        return targetsLeft;
    }

    public int getMissilesLeft() {
        return missilesLeft;
    }

    public boolean updateTargetLeft() {
        if (missilesLeft > 0) {
            missilesLeft--;
            targetsLeftLabel.setText(String.valueOf(missilesLeft));
            return missilesLeft == 0;
        } else return true;
    }

    public boolean updateLevel() {
        if (currentLevel < numOfLevels) {
            currentLevel++;
            levelInfoLabel.setText(String.format("%d / %d", currentLevel, numOfLevels));
            return false;
        } else {
            return true;
        }
    }

    public void setTargets(int targets) {
        targetsLeftLabel.setText(String.valueOf(targets));
    }

    public void setMissile(int missile) {
        missileLeftLabel.setText(String.valueOf(missile));
    }


    public int calculateNumberOfMissile(int numOfTargets, int levelNumber) {
        return numOfTargets + Utility.getRandomInt(0, levelNumber);

    }

    public int calculateNumberOfTargets(int levelNumber) {
        return GameSetting.MIN_NUMBER_OF_TARGETS_PER_LEVEL + levelNumber * 2;
    }

    public int calculateMaxNumberOfParallelTargets(int levelNumber) {
        return levelNumber % GameSetting.MAX_NUMBER_OF_PARALLEL_TARGETS;
    }


}
