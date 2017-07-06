package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrimitivePrivateFields {

    @StorIOSQLiteColumn(name = "field1")
    private boolean field1;

    @StorIOSQLiteColumn(name = "field2")
    private short field2;

    @StorIOSQLiteColumn(name = "field3")
    private int field3;

    @StorIOSQLiteColumn(name = "field4", key = true)
    private long field4;

    @StorIOSQLiteColumn(name = "field5")
    private float field5;

    @StorIOSQLiteColumn(name = "field6")
    private double field6;

    @StorIOSQLiteColumn(name = "field7")
    private String field7;

    @StorIOSQLiteColumn(name = "field8")
    private byte[] field8;

    public boolean isField1() {
        return field1;
    }

    public void setField1(boolean field1) {
        this.field1 = field1;
    }

    public short getField2() {
        return field2;
    }

    public void setField2(short field2) {
        this.field2 = field2;
    }

    public int getField3() {
        return field3;
    }

    public void setField3(int field3) {
        this.field3 = field3;
    }

    public long getField4() {
        return field4;
    }

    public void setField4(long field4) {
        this.field4 = field4;
    }

    public float getField5() {
        return field5;
    }

    public void setField5(float field5) {
        this.field5 = field5;
    }

    public double getField6() {
        return field6;
    }

    public void setField6(double field6) {
        this.field6 = field6;
    }

    public String getField7() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7 = field7;
    }

    public byte[] getField8() {
        return field8;
    }

    public void setField8(byte[] field8) {
        this.field8 = field8;
    }
}