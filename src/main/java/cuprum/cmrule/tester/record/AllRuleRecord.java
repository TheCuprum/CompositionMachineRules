package cuprum.cmrule.tester.record;

import cuprum.cmrule.RuleUtil;

public class AllRuleRecord implements ECARecord, Comparable<AllRuleRecord> {
    private Integer rulePattern;
    private Integer step;

    public AllRuleRecord(Integer rulePattern, Integer step) {
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

        sb.append(d1).append("-").append(d2).append("-").append(d3).append("-").append(d4)
            .append(',').append(this.step);

        return sb.toString();
    }

    @Override
    public int compareTo(AllRuleRecord o) {
        return this.rulePattern.compareTo(o.getRulePattern());
    }

    public Integer getRulePattern() {
        return rulePattern;
    }

    public Integer getStep() {
        return step;
    }

    public static AllRuleRecord parse(String str) {
        String[] fieldSegment = str.split(",", 2);

        if (fieldSegment.length < 2)
            throw new IllegalArgumentException();

        String[] ruleSegment = fieldSegment[0].split("-", 4);

        if (ruleSegment.length < 4)
            throw new IllegalArgumentException();

        return new AllRuleRecord(
                RuleUtil.combineECARulePattern(
                        Integer.parseInt(ruleSegment[0]),
                        Integer.parseInt(ruleSegment[1]),
                        Integer.parseInt(ruleSegment[2]),
                        Integer.parseInt(ruleSegment[3])),
                Integer.parseInt(fieldSegment[1]));
    }
}
