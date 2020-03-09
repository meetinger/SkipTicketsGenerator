package ru.yanchikdev.lib;

public class MathUtils {
    public static int RandomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
    public static int RandomIntInInterval(int min1, int max1, int min2, int max2){
        int factor = RandomInt(0, 2);

        if(factor == 0){
            return RandomInt(min1, max1);
        }else{
            return RandomInt(min2, max2);
        }
    }
}
