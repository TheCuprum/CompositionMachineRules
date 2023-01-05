package cuprum.cmrule.impl;

import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;
import compositionmachine.machine.interfaces.QuiverInitializer;

@Deprecated
public class OneEdgeQuiverInitializer implements QuiverInitializer<ConnectedQuiver> {
    @Override
    public Quiver<ConnectedQuiver> generateQuiver() {
        ConnectedQuiver q1 = new ConnectedQuiver();
        for (int i = 0; i < 7; i++)
            q1.addArrow(0);
        q1.addArrow(1);
        for (int i = 0; i < 7; i++)
            q1.addArrow(0);

        Quiver<ConnectedQuiver> q = new Quiver<>();
        q.add(q1);

        return q;
    }

    @Override
    public boolean iterate() {
        return false;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    };
}
