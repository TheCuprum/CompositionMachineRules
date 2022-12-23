package cuprum.cmrule.impl;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.QuiverInitializer;

public class OneDimensionalQuiverInitializer implements QuiverInitializer<ConnectedQuiver> {

    protected String initString;
    protected ConnectedQuiver cachedCQ;

    public OneDimensionalQuiverInitializer() {
        this("000000000");
    }

    public OneDimensionalQuiverInitializer(String bitString) {
        this.initString = bitString;
        this.pregenerateQuiver();
    }

    private void pregenerateQuiver() {
        ConnectedQuiver cq1 = new ConnectedQuiver();
        for (char c : this.initString.toCharArray()) {
            switch (c) {
                case '0':
                    cq1.addArrow(0);
                    break;
                case '1':
                    cq1.addArrow(1);
                    break;
                default:
                    throw new RuntimeException("Illegal character in quiver bit string");
            }
        }

        this.cachedCQ = cq1;
    }

    public String getName() {
        return this.initString;
    }

    @Override
    public Quiver<ConnectedQuiver> generateQuiver() {
        Quiver<ConnectedQuiver> q = new Quiver<>();
        q.add(this.cachedCQ.typedClone());
        return q;
    };
    // @Override
    // public Quiver<ConnectedQuiver> generateQuiver() {
    // ConnectedQuiver cq1 = new ConnectedQuiver();
    // for (int i = 0; i < 7; i++)
    // cq1.addArrow(0);
    // cq1.addArrow(1);
    // for (int i = 0; i < 7; i++)
    // cq1.addArrow(0);

    // Quiver<ConnectedQuiver> q = new Quiver<>();
    // q.add(cq1);

    // return q;
    // };
}
