package cuprum.cmrule.evaluation;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Scanner;
import java.util.TreeMap;

import cuprum.cmrule.RuleUtil;
import cuprum.cmrule.datatype.Tuple;
import cuprum.cmrule.tester.record.AllConditionRecord;

public class EvaluateCommonCondition {
    private static Map<String, Map<String, Integer>> evaluate(
            Map<String, List<AllConditionRecord>> dataRecordMap) {
        Map<String, Map<String, Integer>> conditionMap = new TreeMap<>(
                EvaluationUtil.defaultComparator());
        Map<String, Map<String, Integer>> commonConditionMap = new TreeMap<>(
                EvaluationUtil.defaultComparator());
        // (initialPattern, rule) -> [targetPattern -> step]

        for (Entry<String, List<AllConditionRecord>> dataEntry : dataRecordMap.entrySet()) {
            List<AllConditionRecord> recordList = dataEntry.getValue();

            for (AllConditionRecord record : recordList) {
                if (record.getQuiverName().equals(dataEntry.getKey()))
                    continue;

                String initalConditionTupleString = Tuple.valueOf(
                        record.getQuiverName(),
                        RuleUtil.ECAPatternToName(record.getRulePattern())).toString();
                Map<String, Integer> stepMap = conditionMap.get(initalConditionTupleString);
                if (stepMap == null) {
                    stepMap = new TreeMap<>((String s1, String s2) -> s1.compareTo(s2));
                    conditionMap.put(initalConditionTupleString, stepMap);
                }

                Integer putResult = stepMap.put(dataEntry.getKey(), record.getStep());
                if (putResult != null)
                    System.err.println("Duplicated key.");
            }
        }

        int targetCount = dataRecordMap.size();
        for (String initalCondition : conditionMap.keySet()) {
            Map<String, Integer> stepMap = conditionMap.get(initalCondition);
            if (stepMap.size() == targetCount)
                commonConditionMap.put(initalCondition, stepMap);
        }

        // return commonConditionMap;
        return conditionMap;
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

        Map<String, Map<String, Integer>> evaluationResult = evaluate(dataRecordMap);

        Map<String, Integer> commonConditionCount = new TreeMap<>(EvaluationUtil.defaultComparator());
        for (Entry<String, Map<String, Integer>> resultEntry : evaluationResult.entrySet()) {
            commonConditionCount.put(resultEntry.getKey(), resultEntry.getValue().size());
        }

        Path resultOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "common_condition.json");
        Path resultCountOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "common_condition_count.json");

        EvaluationUtil.writeJSON(resultOutputPath, evaluationResult);
        EvaluationUtil.writeJSON(resultCountOutputPath, commonConditionCount);
    }
}
