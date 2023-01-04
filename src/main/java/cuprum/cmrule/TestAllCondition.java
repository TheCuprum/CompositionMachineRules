package cuprum.cmrule;

import java.util.Scanner;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.MachineCallback;
import cuprum.cmrule.impl.MatchAndSimpHaltPredicate;
import cuprum.cmrule.impl.MatchQuiverCallback;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;

public class TestAllCondition {
    public static void main(String[] args) {
        String quiverPattern;
        if (args.length == 1) {
            quiverPattern = args[0];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter quiver pattern to match:");
            quiverPattern = sc.nextLine();
            sc.close();
        }
        OneDimensionalQuiverInitializer tempQInit = new OneDimensionalQuiverInitializer(quiverPattern);
        Quiver<ConnectedQuiver> matchQuiver = tempQInit.generateQuiver();
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer();
        MatchQuiverCallback<ConnectedQuiver> callback = new MatchQuiverCallback<>(matchQuiver);
        MatchAndSimpHaltPredicate<ConnectedQuiver> predicate = new MatchAndSimpHaltPredicate<>(matchQuiver);

        ECARuleTester.testAllConditions(qInit, predicate, new MachineCallback[] { callback }, quiverPattern, 1000,
                (Object[] haltReturnValue) -> {
                    if (haltReturnValue[1] != null)
                        return true;
                    else
                        return false;
                });
    }
}
