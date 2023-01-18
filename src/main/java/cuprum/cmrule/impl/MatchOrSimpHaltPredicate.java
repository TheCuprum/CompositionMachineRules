package cuprum.cmrule.impl;

import java.util.LinkedHashMap;

import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;

public class MatchOrSimpHaltPredicate<Q extends BaseConnectedQuiver<Q>> implements HaltPredicate {
    protected SimplificationHaltPredicate simpPrediacte;
    protected ExactMatchHaltPredicate<Q> matchPredicate;

    public MatchOrSimpHaltPredicate(Quiver<Q> quiver) {
        this.simpPrediacte = new SimplificationHaltPredicate();
        this.matchPredicate = new ExactMatchHaltPredicate<>(quiver);
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> boolean testHalt(int step,
            LinkedHashMap<Integer, Quiver<CQ>> quiverHistory) {
        return this.simpPrediacte.testHalt(step, quiverHistory) || this.matchPredicate.testHalt(step, quiverHistory);
    }
}
