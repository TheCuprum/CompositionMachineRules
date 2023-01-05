package cuprum.cmrule.application;

import java.util.function.Function;

public class GeneralFunction implements Function<Object, Object> {
    private Class<?> inClass;
    private Class<?> outClass;
    private Function<Object, Object> internalFunction;

    public GeneralFunction(Class<?> inClass, Class<?> outClass, Function<Object, Object> func) {
        this.inClass = inClass;
        this.outClass = outClass;
        this.internalFunction = func;
    }

    public Class<?> getInClass() {
        return this.inClass;
    }

    public Class<?> getOutClass() {
        return this.outClass;
    }

    public Function<Object, Object> getFunction() {
        return this.internalFunction;
    }

    // @Override
    public Object apply(Object arg0) {
        return this.internalFunction.apply(arg0);
    }

    public GeneralFunction andThen(GeneralFunction after) {
        return new GeneralFunction(inClass, after.getOutClass(), this.internalFunction.andThen(after.getFunction()));
    }

    public GeneralFunction compose(GeneralFunction before) {
        return new GeneralFunction(inClass, before.getOutClass(), this.internalFunction.compose(before.getFunction()));
    }
}
