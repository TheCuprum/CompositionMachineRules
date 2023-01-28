package cuprum.cmrule.tester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.callbacks.SaveDotCallback;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.interfaces.MachineCallback;
import compositionmachine.machine.interfaces.QuiverInitializer;
import cuprum.cmrule.Setting;
import cuprum.cmrule.impl.DetectEdgeCallback;
import cuprum.cmrule.impl.HaltRecordCallback;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.record.AllConditionRecord;
import cuprum.cmrule.tester.record.AllRuleRecord;
import cuprum.cmrule.tester.record.RecordProviderArgs;

/*
 * simplification (d2 & d3):
 * 2 <-> 4
 * 3 <-> 5
 * 10 <-> 12
 * 11 <-> 7
 */
public class ECARuleTester {
    public static final int STEPS = 500;

    public static void testAllConditionsConcurrent(OneDimensionalQuiverInitializer qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, String fileName, int steps, Predicate<Object[]> acceptPredicate,
            int concurrentSize) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                concurrentSize, concurrentSize, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        List<OneDimensionalQuiverInitializer> SubQInitList = qInit.split(concurrentSize);
        List<AllConditionRecord> record = Collections.synchronizedList(new ArrayList<>());
        TestMonitor taskMonitor = new TestMonitor(SubQInitList);

        for (int index = 0; index < SubQInitList.size(); index++) {
            System.out.println("Start Position-" + index + ": " + SubQInitList.get(index).getName());
        }
        System.out.println();

        for (OneDimensionalQuiverInitializer subQInit : SubQInitList) {
            executor.execute(() -> {
                ECARuleTesterCore.runAllConditions(subQInit, predicate, callbacks, steps,
                    getRecordProvider(acceptPredicate), record,
                    false, false);
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("Writing records...");
            // try {
            executor.shutdown();
            // executor.wait(60 * 1000);
            record.sort((AllConditionRecord o1, AllConditionRecord o2) -> {
                return o1.compareTo(o2);
            });
            TesterUtil.writeRecordListToFile(record, fileName);
            // } catch (InterruptedException ie) {
            // System.out.println("Write interrupted, there's no record written to the file
            // \"" + fileName + "\"");
            // }
        }));

        taskMonitor.monitor(2000);
    }

    public static void testAllConditions(OneDimensionalQuiverInitializer qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, String fileName, int steps, Predicate<Object[]> acceptPredicate) {
        List<AllConditionRecord> conditionRecord = Collections.synchronizedList(new ArrayList<>());

        List<OneDimensionalQuiverInitializer> qInitList = new ArrayList<>();
        qInitList.add(qInit);
        TestMonitor taskMontor = new TestMonitor(qInitList);

        Thread taskThread = new Thread(() -> {
            ECARuleTesterCore.runAllConditions(qInit, predicate, callbacks, steps,
                    getRecordProvider(acceptPredicate), conditionRecord,
                    false, false);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("Writing records...");
            TesterUtil.writeRecordListToFile(conditionRecord, fileName);
        }));

        taskThread.run();
        taskMontor.monitor(5000);
    }

    private static Function<RecordProviderArgs, AllConditionRecord> getRecordProvider(
            Predicate<Object[]> acceptPredicate) {
        return (RecordProviderArgs args) -> {
            Object[] quitState = args.getQuitState();
            if (acceptPredicate.test(quitState)) {
                return new AllConditionRecord(
                        args.getQInit().getName(),
                        Integer.valueOf(args.getRulePattern()),
                        (Integer) quitState[0]);
            }
            return null;
        };
    }

    public static void testAllRules(QuiverInitializer<ConnectedQuiver> qInit,
            HaltPredicate predicate, String fileName) {
        testAllRules(qInit, predicate, fileName, STEPS);
    }

    public static void testAllRules(QuiverInitializer<ConnectedQuiver> qInit,
            HaltPredicate predicate, String fileName, int steps) {
        HaltRecordCallback haltCallback = new HaltRecordCallback();
        List<AllRuleRecord> ruleRecord = ECARuleTesterCore.runAllRules(qInit, predicate,
                new MachineCallback[] {
                        // new PrintBlockCallback(),
                        haltCallback
                }, steps,
                (RecordProviderArgs args) -> {
                    int rulePattern = args.getRulePattern();
                    Object[] quitState = args.getQuitState();

                    if (rulePattern % Setting.PRINT_STEP == 0)
                        logRule(rulePattern);

                    if (quitState.length > 0) {
                        return new AllRuleRecord(rulePattern, (Integer) quitState[0]);
                    }
                    return null;
                }, false);

        System.out.println("Writing records...");
        TesterUtil.writeRecordListToFile(ruleRecord, fileName);
        // TesterUtil.writeRuleListToFile(ruleRecord, fileName);
    }

    private static void logRule(int rulePattern) {
        int d1 = (rulePattern >> 16) & 0x03;
        int d2 = (rulePattern >> 12) & 0x0F;
        int d3 = (rulePattern >> 8) & 0x0F;
        int d4 = rulePattern & 0xFF;
        String ruleName = d1 + "-" + d2 + "-" + d3 + "-" + d4;
        System.out.println("Rule: " + ruleName + " -- ");

    }

    public static void testOneRule(int d1, int d2, int d3, int d4, OneDimensionalQuiverInitializer qInit,
            HaltPredicate predicate, int steps) {
        Config placeholderConfig = new Config();
        placeholderConfig.machineName = qInit.getName() + "_eca_" + d1 + "-" + d2 + "-" + d3 + "-" + d4;
        placeholderConfig.iterationSteps = steps;
        Config.complete(placeholderConfig);
        SaveDotCallback saveDotCallback = new SaveDotCallback();
        DetectEdgeCallback detectMinEdgeCallback = new DetectEdgeCallback(1, 2);
        DetectEdgeCallback detectMaxEdgeCallback = new DetectEdgeCallback(qInit.getName().length()); // hmmmmm

        saveDotCallback.initialize(placeholderConfig);

        ECARuleTesterCore.runRule(d1, d2, d3, d4, qInit, predicate,
                new MachineCallback[] { saveDotCallback, detectMinEdgeCallback, detectMaxEdgeCallback },
                steps);

        System.out.println("Quiver: " + qInit.getName());
        System.out.println("Rule: " + "eca_" + d1 + "-" + d2 + "-" + d3 + "-" + d4);
        System.out.println("Done!");
    }
}
