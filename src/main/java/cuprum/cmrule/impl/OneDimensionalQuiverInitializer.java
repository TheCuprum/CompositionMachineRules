package cuprum.cmrule.impl;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.QuiverInitializer;
import cuprum.cmrule.RuleUtil;

public class OneDimensionalQuiverInitializer implements QuiverInitializer<ConnectedQuiver> {

    protected String initString;
    protected int[] initState;
    protected ConnectedQuiver cachedCQ;

    public OneDimensionalQuiverInitializer() {
        this("000000000");
    }

    public OneDimensionalQuiverInitializer(String bitString) {
        int[] stateArray = new int[bitString.length()];
        for (int index = 0; index < bitString.length(); index++) {
            char c = bitString.charAt(index);
            // if (c >= '0' && c <= '9') {
            if (c >= '0' && c <= '1') {
                stateArray[index] = c - '0';
            } else {
                throw new IllegalArgumentException("Non-01 character is not allowed.");
            }
        }

        this.initState = stateArray;
        this.initString = bitString;
        this.pregenerateQuiver();
    }

    public OneDimensionalQuiverInitializer(int[] initArray) {
        this.initState = initArray.clone();
        this.initString = String.valueOf(RuleUtil.intArrayToString(initArray));
        this.pregenerateQuiver();
    }

    private void pregenerateQuiver() {
        ConnectedQuiver cq1 = new ConnectedQuiver();
        for (int state : this.initState) {
            cq1.addArrow(state);
        }

        this.cachedCQ = cq1;
    }

    @Override
    public String getName() {
        return this.initString;
    }

    @Override
    public Quiver<ConnectedQuiver> generateQuiver() {
        Quiver<ConnectedQuiver> q = new Quiver<>();
        q.add(this.cachedCQ.typedClone());
        return q;
    }

    @Override
    public boolean iterate() {
        for(int i = this.initState.length - 1; i >= 0; i--){
            if (this.initState[i] == 0){
                this.initState[i] = 1;
                for (int j = i + 1; j < this.initState.length; j++){
                    this.initState[j] = 0;
                }
                this.initString = RuleUtil.intArrayToString(this.initState);
                this.pregenerateQuiver();
                return true;
            }
            // == 1 continue;
        }
        return false;
    };
    // @Override
    // public Quiver<ConnectedQuiver> generateQuiver() {
    // ConnectedQuiver cq1 = new ConnectedQuiver();
    // for (int i = 0; i < 7; i++)
    // cq1.addArrow(0);
    // cq1.addArrow(1);
    // for (int i = 0; i < 7; i++)
    // cq1.addArrow(0);

    // Quiver<ConnectedQuiver> q = new Quiver<>();
    // q.add(cq1);

    // return q;
    // };
}
