import java.util.Arrays;
import java.util.IntSummaryStatistics;

public class Utility {

    public static String randomArray(int len){
        int i;
        StringBuffer sb = new StringBuffer();
        //int[] ar1 = new int[100];
        for(i = 0; i <  len; i++) {
            if (i != 99){
                sb.append((int)(Math.random() * 100));
                sb.append(",");
            }
            else{
                sb.append((int)(Math.random() * 100));
            }
            //ar1[i] = (int)(Math.random() * 100);
            //System.out.print(ar1[i] + "  ");
        }
        return sb.toString();
    }

    public static String maxValue(String msg){

        String[] Array = msg.split(",");

        int max = Integer.MIN_VALUE, maxIndex = 0;

        for (int i = 0; i < Array.length; i++) {
            if (Integer.parseInt(Array[i]) > max) {
                max = Integer.parseInt(Array[i]);
                maxIndex = i;
            }
        }

        return Array[maxIndex];
    }


}

