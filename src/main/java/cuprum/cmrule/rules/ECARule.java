package cuprum.cmrule.rules;

import compositionmachine.machine.interfaces.RuleSet;

public class ECARule extends RuleSet {
    protected int delta1RulePattern; // 2bit int
    protected int delta2RulePattern; // 4bit int
    protected int delta3RulePattern; // 4bit int
    protected int delta4RulePattern; // 8bit int

    public ECARule(int delta1Rule, int delta2Rule, int delta3Rule, int delta4Rule){
        this.delta1RulePattern = delta1Rule & 0x03;
        this.delta2RulePattern = delta2Rule & 0x0F;
        this.delta3RulePattern = delta3Rule & 0x0F;
        this.delta4RulePattern = delta4Rule & 0xFF;
    }

    @Override
    public int delta1(int organism) {
        int o = organism > 0 ? 1 : 0;
        return (this.delta1RulePattern >> o) & 0x01;
    }

    @Override
    public int delta2(int organism, int neighbourRight) {
        int o = organism > 0 ? 1 : 0;
        int right = neighbourRight > 0 ? 1 : 0;

        int state = (o << 1) + right;

        return (this.delta2RulePattern >> state) & 0x01;
    }

    @Override
    public int delta3(int neighbourLeft, int organism) {
        int left = neighbourLeft > 0 ? 1 : 0;
        int o = organism > 0 ? 1 : 0;

        int state = (left << 1) + o;

        return ((this.delta3RulePattern >> state) & 0x01);
    }

    @Override
    public int delta4(int neighbourLeft, int organism, int neighbourRight) {
        int left = neighbourLeft > 0 ? 1 : 0;
        int o = organism > 0 ? 1 : 0;
        int right = neighbourRight > 0 ? 1 : 0;

        int state = (left << 2) + (o << 1) + right;

        return (this.delta4RulePattern >> state) & 0x01;
    }

}
