package com.pushtorefresh.storio.common.annotations.processor.introspection

import com.nhaarman.mockito_kotlin.mock
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import javax.lang.model.element.Element

class StorIOTypeMetaTest {

    private lateinit var annotationMock: Annotation

    @Before
    fun setUp() {
        annotationMock = mock()
    }

    @Test
    fun constructor() {
        // when
        val typeMeta = StorIOTestTypeMeta("TEST", "TEST", annotationMock, true)

        // then
        assertThat(typeMeta.simpleName).isEqualTo("TEST")
        assertThat(typeMeta.packageName).isEqualTo("TEST")
        assertThat(typeMeta.storIOType).isEqualTo(annotationMock)
        assertThat(typeMeta.needCreator).isEqualTo(true)
    }

    @Test
    fun equalsAndHashCode() {
        EqualsVerifier.forClass(StorIOTypeMeta::class.java)
                .suppress(Warning.REFERENCE_EQUALITY)
                .usingGetClass()
                .verify()
    }

    @Test
    fun toStringValitadion() {
        // given
        val typeMeta = StorIOTestTypeMeta("TEST", "TEST", annotationMock, true)
        val expectedString = "StorIOTypeMeta(simpleName='TEST', packageName='TEST'," +
                " storIOType=$annotationMock, needCreator=true, creator=null," +
                " columns=${typeMeta.columns})"

        // when
        val toString = typeMeta.toString()

        // then
        assertThat(expectedString).isEqualTo(toString)
    }

}

class StorIOTestColumnMeta(enclosingElement: Element,
                           element: Element,
                           elementName: String,
                           javaType: JavaType,
                           storIOColumn: Annotation)
    : StorIOColumnMeta<Annotation>(
        enclosingElement,
        element,
        elementName,
        javaType,
        storIOColumn)

class StorIOTestTypeMeta(simpleName: String,
                         packageName: String,
                         storIOType: Annotation,
                         needCreator: Boolean)
    : StorIOTypeMeta<Annotation, StorIOTestColumnMeta>(
        simpleName,
        packageName,
        storIOType,
        needCreator)