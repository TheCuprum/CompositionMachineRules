package cuprum.cmrule.tester.record;

import cuprum.cmrule.RuleUtil;

public class AllConditionRecord implements ECARecord<AllConditionRecord> {
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

    public static AllConditionRecord parse(String str) {
        String[] fieldSegment = str.split(",", 3);

        if (fieldSegment.length < 3)
            throw new IllegalArgumentException();

        String[] ruleSegment = fieldSegment[1].split("-", 4);

        if (ruleSegment.length < 4)
            throw new IllegalArgumentException();

        return new AllConditionRecord(
                fieldSegment[0],
                RuleUtil.combineECARulePattern(
                        Integer.parseInt(ruleSegment[0]),
                        Integer.parseInt(ruleSegment[1]),
                        Integer.parseInt(ruleSegment[2]),
                        Integer.parseInt(ruleSegment[3])),
                Integer.parseInt(fieldSegment[2]));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((quiverName == null) ? 0 : quiverName.hashCode());
        result = prime * result + ((rulePattern == null) ? 0 : rulePattern.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AllConditionRecord other = (AllConditionRecord) obj;
        if (quiverName == null) {
            if (other.quiverName != null)
                return false;
        } else if (!quiverName.equals(other.quiverName))
            return false;
        if (rulePattern == null) {
            if (other.rulePattern != null)
                return false;
        } else if (!rulePattern.equals(other.rulePattern))
            return false;
        // if (step == null) {
        //     if (other.step != null)
        //         return false;
        // } else if (!step.equals(other.step))
        //     return false;
        return true;
    }
}
