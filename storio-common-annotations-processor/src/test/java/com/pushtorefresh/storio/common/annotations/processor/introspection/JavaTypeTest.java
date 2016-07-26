package com.pushtorefresh.storio.common.annotations.processor.introspection;

import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BYTE_ARRAY;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JavaTypeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @NotNull
    private static TypeMirror mockTypeMirror(@Nullable TypeKind typeKind, @Nullable String typeName) {
        final TypeMirror typeMirror = mock(TypeMirror.class);

        when(typeMirror.getKind())
                .thenReturn(typeKind);

        when(typeMirror.toString())
                .thenReturn(typeName);

        return typeMirror;
    }
    
    @Test
    public void fromIllegalArgumentException() {
    	// given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Unsupported type: null");
        final TypeMirror typeMirror = mock(TypeMirror.class);
        when(typeMirror.getKind()).thenReturn(null);
        when(typeMirror.toString()).thenReturn(null);

        // when
        JavaType.from(typeMirror);
        
        // then
        fail("IllegalArgumentException expected.");
    }
    
    @Test
    public void valueOf() {
        // when
        JavaType javaType = JavaType.valueOf("BOOLEAN");
        
        // then
        assertThat(javaType.toString()).isEqualTo("BOOLEAN");
    }

    @Test
    public void fromBoolean() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.BOOLEAN, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(BOOLEAN);
    }

    @Test
    public void fromBooleanObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, Boolean.class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(BOOLEAN_OBJECT);
    }

    @Test
    public void fromShort() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.SHORT, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(SHORT);
    }

    @Test
    public void fromShortObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, Short.class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(SHORT_OBJECT);
    }

    @Test
    public void fromInteger() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.INT, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(INTEGER);
    }

    @Test
    public void fromIntegerObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, Integer.class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(INTEGER_OBJECT);
    }

    @Test
    public void fromLong() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.LONG, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(LONG);
    }

    @Test
    public void fromLongObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, Long.class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(LONG_OBJECT);
    }

    @Test
    public void fromFloat() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.FLOAT, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(FLOAT);
    }

    @Test
    public void fromFloatObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, Float.class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(FLOAT_OBJECT);
    }

    @Test
    public void fromDouble() {
        final TypeMirror typeMirror = mockTypeMirror(TypeKind.DOUBLE, null);
        assertThat(JavaType.from(typeMirror)).isEqualTo(DOUBLE);
    }

    @Test
    public void fromDoubleObject() {
        final TypeMirror typeMirror = mockTypeMirror(null, Double.class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(DOUBLE_OBJECT);
    }

    @Test
    public void fromString() {
        final TypeMirror typeMirror = mockTypeMirror(null, String.class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(STRING);
    }

    @Test
    public void fromByteArray() {
        final TypeMirror typeMirror = mockTypeMirror(null, byte[].class.getCanonicalName());
        assertThat(JavaType.from(typeMirror)).isEqualTo(BYTE_ARRAY);
    }

    @Test
    public void booleanObjectIsBoxed() {
        assertThat(BOOLEAN_OBJECT.isBoxedType()).isTrue();
    }

    @Test
    public void shortObjectIsBoxed() {
        assertThat(SHORT_OBJECT.isBoxedType()).isTrue();
    }

    @Test
    public void integerObjectIsBoxed() {
        assertThat(INTEGER_OBJECT.isBoxedType()).isTrue();
    }

    @Test
    public void longObjectIsBoxed() {
        assertThat(LONG_OBJECT.isBoxedType()).isTrue();
    }

    @Test
    public void floatObjectIsBoxed() {
        assertThat(FLOAT_OBJECT.isBoxedType()).isTrue();
    }

    @Test
    public void doubleObjectIsBoxed() {
        assertThat(DOUBLE_OBJECT.isBoxedType()).isTrue();
    }

    @Test
    public void shortPrimitiveIsNotBoxed() {
        assertThat(SHORT.isBoxedType()).isFalse();
    }

    @Test
    public void integerPrimitiveIsNotBoxed() {
        assertThat(INTEGER.isBoxedType()).isFalse();
    }

    @Test
    public void longPrimitiveIsNotBoxed() {
        assertThat(LONG.isBoxedType()).isFalse();
    }

    @Test
    public void floatPrimitiveIsNotBoxed() {
        assertThat(FLOAT.isBoxedType()).isFalse();
    }

    @Test
    public void doublePrimitiveIsNotBoxed() {
        assertThat(DOUBLE.isBoxedType()).isFalse();
    }

    @Test
    public void stringIsNotBoxed() {
        assertThat(STRING.isBoxedType()).isFalse();
    }

    @Test
    public void byteArrayIsNotBoxed() {
        assertThat(BYTE_ARRAY.isBoxedType()).isFalse();
    }
}
