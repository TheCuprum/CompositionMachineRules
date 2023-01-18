package cuprum.cmrule;

import java.util.Scanner;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.MachineCallback;
import cuprum.cmrule.impl.MatchOrSimpHaltPredicate;
import cuprum.cmrule.impl.MatchQuiverCallback;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ConcurrentECARuleTester;

public class TestAllConditionConcurrent {
    public static void main(String[] args) {
        String matchQuiverPattern;
        String startQuiverPattern;
        if (args.length == 2) {
            matchQuiverPattern = args[0];
            startQuiverPattern = args[1];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter quiver pattern to match:");
            matchQuiverPattern = sc.nextLine();
            System.out.println("Enter initial quiver pattern:");
            startQuiverPattern = sc.nextLine();
            if (startQuiverPattern.equals("-"))
                startQuiverPattern = "0".repeat(matchQuiverPattern.length());
            sc.close();
        }

        if (startQuiverPattern.length() != matchQuiverPattern.length())
            throw new IllegalArgumentException("The quiver patterns must match.");

        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long endTime = System.currentTimeMillis();
            long deltaTime = endTime - startTime;
            long day = deltaTime / (1000 * 60 * 60 * 24);
            long hour = deltaTime / (1000 * 60 * 60) % 24;
            long minute = deltaTime / (1000 * 60) % 60;
            long second = deltaTime / 1000 % 60;
            long milli = deltaTime % 1000;
            System.out.println(String.format("Total Time: %d:%d:%d:%d.%d", day, hour, minute, second, milli));
        }));

        OneDimensionalQuiverInitializer tempQInit = new OneDimensionalQuiverInitializer(matchQuiverPattern);
        Quiver<ConnectedQuiver> matchQuiver = tempQInit.generateQuiver();
        // OneDimensionalQuiverInitializer qInit = new
        // OneDimensionalQuiverInitializer("0".repeat(matchQuiverPattern.length()));
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(startQuiverPattern);
        // qInit.setTerminateState("0".repeat(startQuiverPattern.length()));
        MatchQuiverCallback<ConnectedQuiver> callback = new MatchQuiverCallback<>(matchQuiver);
        MatchOrSimpHaltPredicate<ConnectedQuiver> predicate = new MatchOrSimpHaltPredicate<>(matchQuiver);

        ConcurrentECARuleTester.testAllConditions(
                qInit,
                predicate,
                new MachineCallback[] { callback },
                matchQuiverPattern + "_" + Setting.ALL_CONDITION_RECORD_FILE, 1000,
                (Object[] haltReturnValue) -> {
                    if (haltReturnValue.length > 0 && haltReturnValue[1] != null)
                        return true;
                    else
                        return false;
                },
                4);

        System.exit(0);
    }
}
