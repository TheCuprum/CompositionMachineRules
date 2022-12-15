package cuprum.cmrule.impl;

import java.util.LinkedHashMap;

import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.HaltPredicate;
import cuprum.cmrule.tester.TesterUtil;

public class SimplificationHaltPredicate implements HaltPredicate {

    public static final int START_TEST_STEP = 10;
    public static final int TEST_LOOPS = 3;

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> boolean testHalt(int step,
            LinkedHashMap<Integer, Quiver<CQ>> quiverHistory) {
        if (step <= START_TEST_STEP) {
            return false;
        } else {
            Quiver<CQ> currentQuiver = quiverHistory.get(step);

            for (int i = 0; i < currentQuiver.size(); i++) {
                BaseConnectedQuiver<CQ> cq = currentQuiver.get(i);
                int edgeCount = TesterUtil.countArrows(cq);

                if (edgeCount == 1) { // if there is only one edge in this part of connected quiver
                    boolean loopFlag = false;
                    int loopStep = -1;
                    int maxArrowNumber = 0;

                    for (int j = step - 1; j > START_TEST_STEP; j--) {
                        BaseConnectedQuiver<CQ> scannedQuiver = quiverHistory.get(j).get(i);
                        if (loopStep < 0 && scannedQuiver.equals(cq)) {
                            // return true;
                            loopStep = j;
                            loopFlag = true;
                        }
                        int arrowCount = TesterUtil.countArrows(scannedQuiver);
                        if (arrowCount > maxArrowNumber)
                            maxArrowNumber = arrowCount;

                        if (loopFlag && maxArrowNumber >= scannedQuiver.getArrowStates().size() / 2)
                            return true;

                        if (step - j >= (step - loopStep) * TEST_LOOPS)
                            break;
                    }
                }
            }
            return false;
        }
    }
}
