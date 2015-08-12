package com.pushtorefresh.storio.sqlite.annotations.processor.introspection;

import org.junit.Test;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.BOOLEAN;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.BOOLEAN_OBJECT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.DOUBLE;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.DOUBLE_OBJECT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.FLOAT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.FLOAT_OBJECT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.INTEGER;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.INTEGER_OBJECT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.LONG;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.LONG_OBJECT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.SHORT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.SHORT_OBJECT;
import static com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType.STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JavaTypeTest {

    private static TypeMirror mockTypeMirror(TypeKind typeKind, String typeName) {
        final TypeMirror typeMirror = mock(TypeMirror.class);

        when(typeMirror.getKind())
                .thenReturn(typeKind);

        when(typeMirror.toString())
                .thenReturn(typeName);

        return typeMirror;
    }

    @Test
    public void fromBoolean() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.BOOLEAN, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(BOOLEAN);
    }

    @Test
    public void fromBooleanObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Boolean");
        assertThat(JavaType.from(typeMirror)).isEqualTo(BOOLEAN_OBJECT);
    }

    @Test
    public void fromShort() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.SHORT, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(SHORT);
    }

    @Test
    public void fromShortObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Short");
        assertThat(JavaType.from(typeMirror)).isEqualTo(SHORT_OBJECT);
    }

    @Test
    public void fromInteger() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.INT, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(INTEGER);
    }

    @Test
    public void fromIntegerObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Integer");
        assertThat(JavaType.from(typeMirror)).isEqualTo(INTEGER_OBJECT);
    }

    @Test
    public void fromLong() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.LONG, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(LONG);
    }

    @Test
    public void fromLongObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Long");
        assertThat(JavaType.from(typeMirror)).isEqualTo(LONG_OBJECT);
    }

    @Test
    public void fromFloat() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.FLOAT, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(FLOAT);
    }

    @Test
    public void fromFloatObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Float");
        assertThat(JavaType.from(typeMirror)).isEqualTo(FLOAT_OBJECT);
    }

    @Test
    public void fromDouble() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.DOUBLE, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(DOUBLE);
    }

    @Test
    public void fromDoubleObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Double");
        assertThat(JavaType.from(typeMirror)).isEqualTo(DOUBLE_OBJECT);
    }

    @Test
    public void fromString() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.String");
        assertThat(JavaType.from(typeMirror)).isEqualTo(STRING);
    }
}
