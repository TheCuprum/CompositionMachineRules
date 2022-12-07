package cuprum.cmrule;

import cuprum.cmrule.impl.OneEdgeQuiverInitializer;
import cuprum.cmrule.impl.SimplificationHaltPredicate;
import cuprum.cmrule.tester.ECARuleTester;

public class TestSimplification {
    public static void main(String[] args) {
        OneEdgeQuiverInitializer qInit = new OneEdgeQuiverInitializer();
        SimplificationHaltPredicate predicate = new SimplificationHaltPredicate();
        
        ECARuleTester.testAll(qInit, predicate, Setting.SIMPLIFICATION_RECORD_FILE, 5000);
    }
}
