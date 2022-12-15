package cuprum.cmrule.tester;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import compositionmachine.machine.Arrow;
import compositionmachine.machine.interfaces.BaseConnectedQuiver;
import compositionmachine.util.FileUtil;
import cuprum.cmrule.Setting;

public class TesterUtil {
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
