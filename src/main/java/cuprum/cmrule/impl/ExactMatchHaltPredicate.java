package cuprum.cmrule.impl;

import java.util.LinkedHashMap;

import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;

public class ExactMatchHaltPredicate<Q extends BaseConnectedQuiver<Q>> implements HaltPredicate {
    protected Quiver<Q> matchQuiver;

    public ExactMatchHaltPredicate(Quiver<Q> quiver) {
        this.matchQuiver = quiver;
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> boolean testHalt(int step,
            LinkedHashMap<Integer, Quiver<CQ>> quiverHistory) {
        Quiver<CQ> currentQuiver = quiverHistory.get(Integer.valueOf(step));
        return this.matchQuiver.equals(currentQuiver);
    }
}
