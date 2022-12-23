package cuprum.cmrule;

import compositionmachine.machine.predicates.LoopPredicate;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;

public class TestHalt {
    public static void main(String[] args) {
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer();
        LoopPredicate predicate = new LoopPredicate();

        ECARuleTester.testAll(qInit, predicate, Setting.HALT_RECORD_FILE);
    }
}
