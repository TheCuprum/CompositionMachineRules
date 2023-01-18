package cuprum.cmrule.tester;

import java.util.ArrayList;
import java.util.List;

import cuprum.cmrule.impl.OneDimensionalQuiverInitializer;

public class TestMonitor {
    private List<OneDimensionalQuiverInitializer> qInitList = new ArrayList<>();

    private List<String> infoLines = new ArrayList<>();

    public TestMonitor(List<OneDimensionalQuiverInitializer> qInitList) {
        for (OneDimensionalQuiverInitializer qInit : qInitList) {
            this.qInitList.add(qInit);
        }
    }

    public void monitor(int milliInterval) {
        while (true) {
            for (int index = this.infoLines.size() - 1; index >= 0; index--) {
                System.out.print("\b".repeat(this.infoLines.get(index).length()));
                if (index != 0)
                    System.out.print("\033[1A");
            }
            this.infoLines.clear();

            int indexCount = 0;
            boolean stopFlag = true;
            for (int index = 0; index < this.qInitList.size(); index++) {
                String printString = "Task-" + indexCount + ": ";
                if (this.qInitList.get(index).hasNext()) {
                    stopFlag = false;
                    printString += this.qInitList.get(index).getName();
                } else {
                    printString += "Done"
                            + " ".repeat(Math.max(this.qInitList.get(indexCount).getName().length() - 4, 0));
                }
                this.infoLines.add(printString);
                System.out.print(printString);
                if (index != this.qInitList.size() - 1)
                    System.out.println();

                indexCount++;
            }
            if (stopFlag)
                break;

            try {
                Thread.sleep(milliInterval);
            } catch (InterruptedException e) {
                System.out.println("Monitor interrupted.");
                break;
            }
        }
    }
}
