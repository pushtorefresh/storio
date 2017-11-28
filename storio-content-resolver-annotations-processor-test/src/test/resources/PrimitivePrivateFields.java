package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrimitivePrivateFields {

    @StorIOContentResolverColumn(name = "field1")
    private boolean field1;

    @StorIOContentResolverColumn(name = "field2")
    private short field2;

    @StorIOContentResolverColumn(name = "field3")
    private int field3;

    @StorIOContentResolverColumn(name = "field4", key = true)
    private long field4;

    @StorIOContentResolverColumn(name = "field5")
    private float field5;

    @StorIOContentResolverColumn(name = "field6")
    private double field6;

    @StorIOContentResolverColumn(name = "field7")
    private String field7;

    @StorIOContentResolverColumn(name = "field8")
    private byte[] field8;

    @StorIOContentResolverCreator
    public PrimitivePrivateFields(boolean field1, short field2, int field3, long field4, float field5, double field6, String field7, byte[] field8) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
        this.field7 = field7;
        this.field8 = field8;
    }

    public boolean isField1() {
        return field1;
    }

    public short getField2() {
        return field2;
    }

    public int getField3() {
        return field3;
    }

    public long getField4() {
        return field4;
    }

    public float getField5() {
        return field5;
    }

    public double getField6() {
        return field6;
    }

    public String getField7() {
        return field7;
    }

    public byte[] getField8() {
        return field8;
    }
}