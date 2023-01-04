package cuprum.cmrule;

public class RuleUtil {
    public static String intArrayToString(int[] arr){
        StringBuilder sb = new StringBuilder();
        for (int num: arr){
            sb.append(num);
        }
        return sb.toString();
    }

    public static <O> String arrayToString(O[] arr){
        StringBuilder sb = new StringBuilder();
        for (O o: arr){
            sb.append(o.toString());
        }
        return sb.toString();
    }
}
