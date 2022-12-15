package cuprum.cmrule.tester;

import java.util.ArrayList;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.callbacks.SaveDotCallback;
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
        ArrayList<Integer> ruleRecord = new ArrayList<>();

        /*
         * simplification (d2 & d3):
         * 2 <-> 4
         * 3 <-> 5
         * 10 <-> 12
         * 11 <-> 7
         */

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
            Object[] quitState = machine.execute(steps);

            if (quitState.length > 0)
                ruleRecord.add(i);

            if (i % Setting.PRINT_STEP == 0)
                System.out.println();
        }

        System.out.println("Writing records...");

        TesterUtil.writeRuleListToFile(ruleRecord, fileName);
    }

    public static void testOne(int d1, int d2, int d3, int d4, QuiverInitializer<ConnectedQuiver> qInit,
            HaltPredicate predicate, int steps) {
        ECARule rule = new ECARule(d1, d2, d3, d4);
        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(qInit, rule, predicate);

        Config placeholderConfig = new Config();
        placeholderConfig.machineName = "eca_" + d1 + "-" + d2 + "-" + d3 + "-" + d4;
        placeholderConfig.iterationSteps = steps;
        Config.complete(placeholderConfig);
        SaveDotCallback saveDotCallback = new SaveDotCallback();
        saveDotCallback.initialize(placeholderConfig);

        machine.addCallback(saveDotCallback);

        machine.execute(steps);

        System.out.println("Rule: " + "eca_" + d1 + "-" + d2 + "-" + d3 + "-" + d4 + " Done!");
    }
}
