package ru.yanchikdev;

public class GenerationBound {
    int leftBound, rightBound;

    public GenerationBound(int leftBound, int rightBound) {
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public int getLeftBound() {
        return leftBound;
    }

    public int getRightBound() {
        return rightBound;
    }

    @Override
    public String toString() {
        return "GenerationBound{" +
                "leftBound=" + leftBound +
                ", rightBound=" + rightBound +
                '}';
    }
}
