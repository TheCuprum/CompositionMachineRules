package cuprum.cmrule.tester;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.Arrow;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.callbacks.SaveDotCallback;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.interfaces.MachineCallback;
import compositionmachine.machine.interfaces.QuiverInitializer;
import compositionmachine.machine.predicates.LoopPredicate;
import cuprum.cmrule.Setting;
import cuprum.cmrule.impl.DetectEdgeCallback;
import cuprum.cmrule.impl.HaltRecordCallback;
import cuprum.cmrule.impl.MatchOrLoopHaltPredicate;
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
    public static final int CHECK_POOL_MILLI = 5000;
    public static final int CHECK_POOL_MILLI_SHORT = 1000;

    /**
     * Do not call this method unless you know it is a real instance of
     * ConnectedQuiver.
     * 
     * @param <CQ>
     * @param quiver
     * @return
     */
    public static <CQ extends BaseConnectedQuiver<CQ>> String connectedQuiverToString(Quiver<CQ> quiver) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < quiver.size(); index++) {
            if (index > 0)
                sb.append(',');
            BaseConnectedQuiver<CQ> cq = quiver.get(index);
            Iterator<Arrow> arrIter = cq.getArrowIterator();
            while (arrIter.hasNext()) {
                Arrow arrow = arrIter.next();
                sb.append(cq.getArrowState(arrow) > 0 ? '1' : '0');
            }
        }
        return sb.toString();
    }

    public static void categorizeAllMatchesConcurrent(
            OneDimensionalQuiverInitializer startQInit,
            String dataDirectory, String fileNamePostfix, int maxSteps,
            int concurrentSize, int ioThreads, int monitorMilliInterval) {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(
                concurrentSize, concurrentSize, 5, TimeUnit.SECONDS, taskQueue);

        LoopPredicate predicate = new LoopPredicate();
        Map<String, Set<AllConditionRecord>> recordSetMap = Collections.synchronizedMap(new TreeMap<>());
        List<OneDimensionalQuiverInitializer> subQInitList = startQInit.split(concurrentSize);

        if (ioThreads > 0) {
            Thread recordCollectThreadMain;
            Runtime.getRuntime().addShutdownHook(recordCollectThreadMain = new Thread(() -> {
                System.out.println("Shutting down...");
                try {
                    taskExecutor.shutdown();
                    taskExecutor.awaitTermination(10, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TesterUtil.writeRecordMapConcurrent(ioThreads, recordSetMap,
                        Path.of(Setting.DATA_PATH, dataDirectory).toString(), fileNamePostfix, CHECK_POOL_MILLI_SHORT);
            }, "Record Collect Thread-Main"));
        }

        Thread taskSubmitThread = new Thread(() -> {
            for (OneDimensionalQuiverInitializer subQInit : subQInitList) {
                taskExecutor.execute(() -> {
                    while (subQInit.isAvailable()) {
                        String startQuiverName = subQInit.getName();
                        ECARuleTesterCore.runAllRulesSimple(
                                subQInit, predicate, new MachineCallback[0], maxSteps,
                                (RecordProviderArgs args) -> {
                                    Map<Integer, Quiver<ConnectedQuiver>> history = args.getMachine()
                                            .getQuiverHistory();
                                    for (int step = 1; step < history.size(); step++) {
                                        String statePattern = ECARuleTester.connectedQuiverToString(history.get(step));
                                        synchronized (recordSetMap) {
                                            Set<AllConditionRecord> recordSet = recordSetMap.get(statePattern);
                                            if (recordSet == null) {
                                                recordSet = Collections.synchronizedSet(new TreeSet<>());
                                                recordSetMap.put(statePattern, recordSet);
                                            }
                                            recordSet.add(new AllConditionRecord(
                                                    startQuiverName, args.getRulePattern(), step));
                                        }
                                    }
                                    return null;
                                }, false);
                        subQInit.iterate();
                    }
                });

                while (taskQueue.size() > concurrentSize / 2) {
                    try {
                        Thread.sleep(CHECK_POOL_MILLI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            taskExecutor.shutdown();
        }, "Task Submit Thread");
        taskSubmitThread.start();

        Thread monitorThread = new Thread(() -> {
            // List<OneDimensionalQuiverInitializer> qList = new ArrayList<>();
            // qList.add(startQInit);
            // TestMonitor taskMonitor = new TestMonitor(qList);
            TestMonitor taskMonitor = new TestMonitor(subQInitList);
            taskMonitor.monitor(monitorMilliInterval);
        }, "Task Monitor Thread");
        monitorThread.run();

        try {
            taskExecutor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testAllMatchesConcurrent(
            OneDimensionalQuiverInitializer targetQInit, OneDimensionalQuiverInitializer startQInit,
            String dataDirectory, String fileNamePostfix, int maxSteps,
            int concurrentSize, int monitorMilliInterval, boolean writeResult) {

        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                concurrentSize, concurrentSize, 5, TimeUnit.SECONDS, taskQueue);

        Thread taskSubmitThread = new Thread(() -> {
            while (targetQInit.isAvailable()) {
                Quiver<ConnectedQuiver> targetQuiver = targetQInit.generateQuiver();
                MatchQuiverCallback<ConnectedQuiver> callback = new MatchQuiverCallback<>(targetQuiver);
                MatchOrLoopHaltPredicate<ConnectedQuiver> predicate = new MatchOrLoopHaltPredicate<>(targetQuiver);


                Set<AllConditionRecord> record = Collections.synchronizedSet(new TreeSet<>());
                List<OneDimensionalQuiverInitializer> SubQInitList = startQInit.split(concurrentSize);
                CountDownLatch latch = new CountDownLatch(SubQInitList.size());

                String qInitName = targetQInit.getName();
                if (writeResult) {
                    Thread recordCollectThread = new Thread(() -> {
                        try {
                            latch.await();
                            TesterUtil.writeRecordsToFile(record,
                                    Path.of(Setting.DATA_PATH, dataDirectory).toString(),
                                    qInitName + "_" + fileNamePostfix);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "Record Collect Thread-" + qInitName);
                    recordCollectThread.start();
                }

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
            int concurrentSize, boolean writeResult) {
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

        if (writeResult) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Writing records...");
                // try {
                executor.shutdown();
                // executor.wait(60 * 1000);
                // record.sort((AllConditionRecord o1, AllConditionRecord o2) -> {
                // return o1.compareTo(o2);
                // });
                TesterUtil.writeRecordsToFile(record, fileName);
                // } catch (InterruptedException ie) {
                // System.out.println("Write interrupted, there's no record written to the file
                // \"" + fileName + "\"");
                // }
            }));
        }

        TestMonitor taskMonitor = new TestMonitor(SubQInitList);
        taskMonitor.monitor(2000);
    }

    public static void testAllConditions(OneDimensionalQuiverInitializer qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, String fileName, int maxSteps, Predicate<Object[]> acceptPredicate,
            boolean writeResult) {
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

        if (writeResult) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Writing records...");
                TesterUtil.writeRecordsToFile(conditionRecord, fileName);
            }));
        }

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
