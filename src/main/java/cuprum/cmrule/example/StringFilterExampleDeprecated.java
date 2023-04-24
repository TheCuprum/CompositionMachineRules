package cuprum.cmrule.example;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.predicates.NullPredicate;
import cuprum.cmrule.application.GeneralFunction;
import cuprum.cmrule.application.OneDimFunctionMapper;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.rules.ECARule;

@Deprecated
public class StringFilterExampleDeprecated {
    public static GeneralFunction getStringFunction(String appendString) {
        return new GeneralFunction(String.class, String.class, (o) -> ((String) o).concat(appendString));
    }

    public static GeneralFunction[] generateFunctionList(String inputString) {
        GeneralFunction[] functionList = new GeneralFunction[inputString.length()];
        for (int index = 0; index < inputString.length(); index++) {
            functionList[index] = getStringFunction(inputString.substring(index, index + 1));
        }
        return functionList;
    }

    public static void main(String[] args) {
        int[] ruleNumber = new int[4];
        String initialQuiverPattern;
        int steps;
        if (args.length == 6) {
            initialQuiverPattern = args[0];
            ruleNumber[0] = Integer.parseInt(args[1]);
            ruleNumber[1] = Integer.parseInt(args[2]);
            ruleNumber[2] = Integer.parseInt(args[3]);
            ruleNumber[3] = Integer.parseInt(args[4]);
            steps = Integer.parseInt(args[5]);
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter quiver pattern:");
            initialQuiverPattern = sc.nextLine();
            System.out.println("Enter the rules (d1, d2, d3, d4):");
            ruleNumber[0] = sc.nextInt();
            ruleNumber[1] = sc.nextInt();
            ruleNumber[2] = sc.nextInt();
            ruleNumber[3] = sc.nextInt();
            System.out.println("Enter iteration steps:");
            steps = sc.nextInt();
            sc.close();
        }

        // String funcString = "asdfuhellolihfdu";
        String funcString = "fuhellol";
        // String initialQuiverPattern = "00000000";
        // int[] ruleNumber = new int[] { 0, 1, 1, 247 };
        // String matchQuiverPattern = "00111110";
        // int steps = 3;

        ArrayList<GeneralFunction[]> funcList = new ArrayList<>();
        GeneralFunction[] funcs = generateFunctionList(funcString);
        funcList.add(funcs);

        // if (funcs.length != initialQuiverPattern.length() || funcs.length !=
        // matchQuiverPattern.length()) {
        // System.err.println("Pattern size mismatch.");
        // System.exit(0);
        // }

        if (funcs.length != initialQuiverPattern.length()) {
            System.err.println("Pattern size mismatch.");
            System.exit(0);
        }

        OneDimFunctionMapper mapper = new OneDimFunctionMapper(funcList);
        Quiver<ConnectedQuiver> initialQuiver = OneDimensionalQuiverInitializer.genQuiver(initialQuiverPattern);
        // Quiver<ConnectedQuiver> matchQuiver =
        // OneDimensionalQuiverInitializer.genQuiver(matchQuiverPattern);

        // MatchAndSimpHaltPredicate<ConnectedQuiver> predicate = new
        // MatchAndSimpHaltPredicate<>(matchQuiver);
        // MatchQuiverCallback<ConnectedQuiver> matchQuiverCallback = new
        // MatchQuiverCallback<>(matchQuiver);
        // HaltRecordCallback haltRecordCallback = new HaltRecordCallback();
        // CompositionMachine<ConnectedQuiver> machine =
        // CompositionMachine.createMachine(initialQuiver,
        // new ECARule(ruleNumber[0], ruleNumber[1], ruleNumber[2], ruleNumber[3]),
        // predicate);
        // machine.addCallback(matchQuiverCallback);
        // machine.addCallback(haltRecordCallback);

        // Object[] result = machine.execute(steps);

        // if (result.length > 0){
        // GeneralFunction[] outFunctions =
        // mapper.mapFunctions((Quiver<ConnectedQuiver>)result[1]);
        // System.out.println("Output function count: " + outFunctions.length);
        // System.out.println("Test input: " + "[empty string]");
        // for (GeneralFunction fn : outFunctions) {
        // System.out.println("Output: " + fn.apply(""));
        // }
        // }

        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(initialQuiver,
                new ECARule(ruleNumber[0], ruleNumber[1], ruleNumber[2], ruleNumber[3]), new NullPredicate());

        machine.execute(steps + 1);

        Quiver<ConnectedQuiver> quiver = machine.getQuiverHistory().get(steps);
        // for (int index = 0; index <= steps; index++) {
        //     System.out.println(machine.getQuiverHistory().get(index));
        // }
        if (quiver != null) {
            Set<GeneralFunction> outFunctions = mapper.mapFunctions(quiver);
            System.out.println("Output function count: " + outFunctions.size());
            System.out.println("Test input: " + "[empty string]");
            for (GeneralFunction fn : outFunctions) {
                System.out.println("Output: " + fn.apply(""));
            }
        }
    }
}
