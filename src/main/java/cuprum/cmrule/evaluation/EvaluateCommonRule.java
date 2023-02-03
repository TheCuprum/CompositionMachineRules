package cuprum.cmrule.evaluation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import cuprum.cmrule.RuleUtil;
import cuprum.cmrule.tester.record.AllConditionRecord;

public class EvaluateCommonRule {
    private static Map<String, Map<String, Map<String, Integer>>> evaluate(
            Map<String, List<AllConditionRecord>> dataRecordMap) {
        Map<String, Map<String, Map<String, Integer>>> ruleMap = new TreeMap<>(
                EvaluationUtil.defaultComparator());
        Map<String, Map<String, Map<String, Integer>>> commonRuleMap = new TreeMap<>(
                EvaluationUtil.defaultComparator());
        // rule -> [initialPattern -> step]

        for (Entry<String, List<AllConditionRecord>> dataEntry : dataRecordMap.entrySet()) {
            List<AllConditionRecord> recordList = dataEntry.getValue();
            String targetQuiverPattern = dataEntry.getKey();

            for (AllConditionRecord record : recordList) {
                if (record.getQuiverName().equals(dataEntry.getKey()))
                    continue;

                String ruleName = RuleUtil.ECAPatternToName(record.getRulePattern());

                Map<String, Map<String, Integer>> targetQuiverMap = ruleMap.get(ruleName);
                if (targetQuiverMap == null) {
                    targetQuiverMap = new TreeMap<>(EvaluationUtil.defaultComparator());
                    ruleMap.put(ruleName, targetQuiverMap);
                }

                Map<String, Integer> stepMap = targetQuiverMap.get(targetQuiverPattern);
                if (stepMap == null) {
                    stepMap = new TreeMap<>((String s1, String s2) -> s1.compareTo(s2));
                    targetQuiverMap.put(targetQuiverPattern, stepMap);
                }

                Integer putResult = stepMap.put(record.getQuiverName(), record.getStep());
                if (putResult != null)
                    System.err.print("");
                // System.err.println("Duplicated key.");
            }
        }

        int targetCount = dataRecordMap.size();
        for (String ruleName : ruleMap.keySet()) {
            Map<String, Map<String, Integer>> targetQuiverMap = ruleMap.get(ruleName);
            if (targetQuiverMap.size() == targetCount)
                commonRuleMap.put(ruleName, targetQuiverMap);
        }

        return commonRuleMap;
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
        Map<String, List<AllConditionRecord>> dataRecordMap = EvaluationUtil.parseDataRecord(
                targetPath, AllConditionRecord::parse);

        Map<String, Map<String, Map<String, Integer>>> evaluationResult = evaluate(dataRecordMap);
        Map<String, Integer> commentMap = new HashMap<>();
        commentMap.put("rule_count", evaluationResult.size());

        // Map<String, Integer> commonRuleCount = new
        // TreeMap<>(getRuleNameComparator());
        // for (Entry<String,Map<String, Integer>> resultEntry:
        // evaluationResult.entrySet()){
        // commonRuleCount.put(resultEntry.getKey(), resultEntry.getValue().size());
        // }

        Path resultOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "common_rule.json");
        // Path resultCountOutputPath = EvaluationUtil.getEvaluationFile(targetPath,
        // "common_rule_count.json");

        EvaluationUtil.writeJSON(resultOutputPath, new Object[] { commentMap, evaluationResult });
        // EvaluationUtil.writeJSON(resultCountOutputPath, commonRuleCount);
    }
}
