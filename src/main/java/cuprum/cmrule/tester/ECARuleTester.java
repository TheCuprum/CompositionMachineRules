package cuprum.cmrule.tester;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.callbacks.SaveDotCallback;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.interfaces.MachineCallback;
import compositionmachine.machine.interfaces.QuiverInitializer;
import cuprum.cmrule.Setting;
import cuprum.cmrule.impl.DetectEdgeCallback;
import cuprum.cmrule.impl.HaltRecordCallback;
import cuprum.cmrule.impl.MatchOrSimpHaltPredicate;
import cuprum.cmrule.impl.MatchQuiverCallback;
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
    public static final int CHECK_POOL_MILLI = 10000;

    public static void testAllMatchesConcurrent(
            OneDimensionalQuiverInitializer targetQInit, OneDimensionalQuiverInitializer startQInit,
            String dataDirectory, String fileNamePostfix, int maxSteps,
            int concurrentSize, int monitorMilliInterval) {

        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                concurrentSize, concurrentSize, 5, TimeUnit.SECONDS, taskQueue);

        Thread taskSubmitThread = new Thread(() -> {
            while (targetQInit.isAvailable()) {
                Quiver<ConnectedQuiver> targetQuiver = targetQInit.generateQuiver();
                MatchQuiverCallback<ConnectedQuiver> callback = new MatchQuiverCallback<>(targetQuiver);
                MatchOrSimpHaltPredicate<ConnectedQuiver> predicate = new MatchOrSimpHaltPredicate<>(targetQuiver);

                // List<AllConditionRecord> record = Collections.synchronizedList(new
                // ArrayList<>());
                Set<AllConditionRecord> record = Collections.synchronizedSet(new TreeSet<>());
                List<OneDimensionalQuiverInitializer> SubQInitList = startQInit.split(concurrentSize);
                CountDownLatch latch = new CountDownLatch(SubQInitList.size());

                String qInitName = targetQInit.getName();
                Thread recordCollectThread = new Thread(() -> {
                    try {
                        latch.await();
                        // record.sort((AllConditionRecord o1, AllConditionRecord o2) -> {
                        //     return o1.compareTo(o2);
                        // });
                        TesterUtil.writeRecordsToFile(record,
                                Path.of(Setting.DATA_PATH, dataDirectory).toString(),
                                qInitName + "_" + fileNamePostfix);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "Record Collect Thread-" + qInitName);
                recordCollectThread.start();

                for (OneDimensionalQuiverInitializer subQInit : SubQInitList) {
                    executor.execute(() -> {
                        ECARuleTesterCore.runAllConditions(
                                subQInit, predicate, new MachineCallback[] { callback }, maxSteps,
                                getRecordProvider((Object[] haltReturnValue) -> {
                                    if (haltReturnValue.length > 0 && haltReturnValue[1] != null)
                                        return true;
                                    else
                                        return false;
                                }), record, false, false);
                        latch.countDown();
                    });
                }

                while (taskQueue.size() >= concurrentSize) {
                    try {
                        Thread.sleep(CHECK_POOL_MILLI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                targetQInit.iterate();
            }
        }, "Task Submit Thread");
        taskSubmitThread.start();

        Thread monitorThread = new Thread(() -> {
            List<OneDimensionalQuiverInitializer> qList = new ArrayList<>();
            qList.add(targetQInit);
            TestMonitor taskMonitor = new TestMonitor(qList);
            taskMonitor.monitor(monitorMilliInterval);
        }, "Task Monitor Thread");
        monitorThread.run();

        try {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testAllConditionsConcurrent(OneDimensionalQuiverInitializer qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, String fileName, int maxSteps, Predicate<Object[]> acceptPredicate,
            int concurrentSize) {
        List<OneDimensionalQuiverInitializer> SubQInitList = qInit.split(concurrentSize);
        for (int index = 0; index < SubQInitList.size(); index++) {
            System.out.println("Start Position-" + index + ": " + SubQInitList.get(index).getName());
        }
        System.out.println();

        // List<AllConditionRecord> record = Collections.synchronizedList(new
        // ArrayList<>());
        Set<AllConditionRecord> record = Collections.synchronizedSet(new TreeSet<>());
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                concurrentSize, concurrentSize, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        Thread taskSubmitThread = new Thread(() -> {
            for (OneDimensionalQuiverInitializer subQInit : SubQInitList) {
                executor.execute(() -> {
                    ECARuleTesterCore.runAllConditions(subQInit, predicate, callbacks, maxSteps,
                            getRecordProvider(acceptPredicate), record,
                            false, false);
                });
            }
        });
        taskSubmitThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("Writing records...");
            // try {
            executor.shutdown();
            // executor.wait(60 * 1000);
            // record.sort((AllConditionRecord o1, AllConditionRecord o2) -> {
            //     return o1.compareTo(o2);
            // });
            TesterUtil.writeRecordsToFile(record, fileName);
            // } catch (InterruptedException ie) {
            // System.out.println("Write interrupted, there's no record written to the file
            // \"" + fileName + "\"");
            // }
        }));

        TestMonitor taskMonitor = new TestMonitor(SubQInitList);
        taskMonitor.monitor(2000);
    }

    public static void testAllConditions(OneDimensionalQuiverInitializer qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, String fileName, int maxSteps, Predicate<Object[]> acceptPredicate) {
        // List<AllConditionRecord> conditionRecord = Collections.synchronizedList(new
        // ArrayList<>());
        Set<AllConditionRecord> conditionRecord = Collections.synchronizedSet(new TreeSet<>());

        List<OneDimensionalQuiverInitializer> qInitList = new ArrayList<>();
        qInitList.add(qInit);
        TestMonitor taskMonitor = new TestMonitor(qInitList);

        Thread taskThread = new Thread(() -> {
            ECARuleTesterCore.runAllConditions(qInit, predicate, callbacks, maxSteps,
                    getRecordProvider(acceptPredicate), conditionRecord,
                    false, false);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("Writing records...");
            TesterUtil.writeRecordsToFile(conditionRecord, fileName);
        }));

        taskThread.start();
        taskMonitor.monitor(5000);
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
        TesterUtil.writeRecordsToFile(ruleRecord, fileName);
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
