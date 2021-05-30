package rifleRange.utility;

import java.util.Random;

public class Utility {

    static Random random = new Random();

    public static int getRandomInt(int low, int high) {
        return low + random.nextInt(high - low + 1);
    }

    public static double getRandomDouble(double low, double high) {
        return low + random.nextDouble() * (high - low);
    }
}
