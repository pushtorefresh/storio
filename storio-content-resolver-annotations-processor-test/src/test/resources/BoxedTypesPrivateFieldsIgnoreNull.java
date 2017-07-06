package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class BoxedTypesPrivateFieldsIgnoreNull {

    @StorIOContentResolverColumn(name = "field1", ignoreNull = true)
    private Boolean field1;

    @StorIOContentResolverColumn(name = "field2", ignoreNull = true)
    private Short field2;

    @StorIOContentResolverColumn(name = "field3", ignoreNull = true)
    private Integer field3;

    @StorIOContentResolverColumn(name = "field4", key = true, ignoreNull = true)
    private Long field4;

    @StorIOContentResolverColumn(name = "field5", ignoreNull = true)
    private Float field5;

    @StorIOContentResolverColumn(name = "field6", ignoreNull = true)
    private Double field6;

    @StorIOContentResolverCreator
    public BoxedTypesPrivateFieldsIgnoreNull(Boolean field1, Short field2, Integer field3, Long field4, Float field5, Double field6) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
    }

    public Boolean getField1() {
        return field1;
    }

    public Short getField2() {
        return field2;
    }

    public Integer getField3() {
        return field3;
    }

    public Long getField4() {
        return field4;
    }

    public Float getField5() {
        return field5;
    }

    public Double getField6() {
        return field6;
    }
}