package com.pushtorefresh.storio.sqlite.processor.introspection;

import org.junit.Test;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.BOOLEAN;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.BOOLEAN_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.DOUBLE;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.DOUBLE_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.FLOAT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.FLOAT_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.INTEGER;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.INTEGER_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.LONG;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.LONG_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.SHORT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.SHORT_OBJECT;
import static com.pushtorefresh.storio.sqlite.processor.introspection.JavaType.STRING;
import static org.junit.Assert.assertEquals;
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
        assertEquals(BOOLEAN, JavaType.from(typeMirror));
    }

    @Test
    public void fromBooleanObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Boolean");
        assertEquals(BOOLEAN_OBJECT, JavaType.from(typeMirror));
    }

    @Test
    public void fromShort() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.SHORT, null);
        assertEquals(SHORT, JavaType.from(typeMirror));
    }

    @Test
    public void fromShortObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Short");
        assertEquals(SHORT_OBJECT, JavaType.from(typeMirror));
    }

    @Test
    public void fromInteger() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.INT, null);
        assertEquals(INTEGER, JavaType.from(typeMirror));
    }

    @Test
    public void fromIntegerObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Integer");
        assertEquals(INTEGER_OBJECT, JavaType.from(typeMirror));
    }

    @Test
    public void fromLong() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.LONG, null);
        assertEquals(LONG, JavaType.from(typeMirror));
    }

    @Test
    public void fromLongObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Long");
        assertEquals(LONG_OBJECT, JavaType.from(typeMirror));
    }

    @Test
    public void fromFloat() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.FLOAT, null);
        assertEquals(FLOAT, JavaType.from(typeMirror));
    }

    @Test
    public void fromFloatObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Float");
        assertEquals(FLOAT_OBJECT, JavaType.from(typeMirror));
    }

    @Test
    public void fromDouble() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.DOUBLE, null);
        assertEquals(DOUBLE, JavaType.from(typeMirror));
    }

    @Test
    public void fromDoubleObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.Double");
        assertEquals(DOUBLE_OBJECT, JavaType.from(typeMirror));
    }

    @Test
    public void fromString() {
        final TypeMirror typeMirror = mockTypeMirror(null, "java.lang.String");
        assertEquals(STRING, JavaType.from(typeMirror));
    }
}
