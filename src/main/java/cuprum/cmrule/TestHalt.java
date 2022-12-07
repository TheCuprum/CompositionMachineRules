package cuprum.cmrule;

import compositionmachine.machine.predicates.LoopPredicate;
import cuprum.cmrule.impl.OneEdgeQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;

public class TestHalt {
    public static void main(String[] args) {
        OneEdgeQuiverInitializer qInit = new OneEdgeQuiverInitializer();
        LoopPredicate predicate = new LoopPredicate();

        ECARuleTester.testAll(qInit, predicate, Setting.HALT_RECORD_FILE);
    }
}
