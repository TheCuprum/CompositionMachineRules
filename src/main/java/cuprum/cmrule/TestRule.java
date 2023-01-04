package cuprum.cmrule;

import java.util.Scanner;

import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.predicates.LoopPredicate;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;

public class TestRule {
    public static void main(String[] args) {
        int d1, d2, d3, d4;
        String quiverPattern;
        if (args.length == 5) {
            quiverPattern = args[0];
            d1 = Integer.parseInt(args[1]);
            d2 = Integer.parseInt(args[2]);
            d3 = Integer.parseInt(args[3]);
            d4 = Integer.parseInt(args[4]);
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter quiver pattern:");
            quiverPattern = sc.nextLine();
            System.out.println("Enter the rules (d1, d2, d3, d4):");
            d1 = sc.nextInt();
            d2 = sc.nextInt();
            d3 = sc.nextInt();
            d4 = sc.nextInt();
            sc.close();
        }

        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(
                quiverPattern.length() <= 0 ? Setting.TEST_BIT_STRING : quiverPattern);
        HaltPredicate predicate = new LoopPredicate(20);

        ECARuleTester.testOneRule(d1, d2, d3, d4, qInit, predicate, 150);
    }
}
