package cuprum.cmrule.tester.record;

import cuprum.cmrule.RuleUtil;

public class AllMatchRecord implements ECARecord, Comparable<AllMatchRecord> {
    private String targetQuiverName;
    private String startQuiverName;
    private Integer rulePattern;
    private Integer step;

    public AllMatchRecord(String targetQuiverName, String startQuiverName, Integer rulePattern, Integer step) {
        this.targetQuiverName = targetQuiverName;
        this.startQuiverName = startQuiverName;
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

        sb.append(this.targetQuiverName)
                .append(',')
                .append(this.startQuiverName)
                .append(',')
                .append(d1).append('-').append(d2).append('-').append(d3).append('-').append(d4)
                .append(',')
                .append(this.step);
        return sb.toString();
    }

    @Override
    public int compareTo(AllMatchRecord o) {
        int targetResult = this.targetQuiverName.compareTo(o.getTargetQuiverName());
        if (targetResult == 0) {
            int startResult = this.startQuiverName.compareTo(o.getStartQuiverName());
            if (startResult == 0) {
                return this.rulePattern.compareTo(o.getRulePattern());
            }
            return startResult;
        }
        return targetResult;
    }

    public String getStartQuiverName() {
        return startQuiverName;
    }

    public String getTargetQuiverName() {
        return targetQuiverName;
    }

    public Integer getRulePattern() {
        return rulePattern;
    }

    public Integer getStep() {
        return step;
    }

    public static AllMatchRecord parse(String str) {
        String[] fieldSegment = str.split(",", 4);

        if (fieldSegment.length < 4)
            throw new IllegalArgumentException();

        String[] ruleSegment = fieldSegment[2].split("-", 4);

        if (ruleSegment.length < 4)
            throw new IllegalArgumentException();

        return new AllMatchRecord(
                fieldSegment[0],
                fieldSegment[1],
                RuleUtil.combineECARulePattern(
                        Integer.parseInt(ruleSegment[0]),
                        Integer.parseInt(ruleSegment[1]),
                        Integer.parseInt(ruleSegment[2]),
                        Integer.parseInt(ruleSegment[3])),
                Integer.parseInt(fieldSegment[3]));
    }
}
