package cuprum.cmrule.application;

import java.util.ArrayList;
import java.util.Iterator;

import compositionmachine.machine.Arrow;
import compositionmachine.machine.ConnectedQuiver;
import compositionmachine.machine.Quiver;

public class OneDimFunctionMapper {
    ArrayList<GeneralFunction[]> funcList;

    public OneDimFunctionMapper(GeneralFunction[][] functionList) {
        this.funcList = new ArrayList<>();
        for (GeneralFunction[] functions : functionList) {
            this.addFunction(functions);
        }
    }

    public OneDimFunctionMapper(ArrayList<GeneralFunction[]> functionList) {
        this.funcList = functionList;
    }

    public static GeneralFunction[] mapFunctions(GeneralFunction[][] functionList, Quiver<ConnectedQuiver> quiver){
        OneDimFunctionMapper mapper = new OneDimFunctionMapper(functionList);
        return mapper.mapFunctions(quiver);
    }

    private static boolean functionTypeCheck(GeneralFunction[] functions) {
        for (int index = 0; index < functions.length - 1; index++) {
            if (!functions[index].getOutClass().equals(functions[index + 1].getInClass())) {
                return false;
            }
        }
        return true;
    }

    public void addFunction(GeneralFunction[] functions) {
        if (OneDimFunctionMapper.functionTypeCheck(functions)) {
            this.funcList.add(functions);
        } else {
            throw new IllegalArgumentException("Functions' in and out types must match.");
        }
    }

    public GeneralFunction[] mapFunctions(Quiver<ConnectedQuiver> quiver) {
        if (quiver.size() != this.funcList.size())
            throw new IllegalArgumentException("Quiver and function size mismatch.");

        for (int index = 0; index < quiver.size(); index++) {
            if (quiver.get(index).getArrowStates().size() != this.funcList.get(index).length)
                throw new IllegalArgumentException("Quiver and function size mismatch.");
        }

        ArrayList<GeneralFunction> retFunctions = new ArrayList<>();
        ArrayList<GeneralFunction> functionBuffer = new ArrayList<>();
        for (int i = 0; i < quiver.size(); i++) {
            ConnectedQuiver cq = quiver.get(i);
            GeneralFunction[] functions = this.funcList.get(i);
            int len = cq.getArrowStates().size();
            Iterator<Arrow> arrIter = cq.getArrowIterator();
            for (int j = 0; j < len; j++) {
                int state = cq.getArrowState(arrIter.next());
                if (state > 0) {
                    functionBuffer.add(functions[j]);
                } else if (functionBuffer.size() > 0) {
                    // combine functions
                    GeneralFunction fn = functionBuffer.get(0);
                    for (int index = 1; index < functionBuffer.size(); index++) {
                        fn = fn.andThen(functionBuffer.get(index));
                    }
                    functionBuffer.clear();
                    retFunctions.add(fn);
                }
            }
        }
        return retFunctions.toArray(new GeneralFunction[0]);
    }
}
