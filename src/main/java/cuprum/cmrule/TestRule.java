package cuprum.cmrule;

import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.predicates.LoopPredicate;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;

public class TestRule {
    public static void main(String[] args) {
        ProgramArgumentProcessor argProcessor = new ProgramArgumentProcessor();
        ProgramArgument parsedArgs = argProcessor
                .addInitialPattern().addRule()
                .addExtraField("step", "Enter iteration steps:")
                .handleArgument(args);

        int[] rulePattern = parsedArgs.getRulePattern();
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(
                parsedArgs.getInitialPattern().length() <= 0 ? Setting.TEST_BIT_STRING
                        : parsedArgs.getInitialPattern());
        HaltPredicate predicate = new LoopPredicate(20);

        ECARuleTester.testOneRule(
                rulePattern[0], rulePattern[1], rulePattern[2], rulePattern[3],
                qInit, predicate,
                parsedArgs.getExtraFieldAsInteger("step") > 0 ? parsedArgs.getExtraFieldAsInteger("step").intValue() : 150);
    }
}
