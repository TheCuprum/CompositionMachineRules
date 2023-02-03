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

    public static int combineECARulePattern(int d1, int d2, int d3, int d4) {
        return (d1 << 16) + (d2 << 12) + (d3 << 8) + d4;
    }

    public static String ECAPatternToName(int rulePattern){
        int d1 = (rulePattern >> 16) & 0x03;
        int d2 = (rulePattern >> 12) & 0x0F;
        int d3 = (rulePattern >> 8) & 0x0F;
        int d4 = rulePattern & 0xFF;

        return d1 + "-" + d2 + "-" + d3 + "-" + d4;
    }
}
