import java.io.Serializable;

public class RandomArray implements Serializable{
    private int[] randomArray;

    public RandomArray(int arraySize) {
        this.randomArray = new int[arraySize];
        for( int i = 0; i <  arraySize; i++) {
            this.randomArray[i] = ((int) (Math.random() * 100));
        }
    }

    public RandomArray() {
    }

    public int[] getRandomArray() {
        return randomArray;
    }

    public void setRandomArray(int[] randomArray) {
        this.randomArray = randomArray;
    }
}
