package cuprum.cmrule.evaluation;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import cuprum.cmrule.RuleUtil;
import cuprum.cmrule.datatype.Tuple;
import cuprum.cmrule.tester.record.AllConditionRecord;

public class EvaluatePerformancePerRule {
    static class PerRuleRecord {
        public int maxStep;
        public int minStep;
        public Map<Integer, Integer> stepFreq;

        public PerRuleRecord(int maxStep, int minStep, Map<Integer, Integer> stepFreq) {
            this.maxStep = maxStep;
            this.minStep = minStep;
            this.stepFreq = stepFreq;
        }
    }

    private static Object evaluateFunction(Tuple<String, List<AllConditionRecord>> dataEntry) {
        Map<String, PerRuleRecord> ruleMap = new TreeMap<>((String s1, String s2) -> s1.compareTo(s2));

        List<AllConditionRecord> recordList = dataEntry.getItemB();

        for (AllConditionRecord record : recordList) {
            if (record.getQuiverName().equals(dataEntry.getItemA()))
                continue;

            Integer recordStep = record.getStep();
            String ruleName = RuleUtil.ECAPatternToName(record.getRulePattern());

            PerRuleRecord perfRecord = ruleMap.get(ruleName);
            if (perfRecord == null) {
                perfRecord = new PerRuleRecord(Integer.MIN_VALUE, Integer.MAX_VALUE, new TreeMap<>());
                ruleMap.put(ruleName, perfRecord);
            }

            if (recordStep.compareTo(0) == 0)
                continue;

            if (perfRecord.maxStep < record.getStep())
                perfRecord.maxStep = recordStep;

            if (perfRecord.minStep > record.getStep())
                perfRecord.minStep = recordStep;

            Integer count = perfRecord.stepFreq.getOrDefault(recordStep, 0) + 1;
            perfRecord.stepFreq.put(record.getStep(), count);
        }
        return ruleMap;
    }

    public static void main(String[] args) {
        String targetDir;
        if (args.length == 1) {
            targetDir = args[0];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Target directory:");
            targetDir = sc.nextLine();
            sc.close();
        }

        if (targetDir.length() <= 0)
            throw new IllegalArgumentException();

        Path targetPath = Path.of(targetDir).toAbsolutePath();
        Map<String, Object> evaluationResult = EvaluationUtil.processDataRecord(targetPath, AllConditionRecord::parse, EvaluatePerformancePerRule::evaluateFunction);

        Path resultOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "performance_rule.json");
        System.out.println(resultOutputPath);
        EvaluationUtil.writeJSON(resultOutputPath, evaluationResult);
    }
}
