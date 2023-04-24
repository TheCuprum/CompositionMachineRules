package cuprum.cmrule.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import cuprum.cmrule.ProgramArgument;
import cuprum.cmrule.ProgramArgumentProcessor;
import cuprum.cmrule.application.GeneralFunction;
import cuprum.cmrule.application.OneDimFunctionMapper;
import cuprum.cmrule.impl.ExactMatchHaltPredicate;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.rules.ECARule;

public abstract class ExampleGeneration {
    private Object[] handleArgs(String[] args) {
        ProgramArgumentProcessor argProcessor = new ProgramArgumentProcessor();
        ProgramArgument parsedArgs = argProcessor
                .addInitialPattern().addRule()
                .addExtraField("step", "Enter maximum evolution steps:")
                .addExtraField("inputType", "Enter desired input type (in fully qulified name):")
                .addExtraField("outputType", "Enter desired output type (in fully qulified name)")
                .handleArgument(args);

        String initialQuiverPattern = parsedArgs.getInitialPattern();
        int[] ruleNumber = parsedArgs.getRulePattern();
        int steps = Integer.parseInt(parsedArgs.getExtraField("step"));
        Class<?> inputType;
        Class<?> outputType;
        try {
            inputType = ClassLoader.getSystemClassLoader().loadClass(parsedArgs.getExtraField("inputType"));
            outputType = ClassLoader.getSystemClassLoader().loadClass(parsedArgs.getExtraField("outputType"));
            return new Object[] { initialQuiverPattern, ruleNumber, steps, inputType, outputType };
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    private Quiver<ConnectedQuiver> runMachine(String initialQuiverPattern, String endQuiverPattern, int[] ruleNumber,
            int steps) {
        Quiver<ConnectedQuiver> initialQuiver = OneDimensionalQuiverInitializer.genQuiver(initialQuiverPattern);
        Quiver<ConnectedQuiver> endQuiver = OneDimensionalQuiverInitializer.genQuiver(endQuiverPattern);

        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(initialQuiver,
                new ECARule(ruleNumber[0], ruleNumber[1], ruleNumber[2], ruleNumber[3]),
                new ExactMatchHaltPredicate<ConnectedQuiver>(endQuiver));

        Object[] haltResult = machine.execute(steps);
        if (haltResult.length > 0){
            System.out.println("Result found at step " + haltResult[0]);
            return machine.getQuiverHistory().get(haltResult[0]);
        }
        else {
            System.out.println("Desired output never reached");
            return null;
        }
    }

    public Set<GeneralFunction> runGeneration(String[] args) {
        Object[] parsedArgs = this.handleArgs(args);
        String initialQuiverPattern = (String) parsedArgs[0];
        int[] ruleNumber = (int[]) parsedArgs[1];
        int steps = (int) parsedArgs[2];
        Class<?> inputType = (Class<?>) parsedArgs[3];
        Class<?> outputType = (Class<?>) parsedArgs[4];

        GeneralFunction[] funcs = this.provideFunctions();
        if (funcs.length != initialQuiverPattern.length()) {
            System.err.println("Pattern size mismatch.");
            System.exit(0);
        }

        ArrayList<GeneralFunction[]> funcList = new ArrayList<>();
        funcList.add(funcs);
        OneDimFunctionMapper mapper = new OneDimFunctionMapper(funcList);

        char[] endQuiverPatternArray = new char[funcs.length];
        boolean functionNeededFlag = false;
        for (int index = 0; index < funcs.length; index++) {
            if (funcs[index].getInClass().equals(inputType))
                functionNeededFlag = true;
            if (functionNeededFlag)
                endQuiverPatternArray[index] = '1';
            else
                endQuiverPatternArray[index] = '0';
            if (functionNeededFlag && funcs[index].getOutClass().equals(outputType))
                functionNeededFlag = false;
        }

        Quiver<ConnectedQuiver> quiver = this.runMachine(initialQuiverPattern, String.valueOf(endQuiverPatternArray), ruleNumber, steps);

        if (quiver != null) {
            Set<GeneralFunction> outFunctions = mapper.mapFunctions(quiver);
            // System.out.println("Quiver shape: " + quiver.toString());
            System.out.println("Output function count: " + outFunctions.size());
            for (GeneralFunction fn : outFunctions) {
                if (!fn.getInClass().equals(inputType) || !fn.getOutClass().equals(outputType))
                    continue;

                Object input = this.provideInputObject(fn.getInClass());
                // if (input == null)
                //     continue;
                Object output = fn.apply(input);
                System.out.println("Test input: " + input.toString());
                System.out.println("Output: " + output.toString());
            }

            return outFunctions;
        }

        return null;
    }

    protected abstract GeneralFunction[] provideFunctions();

    /* should not provide null unless the requested type is not supported */
    protected abstract Object provideInputObject(Class<?> cls);
}
