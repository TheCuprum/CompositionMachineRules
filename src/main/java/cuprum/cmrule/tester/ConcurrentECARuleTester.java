package cuprum.cmrule.tester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.interfaces.MachineCallback;
import cuprum.cmrule.impl.HaltRecordCallback;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.rules.ECARule;

public class ConcurrentECARuleTester {
    public static void testAllConditions(OneDimensionalQuiverInitializer qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, String fileName, int steps, Predicate<Object[]> acceptPredicate,
            int concurrentSize) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(concurrentSize, concurrentSize, 5, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        List<OneDimensionalQuiverInitializer> SubQInitList = qInit.split(concurrentSize);
        List<AllConditionRecord> record = Collections.synchronizedList(new ArrayList<>());
        TestMonitor taskMonitor = new TestMonitor(SubQInitList);

        for (int index = 0 ; index < SubQInitList.size(); index++) {
            System.out.println("Start Position-" + index + ": " + SubQInitList.get(index).getName());
        }
        System.out.println();

        // int totalRules = 1 << (2 + 4 + 4 + 8);
        int totalRules = 1 << (2 + 4 + 8);
        for (OneDimensionalQuiverInitializer subQInit : SubQInitList) {
            HaltRecordCallback haltRecordCallback = new HaltRecordCallback(false);

            executor.execute(() -> {
                do {
                    for (int i = 0; i < totalRules; i++) {
                        // int d1 = (i >> 16) & 0x03;
                        // int d2 = (i >> 12) & 0x0F;
                        // int d3 = (i >> 8) & 0x0F;
                        // int d4 = i & 0xFF;
                        int d1 = (i >> 12) & 0x03;
                        int d2 = (i >> 8) & 0x0F;
                        int d3 = TesterUtil.mapDelta2RuleNumber(d2);
                        int d4 = i & 0xFF;

                        ECARule rule = new ECARule(d1, d2, d3, d4);
                        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(qInit, rule,
                                predicate);
                        for (MachineCallback cb : callbacks)
                            machine.addCallback(cb);
                        // machine.addCallback(new PrintBlockCallback());
                        machine.addCallback(haltRecordCallback);
                        Object[] quitState = machine.execute(steps);

                        if (acceptPredicate.test(quitState)) {
                            record.add(new AllConditionRecord(
                                    subQInit.getName(),
                                    Integer.valueOf((d1 << 16) + (d2 << 12) + (d3 << 8) + d4),
                                    (Integer) quitState[quitState.length - 1]));
                        }
                    }
                } while (subQInit.iterate());
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
            //     System.out.println("Write interrupted, there's no record written to the file \"" + fileName + "\"");
            // }
        }));
    
        taskMonitor.monitor(2000);
    }
}
