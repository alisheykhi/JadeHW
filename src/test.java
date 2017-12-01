

public class test {
    public static void main(String[] args) {
        double powerSum1 = 0;
        double powerSum2 = 0;
        double stdev = 0;
        int[] total = {12,16,18,20,15,18,14,17,13,17};
        System.out.println(total.length);
        for (int i = 0; i < total.length; i++) {
            powerSum1 += total[i];
        }
        powerSum1 = powerSum1/total.length;
        System.out.println(powerSum1);
        for (int i = 0; i < total.length; i++) {
            powerSum2 += Math.pow(total[i]-powerSum1, 2);
        }
        powerSum2 = powerSum2/total.length;
        stdev = Math.sqrt(powerSum2);

        }
    }

