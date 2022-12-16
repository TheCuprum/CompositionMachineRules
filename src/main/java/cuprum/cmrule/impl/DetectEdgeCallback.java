package cuprum.cmrule.impl;

import java.util.Map;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.MachineCallback;
import cuprum.cmrule.tester.TesterUtil;

public class DetectEdgeCallback implements MachineCallback {

    private int minNum = 1;
    private int maxNum = 1;

    public DetectEdgeCallback() {
    }

    public DetectEdgeCallback(int accuateNum) {
        this.minNum = accuateNum;
        this.maxNum = accuateNum;
    }

    public DetectEdgeCallback(int minNum, int maxNum) {
        this.minNum = minNum;
        this.maxNum = maxNum;
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
        for (int index = 0; index < newQuiver.size(); index++) {
            CQ cq = newQuiver.get(index);
            int arrowCount = TesterUtil.countArrows(cq);
            if (this.minNum <= arrowCount && arrowCount <= this.maxNum)
                if (this.minNum == this.maxNum)
                    System.out.println("step=" + step + ",cq=" + index + "  matches arrow count (" + this.minNum + ")");
                else
                    System.out.println("step=" + step + ",cq=" + index + "  matches arrow count (" + this.minNum + ","
                            + this.maxNum + ")");
        }
    }

    @Override
    public <CQ extends BaseConnectedQuiver<CQ>> Object onHalt(int step, Map<Integer, Quiver<CQ>> quiverHistory) {
        return null;
    }

}
