package cuprum.cmrule;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProgramArgument {
    private String targetPattern;
    private String initialPattern;
    private int d1, d2, d3, d4;
    private Map<String, String> extraFields = new LinkedHashMap<>();

    private boolean targetPatternSwitch = false;
    private boolean initialPatternSwitch = false;
    private boolean ruleSwitch = false;
    private Map<String, Boolean> extraFieldSwitches = new LinkedHashMap<>();

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

    public String getExtraField(String key) {
        if (this.extraFieldSwitches.get(key).booleanValue()) {
            return this.extraFields.get(key);
        }
        throw new FieldNotEnableException();
    }

    public Integer getExtraFieldAsInteger(String key) {
        if (this.extraFieldSwitches.get(key).booleanValue()) {
            return Integer.parseInt(this.extraFields.get(key));
        }
        throw new FieldNotEnableException();
    }

    public void setExtraField(String key, String value) {
        this.extraFields.put(key, value);
        this.extraFieldSwitches.put(key, true);
    }
}
