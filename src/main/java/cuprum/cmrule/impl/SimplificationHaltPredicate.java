package cuprum.cmrule.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;

import compositionmachine.machine.Arrow;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;

public class SimplificationHaltPredicate implements HaltPredicate {

    public static final int START_TEST_STEP = 10;

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> boolean testHalt(int step,
            LinkedHashMap<Integer, Quiver<CQ>> quiverHistory) {
        if (step <= START_TEST_STEP) {
            return false;
        } else {
            Quiver<CQ> currentQuiver = quiverHistory.get(step);

            for (int i = 0; i < currentQuiver.size(); i++) {
                BaseConnectedQuiver<CQ> cq = currentQuiver.get(i);
                int edgeCount = 0;

                Iterator<Arrow> arrowIterator = cq.getArrowIterator();
                while (arrowIterator.hasNext()) {
                    int state = cq.getArrowState(arrowIterator.next());
                    if (state > 0)
                        edgeCount += state;
                }
                if (edgeCount == 1) { // if there is only one edge in this part of connected quiver
                    for (int j = step - 1; j > START_TEST_STEP; j--) {
                        if (quiverHistory.get(j).get(i).equals(cq))
                            return true;
                    }
                }
            }
            return false;
        }
    }
}
