package cuprum.cmrule.example;

import java.util.ArrayList;
import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.predicates.NullPredicate;
import cuprum.cmrule.application.GeneralFunction;
import cuprum.cmrule.application.OneDimFunctionMapper;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.rules.ECARule;

public class ExampleGeneration {
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
        String funcString = "asdfuhellolihfdu";
        String initialQuiverPattern = "";
        String matchQuiverPattern = "0000011111000000";
        int[] ruleNumber = new int[] { 0, 0, 0, 0 };
        int steps = 1;

        ArrayList<GeneralFunction[]> funcList = new ArrayList<>();
        GeneralFunction[] funcs = generateFunctionList(funcString);
        funcList.add(funcs);

        if (funcs.length != initialQuiverPattern.length() || funcs.length != matchQuiverPattern.length()) {
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

        machine.execute(steps);

        Quiver<ConnectedQuiver> quiver = machine.getQuiverHistory().get(steps);
        if (quiver != null) {
            GeneralFunction[] outFunctions = mapper.mapFunctions(quiver);
            System.out.println("Output function count: " + outFunctions.length);
            System.out.println("Test input: " + "[empty string]");
            for (GeneralFunction fn : outFunctions) {
                System.out.println("Output: " + fn.apply(""));
            }
        }
    }
}
