package cuprum.cmrule;

import java.util.Scanner;

import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.predicates.LoopPredicate;
import cuprum.cmrule.impl.OneEdgeQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;

public class TestRule {
    public static void main(String[] args) {
        int d1, d2, d3, d4;
        if (args.length == 4) {
            d1 = Integer.parseInt(args[0]);
            d2 = Integer.parseInt(args[1]);
            d3 = Integer.parseInt(args[2]);
            d4 = Integer.parseInt(args[3]);
        } else {
            System.out.println("Enter the rules (d1, d2, d3, d4):");
            Scanner sc = new Scanner(System.in);
            d1 = sc.nextInt();
            d2 = sc.nextInt();
            d3 = sc.nextInt();
            d4 = sc.nextInt();
            sc.close();
        }

        OneEdgeQuiverInitializer qInit = new OneEdgeQuiverInitializer();
        HaltPredicate predicate = new LoopPredicate(20);

        ECARuleTester.testOne(d1, d2, d3, d4, qInit, predicate, 150);
    }
}
