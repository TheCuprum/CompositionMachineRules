package cuprum.cmrule.impl;

import java.util.LinkedHashMap;

import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;
import compositionmachine.machine.predicates.LoopPredicate;

public class MatchOrLoopHaltPredicate<Q extends BaseConnectedQuiver<Q>> implements HaltPredicate {
    protected LoopPredicate loopPredicate;
    protected ExactMatchHaltPredicate<Q> matchPredicate;

    public MatchOrLoopHaltPredicate(Quiver<Q> quiver) {
        this(quiver, 0);
    }

    public MatchOrLoopHaltPredicate(Quiver<Q> quiver, int startCheckStep) {
        this.loopPredicate = new LoopPredicate(startCheckStep);
        this.matchPredicate = new ExactMatchHaltPredicate<>(quiver);
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> boolean testHalt(int step,
            LinkedHashMap<Integer, Quiver<CQ>> quiverHistory) {
        return this.matchPredicate.testHalt(step, quiverHistory) || this.loopPredicate.testHalt(step, quiverHistory);
    }
}
