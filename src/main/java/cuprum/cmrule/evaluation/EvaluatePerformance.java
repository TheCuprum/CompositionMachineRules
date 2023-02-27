package cuprum.cmrule.evaluation;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import cuprum.cmrule.tester.record.AllConditionRecord;

public class EvaluatePerformance {
    static class StepRecord {
        public Set<AllConditionRecord> maxStepConditionSet;
        public Set<AllConditionRecord> minStepConditionSet;
        public Map<Integer, Integer> stepFreq;

        public StepRecord(Set<AllConditionRecord> maxStepConditionSet, Set<AllConditionRecord> minStepConditionSet,
                Map<Integer, Integer> stepFreq) {
            this.maxStepConditionSet = maxStepConditionSet;
            this.minStepConditionSet = minStepConditionSet;
            this.stepFreq = stepFreq;
        }

        public StepRecordCount getBrief() {
            return new StepRecordCount(
                    this.maxStepConditionSet.size(),
                    this.minStepConditionSet.size(),
                    stepFreq);
        }
    }

    static class StepRecordCount {
        public Integer maxStepConditionCount;
        public Integer minStepConditionCount;
        public Map<Integer, Integer> stepFreq;

        public StepRecordCount(Integer maxStepConditionCount, Integer minStepConditionCount,
                Map<Integer, Integer> stepFreq) {
            this.maxStepConditionCount = maxStepConditionCount;
            this.minStepConditionCount = minStepConditionCount;
            this.stepFreq = stepFreq;
        }
    }

    private static Map<String, StepRecord> evaluate(Map<String, List<AllConditionRecord>> dataRecordMap) {
        Map<String, StepRecord> performanceMap = new TreeMap<>((String s1, String s2) -> s1.compareTo(s2));

        for (Entry<String, List<AllConditionRecord>> dataEntry : dataRecordMap.entrySet()) {
            List<AllConditionRecord> recordList = dataEntry.getValue();

            Map<Integer, Integer> frequencyMap = new TreeMap<>((Integer x, Integer y) -> x.compareTo(y));

            Integer maxStep = Integer.MIN_VALUE;
            Integer minStep = Integer.MAX_VALUE;
            for (AllConditionRecord record : recordList) {
                if (record.getQuiverName().equals(dataEntry.getKey()))
                    continue;

                Integer recordStep = record.getStep();
                if (recordStep.compareTo(0) == 0)
                    continue;

                if (maxStep.compareTo(record.getStep()) < 0)
                    maxStep = recordStep;

                if (minStep.compareTo(record.getStep()) > 0)
                    minStep = recordStep;

                Integer count = frequencyMap.getOrDefault(recordStep, 0) + 1;
                frequencyMap.put(record.getStep(), count);
            }

            Set<AllConditionRecord> maxStepConditionSet = new TreeSet<>(
                    (AllConditionRecord r1, AllConditionRecord r2) -> r1.compareTo(r2));
            Set<AllConditionRecord> minStepConditionSet = new TreeSet<>(
                    (AllConditionRecord r1, AllConditionRecord r2) -> r1.compareTo(r2));

            for (AllConditionRecord record : recordList) {
                if (record.getQuiverName().equals(dataEntry.getKey()) || record.getStep().compareTo(0) == 0)
                    continue;
                    
                if (maxStep.equals(record.getStep()))
                    maxStepConditionSet.add(record);
                if (minStep.equals(record.getStep()))
                    minStepConditionSet.add(record);
            }

            performanceMap.put(dataEntry.getKey(),
                    new StepRecord(maxStepConditionSet, minStepConditionSet, frequencyMap));
        }
        return performanceMap;
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

        Map<String, StepRecord> evaluationResult = evaluate(dataRecordMap);
        Map<String, StepRecordCount> evaluationResultBrief = new TreeMap<>(EvaluationUtil.defaultComparator());
        for (Entry<String, StepRecord> resultEntry : evaluationResult.entrySet()) {
            evaluationResultBrief.put(resultEntry.getKey(), resultEntry.getValue().getBrief());
        }

        Path resultOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "performance.json");
        Path resultBriefOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "performance_brief.json");
        System.out.println(resultOutputPath);
        System.out.println(resultBriefOutputPath);
        EvaluationUtil.writeJSON(resultOutputPath, evaluationResult);
        EvaluationUtil.writeJSON(resultBriefOutputPath, evaluationResultBrief);
    }
}
