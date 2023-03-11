package cuprum.cmrule;

import java.util.Scanner;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.MachineCallback;
import cuprum.cmrule.impl.MatchOrSimpHaltPredicate;
import cuprum.cmrule.impl.MatchQuiverCallback;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;
import cuprum.cmrule.tester.TesterUtil;

public class TestAllCondition {
    public static void main(String[] args) {
        ProgramArgumentProcessor argProcessor = new ProgramArgumentProcessor();
        ProgramArgument parsedArgs = argProcessor.addTargetPattern().addInitialPattern().handleArgument(args);

        String matchQuiverPattern = parsedArgs.getTargetPattern();
        String startQuiverPattern = parsedArgs.getInitialPattern();
        if (startQuiverPattern.equals("-"))
            startQuiverPattern = "0".repeat(matchQuiverPattern.length());

        if (startQuiverPattern.length() != matchQuiverPattern.length())
            throw new IllegalArgumentException("The quiver patterns must match.");

        TesterUtil.addTimer();

        OneDimensionalQuiverInitializer tempQInit = new OneDimensionalQuiverInitializer(matchQuiverPattern);
        Quiver<ConnectedQuiver> matchQuiver = tempQInit.generateQuiver();
        // OneDimensionalQuiverInitializer qInit = new
        // OneDimensionalQuiverInitializer("0".repeat(matchQuiverPattern.length()));
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(startQuiverPattern);
        // qInit.setTerminateState("0".repeat(startQuiverPattern.length()));
        MatchQuiverCallback<ConnectedQuiver> callback = new MatchQuiverCallback<>(matchQuiver);
        MatchOrSimpHaltPredicate<ConnectedQuiver> predicate = new MatchOrSimpHaltPredicate<>(matchQuiver);

        ECARuleTester.testAllConditions(qInit, predicate, new MachineCallback[] { callback },
                matchQuiverPattern + "_" + Setting.ALL_CONDITION_RECORD_FILE, 1000,
                (Object[] haltReturnValue) -> {
                    if (haltReturnValue.length > 0 && haltReturnValue[1] != null)
                        return true;
                    else
                        return false;
                });
    }
}
