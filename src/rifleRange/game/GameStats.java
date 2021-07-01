package rifleRange.game;

import javafx.scene.Group;
import javafx.scene.control.Label;
import rifleRange.utility.Utility;

import java.util.Calendar;

public class GameStats {
    public static Group gameStatsGUI = new Group();
    public static int numOfLevels;
    public int numOfParallelTargetsOnScreen;
    public int maxNumOfParallelTargetsOnScreen;
    public int numOfTargetsInLevel;
    public int currentNumberOfGeneratedTargetsInLevel;
    Label targetsLeftLabel = new Label();
    Label missileLeftLabel = new Label();
    Label levelInfoLabel = new Label();
    Label scoreLabel = new Label();
    int targetsLeft = 0;
    int missilesLeft = 0;
    int currentLevel = 1;
    double score = 0;
    int numOfTargetsHit = 0;
    long startTime = Calendar.getInstance().getTimeInMillis();
    long elapsedTime = 0;

    public static String getElapsedTimeNiceFormat(long elapsedTime) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = elapsedTime / daysInMilli;
        elapsedTime = elapsedTime % daysInMilli;

        long elapsedHours = elapsedTime / hoursInMilli;
        elapsedTime = elapsedTime % hoursInMilli;

        long elapsedMinutes = elapsedTime / minutesInMilli;
        elapsedTime = elapsedTime % minutesInMilli;

        long elapsedSeconds = elapsedTime / secondsInMilli;

        return String.format(
                "%d days, %d hours, %d minutes, %d seconds",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    public double getScore() {
        return score;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

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

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setGameStatsInfo(int levelNumber) {
        targetsLeft = calculateNumberOfTargets(levelNumber);
        numOfTargetsInLevel = targetsLeft;
        currentNumberOfGeneratedTargetsInLevel = 0;
        missilesLeft = calculateNumberOfMissile(targetsLeft, levelNumber);
        numOfParallelTargetsOnScreen = 0;
        maxNumOfParallelTargetsOnScreen = levelNumber % GameSetting.MAX_NUMBER_OF_PARALLEL_TARGETS;

        targetsLeftLabel.setText(String.valueOf(targetsLeft));
        missileLeftLabel.setText(String.valueOf(missilesLeft));
        scoreLabel.setText(String.valueOf(score));
        levelInfoLabel.setText(String.format("%d / %d", levelNumber, numOfLevels));
    }

    public void targetHit(double points, double factor) {
        synchronized (this) {
            score += calculateGains(points, factor);
            numOfTargetsHit++;
            numOfParallelTargetsOnScreen--;
            scoreLabel.setText(String.valueOf(score));

        }
    }

    private double calculateGains(double points, double factor) {
        return points + points * factor;
    }

    public boolean updateMissileLeft() {
        System.out.println(missilesLeft);
        synchronized (this) {
            if (missilesLeft > 0) {
                missilesLeft--;
                updateMissileLeftDisplay();
                return missilesLeft == 0;
            } else
                return true;
        }
    }

    public int getTargetsLeft() {
        return targetsLeft;
    }

    public int getMissilesLeft() {
        return missilesLeft;
    }

    public boolean updateTargetLeft() {
        synchronized (this) {
            if (targetsLeft > 0) {
                targetsLeft--;
                updateTargetsLeftDisplay();
                return targetsLeft == 0;
            } else return true;
        }
    }

    public synchronized void resetLevelDone(){
        numOfParallelTargetsOnScreen = 0;
        currentNumberOfGeneratedTargetsInLevel = 0;
        numOfTargetsInLevel = 0;
    }

    public boolean updateLevel() {
        synchronized (this) {
            elapsedTime += Calendar.getInstance().getTimeInMillis() - startTime;
            if (currentLevel < numOfLevels) {
                currentLevel++;
                setLevel();
                return false;
            } else {
                return true;
            }
        }
    }

    public void setStartTime(long time) {
        this.startTime = time;
    }

    public void updateTargetsLeftDisplay() {
        targetsLeftLabel.setText(String.valueOf(targetsLeft));
    }

    public void updateMissileLeftDisplay() {
        missileLeftLabel.setText(String.valueOf(missilesLeft));
    }

    public void setLevel() {
        levelInfoLabel.setText(String.format("%d / %d", currentLevel, numOfLevels));
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


    public int getHitTargets() {
        return numOfTargetsHit;
    }


}
