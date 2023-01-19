package cuprum.cmrule.tester;

public class AllConditionRecord implements ECARecord, Comparable<AllConditionRecord> {
    private String quiverName;
    private Integer rulePattern;
    private Integer step;

    public AllConditionRecord(String quiverName, Integer rulePattern, Integer step) {
        this.quiverName = quiverName;
        this.rulePattern = rulePattern;
        this.step = step;
    }

    @Override
    public String getStringRepersentation() {
        StringBuilder sb = new StringBuilder();
        int d1 = (this.rulePattern >> 16) & 0x03;
        int d2 = (this.rulePattern >> 12) & 0x0F;
        int d3 = (this.rulePattern >> 8) & 0x0F;
        int d4 = this.rulePattern & 0xFF;

        sb.append(this.quiverName)
                .append(',')
                .append(d1).append('-').append(d2).append('-').append(d3).append('-').append(d4)
                .append(',')
                .append(this.step);

        return sb.toString();
    }

    @Override
    public int compareTo(AllConditionRecord o) {
        int compareResult = this.quiverName.compareTo(o.getQuiverName());
        if (compareResult == 0) {
            return this.rulePattern.compareTo(o.getRulePattern());
        }
        return compareResult;
    }

    public String getQuiverName() {
        return quiverName;
    }

    public Integer getRulePattern() {
        return rulePattern;
    }

    public Integer getStep() {
        return step;
    }
}
