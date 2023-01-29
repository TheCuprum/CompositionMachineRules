package cuprum.cmrule.tester;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.interfaces.MachineCallback;
import compositionmachine.machine.interfaces.QuiverInitializer;
import cuprum.cmrule.RuleUtil;
import cuprum.cmrule.datatype.Tuple;
import cuprum.cmrule.rules.ECARule;
import cuprum.cmrule.tester.record.ECARecord;
import cuprum.cmrule.tester.record.RecordProviderArgs;

public class ECARuleTesterCore {
    // public static <R extends ECARecord> List<R> runAllMatches(
    //         QuiverInitializer<ConnectedQuiver> targetQInit, QuiverInitializer<ConnectedQuiver> startQInit,
    //         HaltPredicate predicate, MachineCallback[] callbacks, int steps,
    //         Function<RecordProviderArgs, R> recordProvider, List<R> recordStorage,
    //         boolean onlyFindOneQuiver, boolean onlyFindOneRule) {
    //     do {
    //         List<R> ruleRecord = runAllConditions(startQInit, predicate, callbacks, steps, recordProvider,
    //                 recordStorage, onlyFindOneQuiver, onlyFindOneRule);
    //         recordStorage.addAll(ruleRecord);
    //     } while (targetQInit.iterate());
    //     return recordStorage;
    // }

    public static <R extends ECARecord> List<R> runAllConditions(
            QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, int steps,
            Function<RecordProviderArgs, R> recordProvider, List<R> recordStorage,
            boolean onlyFindOneQuiver, boolean onlyFindOneRule) {
        do {
            List<R> ruleRecord = runAllRulesSimple(qInit, predicate, callbacks, steps, recordProvider, onlyFindOneRule);
            recordStorage.addAll(ruleRecord);
            if (onlyFindOneQuiver && ruleRecord.size() != 0)
                break;
        } while (qInit.iterate());
        return recordStorage;
    }

    public static <R extends ECARecord> List<R> runAllRules(
            QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, int steps,
            Function<RecordProviderArgs, R> recordProvider, boolean onlyFindOne) {
        int totalRules = 1 << (2 + 4 + 4 + 8);

        ArrayList<R> ruleRecord = new ArrayList<>();
        for (int i = 0; i < totalRules; i++) {
            int d1 = (i >> 16) & 0x03;
            int d2 = (i >> 12) & 0x0F;
            int d3 = (i >> 8) & 0x0F;
            int d4 = i & 0xFF;

            Tuple<CompositionMachine<ConnectedQuiver>, Object[]> quitTuple = runRule(
                    d1, d2, d3, d4, qInit, predicate, callbacks, steps);

            RecordProviderArgs providerArgs = new RecordProviderArgs(
                    i, qInit, quitTuple.getItemA(), quitTuple.getItemB());

            R reocrd = recordProvider.apply(providerArgs);
            if (reocrd != null) {
                ruleRecord.add(reocrd);
                if (onlyFindOne)
                    break;
            }
        }
        return ruleRecord;
    }

    public static <R extends ECARecord> List<R> runAllRulesSimple(
            QuiverInitializer<ConnectedQuiver> qInit, HaltPredicate predicate,
            MachineCallback[] callbacks, int steps,
            Function<RecordProviderArgs, R> recordProvider, boolean onlyFindOne) {
        /*
         * simplification (d2 & d3):
         * 2 <-> 4
         * 3 <-> 5
         * 10 <-> 12
         * 11 <-> 13
         */

        int totalRules = 1 << (2 + 4 + 8);
        ArrayList<R> ruleRecord = new ArrayList<>();
        for (int i = 0; i < totalRules; i++) {
            int d1 = (i >> 12) & 0x03;
            int d2 = (i >> 8) & 0x0F;
            int d3 = TesterUtil.mapDelta2RuleNumber(d2);
            int d4 = i & 0xFF;

            Tuple<CompositionMachine<ConnectedQuiver>, Object[]> quitTuple = runRule(
                    d1, d2, d3, d4, qInit, predicate, callbacks, steps);

            RecordProviderArgs providerArgs = new RecordProviderArgs(
                    RuleUtil.combineECARulePattern(d1, d2, d3, d4), qInit,
                    quitTuple.getItemA(), quitTuple.getItemB());

            R reocrd = recordProvider.apply(providerArgs);
            if (reocrd != null) {
                ruleRecord.add(reocrd);
                if (onlyFindOne)
                    break;
            }
        }
        return ruleRecord;
    }

    public static Tuple<CompositionMachine<ConnectedQuiver>, Object[]> runRule(
            int d1, int d2, int d3, int d4, QuiverInitializer<ConnectedQuiver> qInit,
            HaltPredicate predicate, MachineCallback[] callbacks, int steps) {
        ECARule rule = new ECARule(d1, d2, d3, d4);
        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(qInit, rule, predicate);
        for (MachineCallback cb : callbacks) {
            machine.addCallback(cb);
        }
        Object[] quitState = machine.execute(steps);
        return new Tuple<>(machine, quitState);
    }
}