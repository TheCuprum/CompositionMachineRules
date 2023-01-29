package cuprum.cmrule;

import java.util.Scanner;

import cuprum.cmrule.impl.List1DQuiverInitializer;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;
import cuprum.cmrule.tester.TesterUtil;

public class TestAllMatch {
    private static List1DQuiverInitializer provideInitializer() {
        return new List1DQuiverInitializer(new String[] {
                "00110",
                // "01110",
        });
    }

    public static void main(String[] args) {
        final int patternLength = 5;

        int concurrentSize;
        if (args.length == 1) {
            concurrentSize = Integer.parseInt(args[0]);
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Max threads to run tasks:");
            concurrentSize = sc.nextInt();
            sc.close();
        }

        // OneDimensionalQuiverInitializer targetQInit = provideInitializer();
        OneDimensionalQuiverInitializer targetQInit = new OneDimensionalQuiverInitializer("0".repeat(patternLength));
        OneDimensionalQuiverInitializer startQInit = new OneDimensionalQuiverInitializer("0".repeat(patternLength));

        TesterUtil.addTimer();

        ECARuleTester.testAllMatchesConcurrent(targetQInit, startQInit, "len=" + patternLength, Setting.ALL_MATCH_RECORD_FILE, 1000,
                concurrentSize, 5000);
    }
}
