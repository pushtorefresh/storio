package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class BoxedTypesMethodsConstructorIgnoreNull {

    private Boolean field1;

    private Short field2;

    private Integer field3;

    private Long field4;

    private Float field5;

    private Double field6;

    @StorIOContentResolverCreator
    public BoxedTypesMethodsConstructorIgnoreNull(Boolean field1, Short field2, Integer field3, Long field4, Float field5,
                                                  Double field6) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
    }

    @StorIOContentResolverColumn(name = "field1", ignoreNull = true)
    public Boolean getField1() {
        return field1;
    }

    @StorIOContentResolverColumn(name = "field2", ignoreNull = true)
    public Short getField2() {
        return field2;
    }

    @StorIOContentResolverColumn(name = "field3", ignoreNull = true)
    public Integer getField3() {
        return field3;
    }

    @StorIOContentResolverColumn(name = "field4", key = true, ignoreNull = true)
    public Long getField4() {
        return field4;
    }

    @StorIOContentResolverColumn(name = "field5", ignoreNull = true)
    public Float getField5() {
        return field5;
    }

    @StorIOContentResolverColumn(name = "field6", ignoreNull = true)
    public Double getField6() {
        return field6;
    }
}