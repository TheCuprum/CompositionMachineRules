package cuprum.cmrule.example;

import java.nio.charset.StandardCharsets;

import cuprum.cmrule.application.GeneralFunction;
import cuprum.cmrule.datatype.Tuple;

public class CharRepeaterExample extends ExampleGeneration {

    @Override
    protected GeneralFunction[] provideFunctions() {
        GeneralFunction byteArrToDouble, doubleToLong, longToTupleIntChar, tupleIntCharToString, stringToCharList;
        byteArrToDouble = new GeneralFunction(byte[].class, Double.class,
                o -> {
                    byte[] bArr = (byte[]) o;
                    String doubleString = new String(bArr, StandardCharsets.UTF_8);
                    return Double.valueOf(doubleString);
                });
        doubleToLong = new GeneralFunction(Double.class, Long.class,
                o -> Long.valueOf((long) Math.floor((Double) o)));
        longToTupleIntChar = new GeneralFunction(Long.class, Tuple.class,
                o -> {
                    long num = (Long) o;
                    Integer intPart = (int) (num & 0xFFFFFFFF);
                    Character charPart = (char) ((num >> 32) & 0x00FF);
                    return new Tuple<Integer, Character>(intPart, charPart);
                });
        tupleIntCharToString = new GeneralFunction(Tuple.class, String.class,
                o -> {
                    Tuple<Integer, Character> tuple = (Tuple<Integer, Character>) o;
                    int count = tuple.getItemA();
                    char ch = tuple.getItemB();
                    return String.valueOf(ch).repeat(count);
                });
        stringToCharList = new GeneralFunction(String.class, char[].class,
                o -> ((String) o).toCharArray());

        return new GeneralFunction[] {
                byteArrToDouble,
                doubleToLong,
                longToTupleIntChar,
                tupleIntCharToString,
                stringToCharList };
    }

    @Override
    protected Object provideInputObject(Class<?> cls) {
        if (cls == Long.class)
            return Long.valueOf((((long)'a' & 0xFF) << 32) | (10 & 0xFFFFFFFF));
        else
            throw new IllegalArgumentException("Only accept Long type");
    }

    public static void main(String[] args) {
        CharRepeaterExample example = new CharRepeaterExample();
        example.runGeneration(args);
    }
}
