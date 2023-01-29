package cuprum.cmrule.tester;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import compositionmachine.machine.Arrow;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.util.FileUtil;
import cuprum.cmrule.Setting;
import cuprum.cmrule.tester.record.ECARecord;

public class TesterUtil {
    public static void addTimer(){
        System.out.println("Start Time: " + (new Date()).toString());
        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long endTime = System.currentTimeMillis();
            long deltaTime = endTime - startTime;
            long day = deltaTime / (1000 * 60 * 60 * 24);
            long hour = deltaTime / (1000 * 60 * 60) % 24;
            long minute = deltaTime / (1000 * 60) % 60;
            long second = deltaTime / 1000 % 60;
            long milli = deltaTime % 1000;
            System.out.println(String.format("Total Time: %d:%d:%d:%d.%d", day, hour, minute, second, milli));
        }));
    }

    public static int mapDelta2RuleNumber(int ruleNumber) {
        if (ruleNumber > 15) {
            throw new IllegalArgumentException("Rule number must less than 16.");
        }
        switch (ruleNumber) {
            case 2:
                return 4;
            case 3:
                return 5;
            case 10:
                return 12;
            case 11:
                return 13;
            case 4:
                return 2;
            case 5:
                return 3;
            case 12:
                return 10;
            case 13:
                return 11;
            default:
                return ruleNumber;
        }
    }

    public static <R extends ECARecord> void writeRecordListToFile(List<R> recordList, String fileName) {
        writeRecordListToFile(recordList, Setting.DATA_PATH, fileName);
    }

    public static <R extends ECARecord> void writeRecordListToFile(
            List<R> recordList, String dataPath, String fileName) {
        File dataDir = FileUtil.createOrChangeDirectory(dataPath);
        File recordFile = Path.of(dataDir.getPath(), fileName).toFile();
        PrintStream recordWriter = null;
        try {
            recordWriter = new PrintStream(recordFile, Charset.forName("UTF8"));
            for (int index = 0; index < recordList.size(); index++) {
                recordWriter.println(recordList.get(index).getStringRepersentation());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (recordWriter != null)
                recordWriter.close();
        }
    }

    @Deprecated
    public static void writeStateAndRuleListToFile(ArrayList<String> stateList, ArrayList<Integer> ruleList,
            ArrayList<Integer> stepList, String fileName) {
        int len = Math.min(Math.min(stateList.size(), ruleList.size()), stepList.size());
        FileUtil.createOrChangeDirectory(Setting.DATA_PATH);
        File recordFile = Path.of(Setting.DATA_PATH, fileName).toFile();
        PrintStream recordWriter = null;
        try {
            recordWriter = new PrintStream(recordFile, Charset.forName("UTF8"));
            for (int index = 0; index < len; index++) {
                String statePattern = stateList.get(index);
                int rulePattern = ruleList.get(index);
                String step = stepList.get(index).toString();
                int d1 = (rulePattern >> 16) & 0x03;
                int d2 = (rulePattern >> 12) & 0x0F;
                int d3 = (rulePattern >> 8) & 0x0F;
                int d4 = rulePattern & 0xFF;

                String ruleName = d1 + "-" + d2 + "-" + d3 + "-" + d4;

                recordWriter.println(statePattern + "," + ruleName + "," + step);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (recordWriter != null)
                recordWriter.close();
        }
    }

    @Deprecated
    public static void writeRuleListToFile(ArrayList<Integer> ruleList, String fileName) {
        FileUtil.createOrChangeDirectory(Setting.DATA_PATH);
        File recordFile = Path.of(Setting.DATA_PATH, fileName).toFile();
        PrintStream recordWriter = null;
        try {
            recordWriter = new PrintStream(recordFile, Charset.forName("UTF8"));
            for (int rulePattern : ruleList) {
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

    public static <CQ extends BaseConnectedQuiver<CQ>> int countArrows(BaseConnectedQuiver<CQ> bcq) {
        int arrowCount = 0;
        Iterator<Arrow> arrowIterator = bcq.getArrowIterator();
        while (arrowIterator.hasNext()) {
            int state = bcq.getArrowState(arrowIterator.next());
            arrowCount += state > 0 ? 1 : 0;
        }
        return arrowCount;
    }

    public static <CQ extends BaseConnectedQuiver<CQ>> int countArrowWeights(BaseConnectedQuiver<CQ> bcq) {
        int arrowCount = 0;
        Iterator<Arrow> arrowIterator = bcq.getArrowIterator();
        while (arrowIterator.hasNext()) {
            int state = bcq.getArrowState(arrowIterator.next());
            arrowCount += state;
        }
        return arrowCount;
    }
}
