package cuprum.cmrule;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.callbacks.PrintBlockCallback;
import compositionmachine.machine.internal.CompositionMachine;
import compositionmachine.machine.predicates.LoopPredicate;
import compositionmachine.util.FileUtil;
import cuprum.cmrule.impl.HaltRecordCallback;
import cuprum.cmrule.impl.OneEdgeQuiverInitializer;
import cuprum.cmrule.rules.ECARule;
import cuprum.cmrule.rules.NotXorRule126;

public class Main {
    public static final int STEPS = 500;

    public static void main(String[] args) {
        int totalRules = 1 << (2 + 4 + 4 + 8);
        OneEdgeQuiverInitializer qInit = new OneEdgeQuiverInitializer();
        LoopPredicate predicate = new LoopPredicate();
        HaltRecordCallback haltCallback = new HaltRecordCallback();

        for (int i = 0; i < totalRules; i++) {
            int d1 = (i >> 16) & 0x03;
            int d2 = (i >> 12) & 0x0F;
            int d3 = (i >> 8) & 0x0F;
            int d4 = i & 0xFF;

            if (i % Setting.PRINT_STEP == 0) {
                String ruleName = d1 + "-" + d2 + "-" + d3 + "-" + d4;
                System.out.print("Rule: " + ruleName + " -- ");
            }

            haltCallback.setRule(i);
            ECARule rule = new ECARule(d1, d2, d3, d4);
            // NotXorRule126 rule = new NotXorRule126();
            CompositionMachine<ConnectedQuiver> machine = CompositionMachine.createMachine(qInit, rule, predicate);
            machine.addCallback(haltCallback);
            // machine.addCallback(new PrintBlockCallback());
            machine.execute(STEPS);
        }

        System.out.println("Writing records...");

        FileUtil.createOrChangeDirectory(Setting.DATA_PATH);
        File recordFile = Path.of(Setting.DATA_PATH, Setting.RECORD_FILE).toFile();
        PrintStream recordWriter = null;
        try {
            recordWriter = new PrintStream(recordFile, Charset.forName("UTF8"));
            ArrayList<Integer> record = haltCallback.getRecord();
            for (int rulePattern : record) {
                int d1 = (rulePattern >> 16) & 0x03;
                int d2 = (rulePattern >> 12) & 0x0F;
                int d3 = (rulePattern >> 8) & 0x0F;
                int d4 = rulePattern & 0xFF;

                String ruleName = d1 + "-" + d2 + "-" + d3 + "-" + d4;

                recordWriter.println(ruleName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (recordWriter != null)
                recordWriter.close();
        }
    }
}
