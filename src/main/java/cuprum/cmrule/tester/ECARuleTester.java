package cuprum.cmrule.tester;

import java.util.ArrayList;
import java.util.function.Predicate;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.callbacks.SaveDotCallback;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.interfaces.MachineCallback;
import compositionmachine.machine.interfaces.QuiverInitializer;
import cuprum.cmrule.Setting;
import cuprum.cmrule.impl.DetectEdgeCallback;
import cuprum.cmrule.impl.HaltRecordCallback;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.rules.ECARule;

/*
 * simplification (d2 & d3):
 * 2 <-> 4
 * 3 <-> 5
 * 10 <-> 12
 * 11 <-> 7
 */
public class ECARuleTester {
    public static final int STEPS = 500;

    public static void testAllConditions(QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, String fileName, int steps, Predicate<Object[]> acceptPredicate) {
        int totalRules = 1 << (2 + 4 + 4 + 8);
        HaltRecordCallback haltRecordCallback = new HaltRecordCallback();
        ArrayList<String> qInitRecord = new ArrayList<>();
        ArrayList<Integer> ruleRecord = new ArrayList<>();
        ArrayList<Integer> stepRecord = new ArrayList<>();
        do {
            for (int i = 0; i < totalRules; i++) {
                int d1 = (i >> 16) & 0x03;
                int d2 = (i >> 12) & 0x0F;
                int d3 = (i >> 8) & 0x0F;
                int d4 = i & 0xFF;

                if (i % Setting.PRINT_STEP == 0) {
                    String ruleName = d1 + "-" + d2 + "-" + d3 + "-" + d4;
                    System.out.print("Rule: " + ruleName + " -- ");
                }

                ECARule rule = new ECARule(d1, d2, d3, d4);
                CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(qInit, rule, predicate);
                for(MachineCallback cb: callbacks)
                    machine.addCallback(cb);
                // machine.addCallback(new PrintBlockCallback());
                machine.addCallback(haltRecordCallback);
                Object[] quitState = machine.execute(steps);

                if (acceptPredicate.test(quitState)){
                    qInitRecord.add(qInit.getName());
                    ruleRecord.add(i);
                    stepRecord.add((Integer)quitState[quitState.length - 1]);
                }

                if (i % Setting.PRINT_STEP == 0)
                    System.out.println();
            }
        } while (qInit.iterate());

        System.out.println("Writing records...");

        TesterUtil.writeStateAndRuleListToFile(qInitRecord, ruleRecord, stepRecord, fileName);
    }

    public static void testAllRules(QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate,
            String fileName) {
        testAllRules(qInit, predicate, fileName, STEPS);
    }

    public static void testAllRules(QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate, String fileName,
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

    public static void testOneRule(int d1, int d2, int d3, int d4, OneDimensionalQuiverInitializer qInit,
            HaltPredicate predicate, int steps) {
        ECARule rule = new ECARule(d1, d2, d3, d4);
        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(qInit, rule, predicate);

        Config placeholderConfig = new Config();
        placeholderConfig.machineName = qInit.getName() + "_eca_" + d1 + "-" + d2 + "-" + d3 + "-" + d4;
        placeholderConfig.iterationSteps = steps;
        Config.complete(placeholderConfig);
        SaveDotCallback saveDotCallback = new SaveDotCallback();
        DetectEdgeCallback detectMinEdgeCallback = new DetectEdgeCallback(1, 2);
        DetectEdgeCallback detectMaxEdgeCallback = new DetectEdgeCallback(qInit.getName().length()); // hmmmmm

        saveDotCallback.initialize(placeholderConfig);

        machine.addCallback(saveDotCallback);
        machine.addCallback(detectMinEdgeCallback);
        machine.addCallback(detectMaxEdgeCallback);

        machine.execute(steps);

        System.out.println("Quiver: " + qInit.getName());
        System.out.println("Rule: " + "eca_" + d1 + "-" + d2 + "-" + d3 + "-" + d4);
        System.out.println("Done!");
    }
}
