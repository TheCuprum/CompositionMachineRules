package cuprum.cmrule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class ProgramArgumentProcessor {
    private boolean targetPatternSwitch = false;
    private boolean initialPatternSwitch = false;
    private boolean ruleSwitch = false;
    private Map<String, String> extraFields = new LinkedHashMap<>();
    // <key, help_text>

    public ProgramArgument handleArgument(String[] args) {
        int expectedLength = (this.targetPatternSwitch ? 1 : 0)
                + (this.initialPatternSwitch ? 1 : 0)
                + (this.ruleSwitch ? 4 : 0)
                + extraFields.size();
        if (args.length == expectedLength) {
            return this.parseArguments(args);
        } else {
            return this.requestArguments();
        }
    }

    private ProgramArgument parseArguments(String[] args) {
        int index = 0;
        ProgramArgument struct = new ProgramArgument();

        if (this.targetPatternSwitch) {
            struct.setTargetPattern(args[index]);
            index += 1;
        }
        if (this.initialPatternSwitch) {
            struct.setInitialPattern(args[index]);
            index += 1;
        }
        if (this.ruleSwitch) {
            struct.setRulePattern(
                    Integer.parseInt(args[index]),
                    Integer.parseInt(args[index + 1]),
                    Integer.parseInt(args[index + 2]),
                    Integer.parseInt(args[index + 3]));
            index += 4;
        }
        for (String key : this.extraFields.keySet()) {
            struct.setExtraField(key, args[index]);
            index++;
        }

        return struct;
    }

    private ProgramArgument requestArguments() {
        Scanner sc = new Scanner(System.in);
        ProgramArgument struct = new ProgramArgument();

        if (this.targetPatternSwitch) {
            System.out.println("Enter target quiver pattern:");
            struct.setTargetPattern(sc.nextLine());
        }
        if (this.initialPatternSwitch) {
            System.out.println("Enter initial quiver pattern:");
            struct.setInitialPattern(sc.nextLine());
        }
        if (this.ruleSwitch) {
            System.out.println("Enter the rules (d1, d2, d3, d4):");
            struct.setRulePattern(
                    sc.nextInt(),
                    sc.nextInt(),
                    sc.nextInt(),
                    sc.nextInt());
            sc.nextLine();
        }
        if (this.extraFields.size() > 0) {
            for (Entry<String, String> entry : this.extraFields.entrySet()) {
                System.out.println(entry.getValue());
                struct.setExtraField(entry.getKey(), sc.nextLine());
            }
        }

        sc.close();
        return struct;
    }

    public ProgramArgumentProcessor addTargetPattern() {
        this.targetPatternSwitch = true;
        return this;
    }

    public ProgramArgumentProcessor addInitialPattern() {
        this.initialPatternSwitch = true;
        return this;
    }

    public ProgramArgumentProcessor addRule() {
        this.ruleSwitch = true;
        return this;
    }

    public ProgramArgumentProcessor addExtraField(String key, String hint) {
        this.extraFields.put(key, hint);
        return this;
    }
}
