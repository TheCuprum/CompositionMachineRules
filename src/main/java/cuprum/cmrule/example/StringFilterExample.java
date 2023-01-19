package cuprum.cmrule.example;

import cuprum.cmrule.application.GeneralFunction;

public class StringFilterExample extends ExampleGeneration{
    public static GeneralFunction getStringFunction(String appendString) {
        return new GeneralFunction(String.class, String.class, (o) -> ((String) o).concat(appendString));
    }

    public static GeneralFunction[] generateFunctionList(String inputString) {
        GeneralFunction[] functionList = new GeneralFunction[inputString.length()];
        for (int index = 0; index < inputString.length(); index++) {
            functionList[index] = getStringFunction(inputString.substring(index, index + 1));
        }
        return functionList;
    }

    @Override
    protected GeneralFunction[] provideFunctions() {
        String funcString = "fuhellol";
        return generateFunctionList(funcString);
    }

    @Override
    protected Object provideInputObject(Class<?> cls) {
        if (cls == String.class)
            return "";
        else 
            throw new IllegalArgumentException("Only accept String type");
    }

    public static void main(String[] args) {
        StringFilterExample example = new StringFilterExample();
        example.runGeneration(args);
    }
}
