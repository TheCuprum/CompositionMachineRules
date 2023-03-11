package cuprum.cmrule.example;

import java.util.ArrayList;
import java.util.Scanner;

import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.predicates.NullPredicate;
import cuprum.cmrule.ProgramArgument;
import cuprum.cmrule.ProgramArgumentProcessor;
import cuprum.cmrule.application.GeneralFunction;
import cuprum.cmrule.application.OneDimFunctionMapper;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.rules.ECARule;

public abstract class ExampleGeneration {
    private Object[] handleArgs(String[] args) {
        ProgramArgumentProcessor argProcessor = new ProgramArgumentProcessor();
        ProgramArgument parsedArgs = argProcessor     
            .addInitialPattern().addRule()
            .addExtraIntegerFields("step", "Enter iteration steps:")
            .handleArgument(args);

        String initialQuiverPattern = parsedArgs.getInitialPattern();
        int[] ruleNumber = parsedArgs.getRulePattern();
        int steps = parsedArgs.getExtraIntegerFields("step");

        return new Object[] { initialQuiverPattern, ruleNumber, steps };
    }

    private Quiver<ConnectedQuiver> runMachine(String initialQuiverPattern, int[] ruleNumber, int steps) {
        Quiver<ConnectedQuiver> initialQuiver = OneDimensionalQuiverInitializer.genQuiver(initialQuiverPattern);

        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(initialQuiver,
                new ECARule(ruleNumber[0], ruleNumber[1], ruleNumber[2], ruleNumber[3]), new NullPredicate());

        machine.execute(steps);

        return machine.getQuiverHistory().get(steps);
    }

    public GeneralFunction[] runGeneration(String[] args) {
        Object[] parsedArgs = this.handleArgs(args);
        String initialQuiverPattern = (String) parsedArgs[0];
        int[] ruleNumber = (int[]) parsedArgs[1];
        int steps = (int) parsedArgs[2];

        GeneralFunction[] funcs = this.provideFunctions();
        if (funcs.length != initialQuiverPattern.length()) {
            System.err.println("Pattern size mismatch.");
            System.exit(0);
        }

        ArrayList<GeneralFunction[]> funcList = new ArrayList<>();
        funcList.add(funcs);
        OneDimFunctionMapper mapper = new OneDimFunctionMapper(funcList);

        Quiver<ConnectedQuiver> quiver = this.runMachine(initialQuiverPattern, ruleNumber, steps);

        if (quiver != null) {
            GeneralFunction[] outFunctions = mapper.mapFunctions(quiver);
            // System.out.println("Quiver shape: " + quiver.toString());
            System.out.println("Output function count: " + outFunctions.length);
            for (GeneralFunction fn : outFunctions) {
                Object input = this.provideInputObject(fn.getInClass());
                Object output = fn.apply(input);
                System.out.println("Test input: " + input.toString());
                System.out.println("Output: " + output.toString());
            }

            return outFunctions;
        }

        return null;
    }

    protected abstract GeneralFunction[] provideFunctions();

    protected abstract Object provideInputObject(Class<?> cls);
}
