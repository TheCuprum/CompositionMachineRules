package cuprum.cmrule.impl;

import java.util.Map;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.MachineCallback;

public class MatchQuiverCallback<Q extends BaseConnectedQuiver<Q>> implements MachineCallback{
    protected Quiver<Q> matchQuiver;

    public MatchQuiverCallback(Quiver<Q> matchQuiver){
        this.matchQuiver = matchQuiver;
    }

    @Override
    public void initialize(Config config) {
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> void onExecuteStart(int totalSteps, Quiver<CQ> initialQuiver) {
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> void onStepBegin(int step, Map<Integer, Quiver<CQ>> quiverHistory) {
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> void onStepEnd(int step, Quiver<CQ> newQuiver,
            Map<Integer, Quiver<CQ>> quiverHistory) {
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> Object onHalt(int step, Map<Integer, Quiver<CQ>> quiverHistory) {
        Quiver<CQ> currentQuiver = quiverHistory.get(step);
        if (this.matchQuiver.equals(currentQuiver))
            return currentQuiver;
        else
            return null;
    }
    
}
