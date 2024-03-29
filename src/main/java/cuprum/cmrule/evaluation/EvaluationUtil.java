package cuprum.cmrule.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import compositionmachine.util.FileUtil;
import cuprum.cmrule.datatype.Tuple;
import cuprum.cmrule.tester.record.ECARecord;

public class EvaluationUtil {
    public static <C extends Comparable<C>> Comparator<C> defaultComparator() {
        return (C o1, C o2) -> o1.compareTo(o2);
    }

    public static void writeJSON(Path resultOutputPath, Object o) {
        BufferedWriter resultWriter = null;
        try {
            resultWriter = new BufferedWriter(new FileWriter(resultOutputPath.toFile(), Charset.forName("UTF-8")));
            // resultWriter = new PrintWriter(
            //         new BufferedWriter(new FileWriter(resultOutputPath.toFile(), Charset.forName("UTF-8"))));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(o, resultWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resultWriter != null)
                try {
                    resultWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static Path getEvaluationFile(Path targetPath, String fileName) {
        String resultDirName = targetPath.getFileName().toString();
        File resultFolder = FileUtil.createOrChangeDirectory(Path.of(EvaluationSetting.PATH, resultDirName).toString());
        return Path.of(resultFolder.toString(), fileName);
    }

    public static <R extends ECARecord<R>> Map<String, List<R>> parseDataRecord(
            Path dataDirectory,
            Function<String, R> parseMethod) {
        File dataFolder = dataDirectory.toFile();
        String[] subDirList = dataFolder.list();

        if (subDirList == null)
            return null;

        Map<String, List<R>> dataRecordMap = new TreeMap<>((String s1, String s2) -> s1.compareTo(s2));
        // Map<String, List<R>> dataRecordMap = new HashMap<>();
        for (String subDirName : subDirList) {
            if (!subDirName.matches("^[01]+_.*"))
                continue;

            File dataFile = dataDirectory.resolve(subDirName).toFile();
            String[] nameSegment = subDirName.split("_", 2);

            if (nameSegment.length < 2)
                continue;

            String quiverPattern = nameSegment[0];
            List<R> recordList = new ArrayList<>();

            Scanner textScanner = null;
            try {
                textScanner = new Scanner(dataFile, Charset.forName("UTF-8"));
                while (textScanner.hasNext()) {
                    R record = parseMethod.apply(textScanner.nextLine());
                    if (record != null)
                        recordList.add(record);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (textScanner != null)
                    textScanner.close();
            }
            dataRecordMap.put(quiverPattern, recordList);
        }
        return dataRecordMap;
    }

    public static <R extends ECARecord<R>> Map<String, Object> processDataRecord(
            Path dataDirectory,
            Function<String, R> parseMethod,
            Function<Tuple<String, List<R>>, Object> processFunction) {
        File dataFolder = dataDirectory.toFile();
        String[] subDirList = dataFolder.list();

        if (subDirList == null)
            return null;

        Map<String, Object> evaluatedResult = new TreeMap<>((String s1, String s2) -> s1.compareTo(s2));
        for (String subDirName : subDirList) {
            if (!subDirName.matches("^[01]+_.*"))
                continue;

            File dataFile = dataDirectory.resolve(subDirName).toFile();
            String[] nameSegment = subDirName.split("_", 2);

            if (nameSegment.length < 2)
                continue;

            String quiverPattern = nameSegment[0];
            List<R> recordList = new ArrayList<>();

            Scanner textScanner = null;
            try {
                textScanner = new Scanner(dataFile, Charset.forName("UTF-8"));
                while (textScanner.hasNext()) {
                    R record = parseMethod.apply(textScanner.nextLine());
                    if (record != null)
                        recordList.add(record);
                }
                Object processedObject = processFunction.apply(new Tuple<String, List<R>>(quiverPattern, recordList));
                if (processedObject != null)
                    evaluatedResult.put(quiverPattern, processedObject);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (textScanner != null)
                    textScanner.close();
            }
        }
        return evaluatedResult;
    }
}
