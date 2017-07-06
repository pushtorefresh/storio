package com.pushtorefresh.storio.common.annotations.processor.introspection

import com.nhaarman.mockito_kotlin.mock
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import javax.lang.model.element.Element

class StorIOColumnMetaTest {

    private lateinit var annotationMock: Annotation
    private lateinit var elementMock: Element
    private lateinit var javaType: JavaType

    @Before
    fun setUp() {
        annotationMock = mock()
        elementMock = mock()
        javaType = JavaType.BOOLEAN
    }

    @Test
    fun constructor() {
        // when
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "TEST", javaType, annotationMock)

        // then
        assertThat(columnMeta.enclosingElement).isEqualTo(elementMock)
        assertThat(columnMeta.element).isEqualTo(elementMock)
        assertThat(columnMeta.elementName).isEqualTo("TEST")
        assertThat(columnMeta.javaType).isEqualTo(javaType)
        assertThat(columnMeta.storIOColumn).isEqualTo(annotationMock)
    }

    @Test
    fun equalsAndHashCode() {
        EqualsVerifier.forClass(StorIOColumnMeta::class.java)
                .suppress(Warning.REFERENCE_EQUALITY)
                .usingGetClass()
                .verify()
    }

    @Test
    fun toStringValidation() {
        // given
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "TEST", javaType, annotationMock, "getter", "setter")
        val expectedString = "StorIOColumnMeta(enclosingElement=$elementMock, element=$elementMock, elementName='TEST', javaType=$javaType, storIOColumn=$annotationMock, getter='getter', setter='setter')"

        // when
        val toString = columnMeta.toString()

        // then
        assertThat(expectedString).isEqualTo(toString)
    }

    @Test
    fun shouldReturnRealElementNameForElementWithoutPrefixes() {
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "property", javaType, annotationMock)

        val realName = columnMeta.realElementName

        assertThat(realName).isEqualTo("property")
    }

    @Test
    fun shouldReturnRealElementNameForElementWithGetPrefix() {
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "getProperty", javaType, annotationMock)

        val realName = columnMeta.realElementName

        assertThat(realName).isEqualTo("property")
    }

    @Test
    fun shouldReturnRealElementNameForElementWithIsPrefix() {
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "isProperty", javaType, annotationMock)

        val realName = columnMeta.realElementName

        assertThat(realName).isEqualTo("property")
    }

    @Test
    fun shouldReturnRealElementNameForElementWithOneCharacterName() {
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "a", javaType, annotationMock)

        val realName = columnMeta.realElementName

        assertThat(realName).isEqualTo("a")
    }

    @Test
    fun shouldReturnRealElementNameForElementStartsWithGet() {
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "getter", javaType, annotationMock)

        val realName = columnMeta.realElementName

        assertThat(realName).isEqualTo("getter")
    }

    @Test
    fun shouldReturnRealElementNameForElementStartsWithIs() {
        val columnMeta = StorIOColumnMeta(elementMock, elementMock, "iso", javaType,
                annotationMock)

        val realName = columnMeta.realElementName

        assertThat(realName).isEqualTo("iso")
    }

}
