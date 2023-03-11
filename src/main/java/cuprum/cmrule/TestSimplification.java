package cuprum.cmrule;

import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.impl.SimplificationHaltPredicate;
import cuprum.cmrule.tester.ECARuleTester;

public class TestSimplification {
    public static void main(String[] args) {
        ProgramArgumentProcessor argProcessor = new ProgramArgumentProcessor();
        ProgramArgument parsedArgs = argProcessor.addInitialPattern().handleArgument(args);

        // OneDimensionalQuiverInitializer qInit = new
        // OneDimensionalQuiverInitializer(Setting.TEST_BIT_STRING);
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(
                parsedArgs.getInitialPattern().length() <= 0 ? Setting.TEST_BIT_STRING
                        : parsedArgs.getInitialPattern());
        SimplificationHaltPredicate predicate = new SimplificationHaltPredicate();

        ECARuleTester.testAllRules(qInit, predicate, qInit.getName() + "_" + Setting.SIMPLIFICATION_RECORD_FILE, 1000);
    }
}
