package cuprum.cmrule.rules;

import compositionmachine.bootstrap.Bootstrap;
import compositionmachine.bootstrap.Config;
import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.interfaces.RuleSet;

public class NotXorRule126 extends RuleSet {
    // 01
    // 0110
    // 0110
    // 01111110
    // 1-9-9-126

    @Override
    public int delta1(int organism) {
        return (organism == 0) ? 1 : 0;
    }

    @Override
    public int delta2(int organism, int neighbourRight) {
        return ((organism == 1 && neighbourRight == 0) ||
                (organism == 0 && neighbourRight == 1)) ? 1 : 0;
    }

    @Override
    public int delta3(int neighbourLeft, int organism) {
        return ((organism == 1 && neighbourLeft == 0) ||
                (organism == 0 && neighbourLeft == 1)) ? 1 : 0;
    }

    @Override
    public int delta4(int neighbourLeft, int organism, int neighbourRight) {
        if (neighbourLeft == 1 && organism == 1 && neighbourRight == 1)
            return 0;
        else if (neighbourLeft == 0 && organism == 0 && neighbourRight == 0)
            return 0;
        else
            return 1;
    }

    public static void main(String[] args) {

        String[] callbackNames = new String[2];
        // callbackNames[0] = "machine.callbacks.PrintBlockCallback";
        callbackNames[0] = "compositionmachine.machine.callbacks.SaveDotCallback";
        callbackNames[1] = "compositionmachine.machine.callbacks.PrintBlockCallback";
        Config config = new Config();
        config.customClassPath = "target/classes/";
        config.initializerName = "compositionmachine.examples.ExampleQuiverInitializer";
        config.ruleName = "cuprum.cmrule.NotXorRule126";
        config.iterationSteps = 24;
        config.callbackNames = callbackNames;
        config.dotOutputPath = "data/";
        config.machineName = "not-xor-rule126";
        Bootstrap b = Bootstrap.createBootstrap(config.customClassPath, config);
        CompositionMachine<?> machine = b.boot();
    }
}
