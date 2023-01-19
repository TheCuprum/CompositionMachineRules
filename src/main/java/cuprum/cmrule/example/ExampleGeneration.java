package cuprum.cmrule.example;

import java.util.ArrayList;
import java.util.Scanner;

import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.predicates.NullPredicate;
import cuprum.cmrule.application.GeneralFunction;
import cuprum.cmrule.application.OneDimFunctionMapper;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.rules.ECARule;

public abstract class ExampleGeneration {
    private Object[] handleArgs(String[] args) {
        String initialQuiverPattern;
        int[] ruleNumber = new int[4];
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

        return new Object[] { initialQuiverPattern, ruleNumber, steps };
    }

    private Quiver<ConnectedQuiver> runMachine(String initialQuiverPattern, int[] ruleNumber, int steps) {
        Quiver<ConnectedQuiver> initialQuiver = OneDimensionalQuiverInitializer.genQuiver(initialQuiverPattern);

        CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(initialQuiver,
                new ECARule(ruleNumber[0], ruleNumber[1], ruleNumber[2], ruleNumber[3]), new NullPredicate());

        machine.execute(steps);

        return machine.getQuiverHistory().get(steps);
    }

    public void runGeneration(String[] args) {
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
        }
    }

    protected abstract GeneralFunction[] provideFunctions();

    protected abstract Object provideInputObject(Class<?> cls);
}
