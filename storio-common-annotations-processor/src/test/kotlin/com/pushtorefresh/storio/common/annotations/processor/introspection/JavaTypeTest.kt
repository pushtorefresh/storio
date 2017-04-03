package com.pushtorefresh.storio.common.annotations.processor.introspection

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

class JavaTypeTest {

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    private fun mockTypeMirror(typeKind: TypeKind?, typeName: String?): TypeMirror {
        val typeMirror = mock<TypeMirror>()

        whenever(typeMirror.kind).thenReturn(typeKind)
        whenever(typeMirror.toString()).thenReturn(typeName)

        return typeMirror
    }

    @Test
    fun fromIllegalArgumentException() {
        // given
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported type: null")
        val typeMirror = mock<TypeMirror>()
        whenever(typeMirror.kind).thenReturn(null)
        whenever(typeMirror.toString()).thenReturn(null)

        // when
        JavaType.from(typeMirror)

        // then
        fail("IllegalArgumentException expected.")
    }

    @Test
    fun valueOf() {
        // when
        val javaType = JavaType.valueOf("BOOLEAN")

        // then
        assertThat(javaType.toString()).isEqualTo("BOOLEAN")
    }

    @Test
    fun fromBoolean() {
        val typeMirror = mockTypeMirror(TypeKind.BOOLEAN, null)
        assertThat(JavaType.from(typeMirror)).isEqualTo(BOOLEAN)
    }

    @Test
    fun fromBooleanObject() {
        val typeMirror = mockTypeMirror(null, Boolean::class.javaObjectType.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(BOOLEAN_OBJECT)
    }

    @Test
    fun fromShort() {
        val typeMirror = mockTypeMirror(TypeKind.SHORT, null)
        assertThat(JavaType.from(typeMirror)).isEqualTo(SHORT)
    }

    @Test
    fun fromShortObject() {
        val typeMirror = mockTypeMirror(null, Short::class.javaObjectType.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(SHORT_OBJECT)
    }

    @Test
    fun fromInteger() {
        val typeMirror = mockTypeMirror(TypeKind.INT, null)
        assertThat(JavaType.from(typeMirror)).isEqualTo(INTEGER)
    }

    @Test
    fun fromIntegerObject() {
        val typeMirror = mockTypeMirror(null, Integer::class.javaObjectType.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(INTEGER_OBJECT)
    }

    @Test
    fun fromLong() {
        val typeMirror = mockTypeMirror(TypeKind.LONG, null)
        assertThat(JavaType.from(typeMirror)).isEqualTo(LONG)
    }

    @Test
    fun fromLongObject() {
        val typeMirror = mockTypeMirror(null, Long::class.javaObjectType.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(LONG_OBJECT)
    }

    @Test
    fun fromFloat() {
        val typeMirror = mockTypeMirror(TypeKind.FLOAT, null)
        assertThat(JavaType.from(typeMirror)).isEqualTo(FLOAT)
    }

    @Test
    fun fromFloatObject() {
        val typeMirror = mockTypeMirror(null, Float::class.javaObjectType.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(FLOAT_OBJECT)
    }

    @Test
    fun fromDouble() {
        val typeMirror = mockTypeMirror(TypeKind.DOUBLE, null)
        assertThat(JavaType.from(typeMirror)).isEqualTo(DOUBLE)
    }

    @Test
    fun fromDoubleObject() {
        val typeMirror = mockTypeMirror(null, Double::class.javaObjectType.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(DOUBLE_OBJECT)
    }

    @Test
    fun fromString() {
        val typeMirror = mockTypeMirror(null, String::class.javaObjectType.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(STRING)
    }

    @Test
    fun fromByteArray() {
        val typeMirror = mockTypeMirror(null, ByteArray::class.java.canonicalName)
        assertThat(JavaType.from(typeMirror)).isEqualTo(BYTE_ARRAY)
    }

    @Test
    fun booleanObjectIsBoxed() {
        assertThat(BOOLEAN_OBJECT.isBoxedType).isTrue()
    }

    @Test
    fun shortObjectIsBoxed() {
        assertThat(SHORT_OBJECT.isBoxedType).isTrue()
    }

    @Test
    fun integerObjectIsBoxed() {
        assertThat(INTEGER_OBJECT.isBoxedType).isTrue()
    }

    @Test
    fun longObjectIsBoxed() {
        assertThat(LONG_OBJECT.isBoxedType).isTrue()
    }

    @Test
    fun floatObjectIsBoxed() {
        assertThat(FLOAT_OBJECT.isBoxedType).isTrue()
    }

    @Test
    fun doubleObjectIsBoxed() {
        assertThat(DOUBLE_OBJECT.isBoxedType).isTrue()
    }

    @Test
    fun shortPrimitiveIsNotBoxed() {
        assertThat(SHORT.isBoxedType).isFalse()
    }

    @Test
    fun integerPrimitiveIsNotBoxed() {
        assertThat(INTEGER.isBoxedType).isFalse()
    }

    @Test
    fun longPrimitiveIsNotBoxed() {
        assertThat(LONG.isBoxedType).isFalse()
    }

    @Test
    fun floatPrimitiveIsNotBoxed() {
        assertThat(FLOAT.isBoxedType).isFalse()
    }

    @Test
    fun doublePrimitiveIsNotBoxed() {
        assertThat(DOUBLE.isBoxedType).isFalse()
    }

    @Test
    fun stringIsNotBoxed() {
        assertThat(STRING.isBoxedType).isFalse()
    }

    @Test
    fun byteArrayIsNotBoxed() {
        assertThat(BYTE_ARRAY.isBoxedType).isFalse()
    }
}