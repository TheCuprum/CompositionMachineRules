package cuprum.cmrule.impl;

import java.util.ArrayList;
import java.util.Map;

import compositionmachine.bootstrap.Config;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.machine.interfaces.MachineCallback;
import cuprum.cmrule.Setting;

// not compatible with config.
public class HaltRecordCallback implements MachineCallback {
    // DotWriter writer;
    private ArrayList<Integer> record = new ArrayList<>();
    private int rulePattern = -1;

    public HaltRecordCallback() {
        // this.writer = new DotWriter(dotOutputPath, machineName, true);

    }

    @Deprecated
    public void setRule(int pattern) {
        this.rulePattern = pattern;
    }

    @Deprecated
    public ArrayList<Integer> getRecord() {
        return this.record;
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
        if (this.rulePattern % Setting.PRINT_STEP == 0)
            System.out.print("HALTS AT TIME " + step + "!");
        if (this.rulePattern > -1 && step > 4) {
            this.record.add(this.rulePattern);
        }
        this.rulePattern = -1;
        return null;
        // int suffixLength = 0;
        // int totalSteps = step;
        // while (totalSteps / 10 > 0) {
        // suffixLength++;
        // totalSteps /= 10;
        // }

        // for (int i = 0 ; i < step; i++)
        // this.writer.writeDotFile(quiverHistory.get(i), Util.leftPadString(i + ".dot",
        // suffixLength, '0'));
    }

}