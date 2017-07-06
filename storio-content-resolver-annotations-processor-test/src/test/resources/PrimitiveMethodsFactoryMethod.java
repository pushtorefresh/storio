package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrimitiveMethodsFactoryMethod {

    private boolean field1;

    private short field2;

    private int field3;

    private long field4;

    private float field5;

    private double field6;

    private String field7;

    private byte[] field8;

    @StorIOContentResolverCreator
    public static PrimitiveMethodsFactoryMethod create(boolean field1, short field2, int field3, long field4, float field5,
                                                       double field6, String field7, byte[] field8) {
        return new PrimitiveMethodsFactoryMethod(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    private PrimitiveMethodsFactoryMethod(boolean field1, short field2, int field3, long field4, float field5,
                                         double field6, String field7, byte[] field8) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
        this.field7 = field7;
        this.field8 = field8;
    }

    @StorIOContentResolverColumn(name = "field1")
    public boolean getField1() {
        return field1;
    }

    @StorIOContentResolverColumn(name = "field2")
    public short getField2() {
        return field2;
    }

    @StorIOContentResolverColumn(name = "field3")
    public int getField3() {
        return field3;
    }

    @StorIOContentResolverColumn(name = "field4", key = true)
    public long getField4() {
        return field4;
    }

    @StorIOContentResolverColumn(name = "field5")
    public float getField5() {
        return field5;
    }

    @StorIOContentResolverColumn(name = "field6")
    public double getField6() {
        return field6;
    }

    @StorIOContentResolverColumn(name = "field7")
    public String getField7() {
        return field7;
    }

    @StorIOContentResolverColumn(name = "field8")
    public byte[] getField8() {
        return field8;
    }
}