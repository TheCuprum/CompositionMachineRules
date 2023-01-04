package cuprum.cmrule;

import java.util.Scanner;

import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.impl.SimplificationHaltPredicate;
import cuprum.cmrule.tester.ECARuleTester;

public class TestSimplification {
    public static void main(String[] args) {
        String quiverPattern;
        if (args.length == 1) {
            quiverPattern = args[0];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter quiver pattern:");
            quiverPattern = sc.nextLine();
            sc.close();
        }
        // OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(Setting.TEST_BIT_STRING);
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(
            quiverPattern.length() <= 0 ? Setting.TEST_BIT_STRING : quiverPattern);
        SimplificationHaltPredicate predicate = new SimplificationHaltPredicate();
        
        ECARuleTester.testAllRules(qInit, predicate, qInit.getName() + "_" + Setting.SIMPLIFICATION_RECORD_FILE, 1000);
    }
}
