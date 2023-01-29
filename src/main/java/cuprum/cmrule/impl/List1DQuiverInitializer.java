package cuprum.cmrule.impl;

public class List1DQuiverInitializer extends OneDimensionalQuiverInitializer {
    private String[] iterateArr;
    private volatile int interateIndex;

    public List1DQuiverInitializer(String[] patternArray) {
        this.iterateArr = patternArray;
        this.interateIndex = 0;
        this.preGenerate();
    }

    private void preGenerate() {
        super.initState = super.generateStateArray(
                this.iterateArr[Math.min(this.interateIndex, this.iterateArr.length - 1)]);
        super.pregenerateQuiver();
    }

    @Override
    public String getName() {
        return this.iterateArr[Math.min(this.interateIndex, this.iterateArr.length - 1)];
    }

    @Override
    public boolean isAvailable() {
        return this.interateIndex < this.iterateArr.length;
    }

    @Override
    public boolean iterate() {
        this.interateIndex++;
        if (this.interateIndex < this.iterateArr.length) {
            this.preGenerate();
            return true;
        }
        return false;
    }
}
