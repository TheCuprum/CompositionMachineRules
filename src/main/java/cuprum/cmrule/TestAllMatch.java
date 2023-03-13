package cuprum.cmrule;

import cuprum.cmrule.impl.List1DQuiverInitializer;
import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;
import cuprum.cmrule.tester.ECARuleTester;
import cuprum.cmrule.tester.TesterUtil;

public class TestAllMatch {
    private static List1DQuiverInitializer provideInitializer() {
        return new List1DQuiverInitializer(new String[] {
                "00110",
                "01110",
                "01100",
        });
    }

    public static void main(String[] args) {
        ProgramArgumentProcessor argProcessor = new ProgramArgumentProcessor();
        ProgramArgument parsedArgs = argProcessor
                .addExtraIntegerFields("pattern_length", "Pattern Length:")
                .addExtraIntegerFields("thread_count", "Max threads to run tasks:")
                .addExtraIntegerFields("io_threads", "Number of threads for file I/O (input 0 to disable):")
                .handleArgument(args);

        int patternLength = parsedArgs.getExtraIntegerFields("pattern_length");
        int concurrentSize = parsedArgs.getExtraIntegerFields("thread_count");
        int ioThreads = parsedArgs.getExtraIntegerFields("io_threads");

        // OneDimensionalQuiverInitializer targetQInit = provideInitializer();
        OneDimensionalQuiverInitializer targetQInit = new OneDimensionalQuiverInitializer("0".repeat(patternLength));
        OneDimensionalQuiverInitializer startQInit = new OneDimensionalQuiverInitializer("0".repeat(patternLength));

        TesterUtil.addTimer();

        ECARuleTester.testAllMatchesConcurrent(targetQInit, startQInit, "len=" + patternLength,
                Setting.ALL_MATCH_RECORD_FILE, 1000, concurrentSize, 5000, ioThreads > 0);
    }
}
