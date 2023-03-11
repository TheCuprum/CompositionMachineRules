package cuprum.cmrule;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProgramArgument {
    private String targetPattern;
    private String initialPattern;
    private int d1, d2, d3, d4;
    private Map<String, Integer> extraIntegerFields = new LinkedHashMap<>();

    private boolean targetPatternSwitch = false;
    private boolean initialPatternSwitch = false;
    private boolean ruleSwitch = false;
    private Map<String, Boolean> extraIntegerFieldSwitches = new LinkedHashMap<>();

    public static class FieldNotEnableException extends RuntimeException {
    }

    public String getTargetPattern() {
        if (this.targetPatternSwitch) {
            return targetPattern;
        }
        throw new FieldNotEnableException();
    }

    public void setTargetPattern(String targetPattern) {
        this.targetPattern = targetPattern;
        this.targetPatternSwitch = true;
    }

    public String getInitialPattern() {
        if (this.initialPatternSwitch) {
            return initialPattern;
        }
        throw new FieldNotEnableException();
    }

    public void setInitialPattern(String initialPattern) {
        this.initialPattern = initialPattern;
        this.initialPatternSwitch = true;
    }

    public int[] getRulePattern() {
        if (this.ruleSwitch) {
            return new int[] { d1, d2, d3, d4 };
        }
        throw new FieldNotEnableException();
    }

    public void setRulePattern(int d1, int d2, int d3, int d4) {
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
        this.ruleSwitch = true;
    }

    public int getExtraIntegerFields(String key) {
        if (this.extraIntegerFieldSwitches.get(key).booleanValue()) {
            return this.extraIntegerFields.get(key).intValue();
        }
        throw new FieldNotEnableException();
    }

    public void setExtraIntegerFields(String key, int value) {
        this.extraIntegerFields.put(key, Integer.valueOf(value));
        this.extraIntegerFieldSwitches.put(key, true);
    }
}
