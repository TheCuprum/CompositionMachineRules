package cuprum.cmrule.evaluation;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import cuprum.cmrule.tester.record.AllConditionRecord;

public class EvaluatePerformanceCSV {
    static class StepRecord {
        public AllConditionRecord maxStepCondition;
        public AllConditionRecord minStepCondition;
        public Integer commonStep;
        public Integer maxCommonCount;

        public StepRecord(AllConditionRecord maxStepCondition, AllConditionRecord minStepCondition, Integer commonStep,
                Integer maxCommonCount) {
            this.maxStepCondition = maxStepCondition;
            this.minStepCondition = minStepCondition;
            this.commonStep = commonStep;
            this.maxCommonCount = maxCommonCount;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.maxStepCondition.getStringRepersentation())
                    .append(',')
                    .append(this.minStepCondition.getStringRepersentation())
                    .append(',')
                    .append(this.commonStep)
                    .append(',')
                    .append(this.maxCommonCount);

            return sb.toString();
        }
    }

    private static Map<String, StepRecord> evaluate(Map<String, List<AllConditionRecord>> dataRecordMap) {
        Map<String, StepRecord> performanceMap = new TreeMap<>((String x, String y) -> x.compareTo(y));
        for (Entry<String, List<AllConditionRecord>> dataEntry : dataRecordMap.entrySet()) {
            List<AllConditionRecord> recordList = dataEntry.getValue();
            Map<Integer, Integer> frequencyMap = new TreeMap<>((Integer x, Integer y) -> x.compareTo(y));

            AllConditionRecord maxStepCondition = new AllConditionRecord("none", -1, Integer.MIN_VALUE);
            AllConditionRecord minStepCondition = new AllConditionRecord("none", -1, Integer.MAX_VALUE);

            for (AllConditionRecord record : recordList) {
                if (record.getQuiverName().equals(dataEntry.getKey()))
                    continue;

                if (maxStepCondition.getStep().compareTo(record.getStep()) < 0)
                    maxStepCondition = record;

                if (minStepCondition.getStep().compareTo(record.getStep()) > 0)
                    minStepCondition = record;

                Integer count = frequencyMap.getOrDefault(record.getStep(), 0);
                frequencyMap.put(record.getStep(), count + 1);
            }

            Integer commonStep = 0;
            Integer maxCount = 0;
            for (Entry<Integer, Integer> freqEntry : frequencyMap.entrySet()) {
                if (maxCount.compareTo(freqEntry.getValue()) < 0) {
                    commonStep = freqEntry.getKey();
                    maxCount = freqEntry.getValue();
                }
            }

            performanceMap.put(dataEntry.getKey(),
                    new StepRecord(maxStepCondition, minStepCondition, commonStep, maxCount));
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
                targetPath,
                AllConditionRecord::parse);

        Map<String, StepRecord> evaluationResult = evaluate(dataRecordMap);

        Path resultOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "performance.csv");

        PrintStream resultWriter = null;
        try {
            resultWriter = new PrintStream(resultOutputPath.toFile(), Charset.forName("UTF-8"));
            resultWriter.println("targetQuiver,maxStep-quiverPattern,maxStep-rulePattern,maxStep," +
                    "minStep-quiverPattern,minStep-rulePattern,minStep," +
                    "commonStep,count");
            for (Entry<String, StepRecord> resultEntry : evaluationResult.entrySet()) {
                StepRecord r = resultEntry.getValue();
                StringBuilder sb = new StringBuilder();
                sb.append(resultEntry.getKey()).append(',')
                        .append(r.maxStepCondition.getStringRepersentation()).append(',')
                        .append(r.minStepCondition.getStringRepersentation()).append(',')
                        .append(r.commonStep).append(',')
                        .append(r.maxCommonCount);
                resultWriter.println(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resultWriter != null)
                resultWriter.close();
        }
    }

}
