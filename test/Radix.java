package test;

public enum Radix {

    R2("2"),
    R8("8"),
    R10("10"),
    R16("16");

    private final String radix;

    Radix(String radix) {
        this.radix = radix;
    }

    public String getRadix() {
        return radix;
    }
}
