package cuprum.cmrule.datatype;

public class Tuple<A, B> {
    private A itemA;
    private B itemB;

    public Tuple(A itemA, B itemB) {
        this.itemA = itemA;
        this.itemB = itemB;
    }

    public static <A, B> Tuple<A,B> valueOf(A itemA, B itemB){
        return new Tuple<A,B>(itemA, itemB);
    }

    public A getItemA() {
        return this.itemA;
    }

    public B getItemB() {
        return this.itemB;
    }

    public Object get(int index) {
        switch (index) {
            case 0:
                return this.itemA;
            case 1:
                return this.itemB;
            default:
                throw new IndexOutOfBoundsException("index must be 0 or 1.");
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Tuple<A, B>(itemA, itemB);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple))
            return false;

        Tuple<?, ?> other = (Tuple<?, ?>) obj;

        return this.itemA.equals(other.getItemA()) && this.itemB.equals(other.getItemB());
    }

    @Override
    public String toString() {
        return "(" + itemA.toString() + "," + itemB.toString() + ")";
    }
}
