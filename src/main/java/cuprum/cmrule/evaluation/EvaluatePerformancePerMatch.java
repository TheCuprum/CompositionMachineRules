package cuprum.cmrule.evaluation;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import cuprum.cmrule.datatype.Tuple;
import cuprum.cmrule.tester.record.AllConditionRecord;

public class EvaluatePerformancePerMatch {
    public static int mostMinCount = Integer.MAX_VALUE;

    static class StepRecord {
        public int maxStep;
        public int minStep;
        public Map<Integer, Integer> stepFreq = new TreeMap<>();
        // public transient Map<Integer, Integer> stepFreq = new TreeMap<>();

        public StepRecord(int maxStep, int minStep) {
            this.maxStep = maxStep;
            this.minStep = minStep;
        }
    }

    private static Object evaluate(Tuple<String, List<AllConditionRecord>> dataEntry) {
        Map<String, StepRecord> startConditionMap = new TreeMap<>((String s1, String s2) -> s1.compareTo(s2));
        List<AllConditionRecord> recordList = dataEntry.getItemB();

        for (AllConditionRecord record : recordList) {
            if (record.getQuiverName().equals(dataEntry.getItemA()))
                continue;

            Integer recordStep = record.getStep();
            String startQuiverName = record.getQuiverName();

            StepRecord perfRecord = startConditionMap.get(startQuiverName);
            if (perfRecord == null) {
                perfRecord = new StepRecord(Integer.MIN_VALUE, Integer.MAX_VALUE);
                startConditionMap.put(startQuiverName, perfRecord);
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

        for (Entry<String, StepRecord> entry : startConditionMap.entrySet()){
            Map<Integer, Integer> freqMap = entry.getValue().stepFreq;

            int minStep = entry.getValue().minStep;
            int minCount = entry.getValue().stepFreq.get(minStep);

            if (minCount < mostMinCount)
                mostMinCount = minCount;
            
        //     for (Entry<Integer, Integer> freqEntry : freqMap.entrySet()){
        //         if (freqEntry.getKey().equals(minStep))
        //             continue;

        //         if (freqEntry.getValue() > minCount){
        //             System.out.println("Minstep is not the most " + entry.getKey() +  " to " + dataEntry.getItemA());
        //             System.out.println(minCount + " < " + freqEntry.getValue());
        //             break;
        //         }
        //     }
        }
        return startConditionMap;
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

        Map<String, Object> evaluationResult = EvaluationUtil.processDataRecord(targetPath, AllConditionRecord::parse, EvaluatePerformancePerMatch::evaluate);

        System.out.println("Most min count is: "+ mostMinCount);

        Path resultOutputPath = EvaluationUtil.getEvaluationFile(targetPath, "performance_match.json");
        System.out.println(resultOutputPath);
        EvaluationUtil.writeJSON(resultOutputPath, evaluationResult);
    }
}
