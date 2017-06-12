package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class BoxedTypesPrivateFieldsIgnoreNull {

    @StorIOSQLiteColumn(name = "field1", ignoreNull = true)
    private Boolean field1;

    @StorIOSQLiteColumn(name = "field2", ignoreNull = true)
    private Short field2;

    @StorIOSQLiteColumn(name = "field3", ignoreNull = true)
    private Integer field3;

    @StorIOSQLiteColumn(name = "field4", key = true, ignoreNull = true)
    private Long field4;

    @StorIOSQLiteColumn(name = "field5", ignoreNull = true)
    private Float field5;

    @StorIOSQLiteColumn(name = "field6", ignoreNull = true)
    private Double field6;

    public Boolean getField1() {
        return field1;
    }

    public void setField1(Boolean field1) {
        this.field1 = field1;
    }

    public Short getField2() {
        return field2;
    }

    public void setField2(Short field2) {
        this.field2 = field2;
    }

    public Integer getField3() {
        return field3;
    }

    public void setField3(Integer field3) {
        this.field3 = field3;
    }

    public Long getField4() {
        return field4;
    }

    public void setField4(Long field4) {
        this.field4 = field4;
    }

    public Float getField5() {
        return field5;
    }

    public void setField5(Float field5) {
        this.field5 = field5;
    }

    public Double getField6() {
        return field6;
    }

    public void setField6(Double field6) {
        this.field6 = field6;
    }
}