package cuprum.cmrule.tester;

import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.interfaces.QuiverInitializer;
import cuprum.cmrule.Setting;
import cuprum.cmrule.impl.HaltRecordCallback;
import cuprum.cmrule.rules.ECARule;

public class ECARuleTester {
    public static final int STEPS = 500;

    public static void testAll(QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate, String fileName) {
        testAll(qInit, predicate, fileName, STEPS);
    }

    public static void testAll(QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate, String fileName,
            int steps) {
        int totalRules = 1 << (2 + 4 + 4 + 8);

        HaltRecordCallback haltCallback = new HaltRecordCallback();

        for (int i = 0; i < totalRules; i++) {
            int d1 = (i >> 16) & 0x03;
            int d2 = (i >> 12) & 0x0F;
            int d3 = (i >> 8) & 0x0F;
            int d4 = i & 0xFF;

            if (i % Setting.PRINT_STEP == 0) {
                String ruleName = d1 + "-" + d2 + "-" + d3 + "-" + d4;
                System.out.print("Rule: " + ruleName + " -- ");
            }

            // haltCallback.setRule(i);
            ECARule rule = new ECARule(d1, d2, d3, d4);
            // NotXorRule126 rule = new NotXorRule126();
            CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(qInit, rule, predicate);
            machine.addCallback(haltCallback);
            // machine.addCallback(new PrintBlockCallback());
            machine.execute(steps);

            if (i % Setting.PRINT_STEP == 0)
                System.out.println();
        }

        System.out.println("Writing records...");

        Util.writeRuleListToFile(haltCallback.getRecord(), fileName);
    }
}
