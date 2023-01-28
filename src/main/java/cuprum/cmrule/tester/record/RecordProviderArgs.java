package cuprum.cmrule.tester.record;

import compositionmachine.machine.CompositionMachine;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.interfaces.QuiverInitializer;

public class RecordProviderArgs {
    private int rulePattern;
    private QuiverInitializer<ConnectedQuiver> qInit;
    private CompositionMachine<ConnectedQuiver> machine;
    private Object[] quitState;

    public RecordProviderArgs(int rulePattern, QuiverInitializer<ConnectedQuiver> qInit,
            CompositionMachine<ConnectedQuiver> machine, Object[] quitState) {
        this.rulePattern = rulePattern;
        this.qInit = qInit;
        this.machine = machine;
        this.quitState = quitState;
    }

    public int getRulePattern() {
        return rulePattern;
    }

    public QuiverInitializer<ConnectedQuiver> getQInit() {
        return qInit;
    }

    public CompositionMachine<ConnectedQuiver> getMachine() {
        return machine;
    }

    public Object[] getQuitState() {
        return quitState;
    }
}
