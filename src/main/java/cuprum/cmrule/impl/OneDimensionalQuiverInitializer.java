package cuprum.cmrule.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.QuiverInitializer;
import cuprum.cmrule.RuleUtil;

public class OneDimensionalQuiverInitializer implements QuiverInitializer<ConnectedQuiver> {

    protected volatile String initString;
    protected int[] initState;
    protected int[] terminateState;
    protected ConnectedQuiver cachedCQ;

    protected volatile boolean available = true;

    public OneDimensionalQuiverInitializer() {
        this("000000000");
    }

    public OneDimensionalQuiverInitializer(String bitString) {
        this.initState = OneDimensionalQuiverInitializer.generateStateArray(bitString);
        this.terminateState = new int[this.initState.length];
        Arrays.fill(this.terminateState, 1);
        this.initString = bitString;
        this.pregenerateQuiver();
    }

    public OneDimensionalQuiverInitializer(int[] initArray) {
        this.initState = Arrays.copyOf(initArray, initArray.length);
        this.terminateState = new int[this.initState.length];
        Arrays.fill(this.terminateState, 1);
        this.initString = String.valueOf(RuleUtil.intArrayToString(initArray));
        this.pregenerateQuiver();
    }

    public static Quiver<ConnectedQuiver> genQuiver(String pattern) {
        OneDimensionalQuiverInitializer qInit = new OneDimensionalQuiverInitializer(pattern);
        return qInit.generateQuiver();
    }

    protected static int[] generateStateArray(String bitString) {
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
        return stateArray;
    }

    public void setTerminateState(String termState) {
        this.terminateState = OneDimensionalQuiverInitializer.generateStateArray(termState);
    }

    public void setTerminateState(int[] termState) {
        this.terminateState = Arrays.copyOf(termState, termState.length);
    }

    protected void pregenerateQuiver() {
        ConnectedQuiver cq1 = new ConnectedQuiver();
        for (int state : this.initState) {
            cq1.addArrow(state);
        }

        this.cachedCQ = cq1;
    }

    @Override
    public String getName() {
        synchronized (this) {
            return this.initString;
        }
    }

    @Override
    public Quiver<ConnectedQuiver> generateQuiver() {
        Quiver<ConnectedQuiver> q = new Quiver<>();
        q.add(this.cachedCQ.typedClone());
        return q;
    }

    @Override
    public boolean iterate() {
        if (!this.available)
            return false;

        if (Arrays.equals(this.initState, this.terminateState)) {
            this.available = false;
            return false;
        }

        // boolean iterationSuccess = false;
        for (int i = this.initState.length - 1; i >= 0; i--) {
            if (this.initState[i] == 0) {
                this.initState[i] = 1;
                for (int j = i + 1; j < this.initState.length; j++) {
                    this.initState[j] = 0;
                }
                // iterationSuccess = true;
                // break;
                synchronized (this) {
                    this.initString = RuleUtil.intArrayToString(this.initState);
                }
                    this.pregenerateQuiver();
                return true;
            }
            // == 1 continue;
        }
        this.available = false;
        return false;

        // if (!Arrays.equals(this.initState, this.terminateState)) {
        // Arrays.fill(this.initState, 0);
        // iterationSuccess = true;
        // }
        // if (iterationSuccess) {
        // synchronized (this) {
        // this.initString = RuleUtil.intArrayToString(this.initState);
        // }
        // this.pregenerateQuiver();
        // if (!Arrays.equals(this.initState, this.terminateState)) {
        // return true;
        // } else {
        // this.next = false;
        // return false;
        // }
        // }
        // this.next = iterationSuccess;
        // return iterationSuccess;
    }
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

    private static long bitArray2LongNumber(int[] bitArray) {
        long num = 0;
        for (int bit : bitArray) {
            num = num << 1;
            num = num | (bit & 0x01);
        }
        return num;
    }

    /*
     * Negative? I don't care.
     */
    private static int[] long2BitArray(long num, int length) {
        int[] bitArr = new int[length];
        long move = num;
        for (int i = length - 1; i >= 0; i--) {
            bitArr[i] = (int) (move & 0x01);
            move = move >> 1;
        }
        return bitArr;
    }

    public List<OneDimensionalQuiverInitializer> split(int splitCount) {
        List<OneDimensionalQuiverInitializer> qInitList = Collections.synchronizedList(new ArrayList<>());

        long initNum = bitArray2LongNumber(this.initState);
        long endNum = bitArray2LongNumber(this.terminateState);

        long interval = (long) Math.ceil((endNum - initNum) / (double) splitCount);

        Long previousNum = initNum;
        Long currentNum = initNum + interval;
        while (currentNum < endNum) {
            OneDimensionalQuiverInitializer qinit = new OneDimensionalQuiverInitializer(
                    long2BitArray(previousNum, this.initState.length));
            qinit.setTerminateState(long2BitArray(currentNum, this.initState.length));
            qInitList.add(qinit);
            previousNum = currentNum;
            currentNum += interval;
        }
        OneDimensionalQuiverInitializer lastQinit = new OneDimensionalQuiverInitializer(
                long2BitArray(previousNum, this.initState.length));
        lastQinit.setTerminateState(long2BitArray(endNum, this.initState.length));
        qInitList.add(lastQinit);

        return qInitList;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }
}
