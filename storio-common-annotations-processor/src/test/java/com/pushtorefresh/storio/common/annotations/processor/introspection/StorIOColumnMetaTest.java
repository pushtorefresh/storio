package com.pushtorefresh.storio.common.annotations.processor.introspection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.reflect.Whitebox.getInternalState;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class StorIOColumnMetaTest {

	private Annotation annotationMock;
	private Element elementMock;
	private JavaType javaType;

	@Before
	public void setUp() throws Exception {
		annotationMock = mock(Annotation.class);
		elementMock = mock(Element.class);
		javaType = JavaType.BOOLEAN;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void constructor() {
		// when
		StorIOColumnMeta storioColumnMeta = new StorIOColumnMeta(elementMock, elementMock, "TEST", javaType,
				annotationMock);

		// then
		assertThat(elementMock).as("Constructor must be set enclosingElement field.")
				.isEqualTo(getInternalState(storioColumnMeta, "enclosingElement"));
		assertThat(elementMock).as("Constructor must be set element field.")
				.isEqualTo(getInternalState(storioColumnMeta, "element"));
		assertThat("TEST").as("Constructor must be set fieldName field.")
				.isEqualTo(getInternalState(storioColumnMeta, "fieldName"));
		assertThat(javaType).as("Constructor must be set javaType field.")
				.isEqualTo(getInternalState(storioColumnMeta, "javaType"));
		assertThat(annotationMock).as("Constructor must be set storIOColumn field.")
				.isEqualTo(getInternalState(storioColumnMeta, "storIOColumn"));
	}

	@Test
	public final void equalsAndHashCode() {
		EqualsVerifier.forClass(StorIOColumnMeta.class).suppress(Warning.REFERENCE_EQUALITY).usingGetClass().verify();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void toStringValidation() {
		// given
		StorIOColumnMeta storioColumnMeta = new StorIOColumnMeta(elementMock, elementMock, "TEST", javaType,
				annotationMock);
		String expectedString = "StorIOColumnMeta{enclosingElement=" + elementMock + ", element=" + elementMock
				+ ", fieldName='TEST" + '\'' + ", javaType=" + javaType + ", storIOColumn=" + annotationMock + '}';

		// when
		String toString = storioColumnMeta.toString();

		// then
		assertThat(expectedString).as("toString method should be equal with expectedString.").isEqualTo(toString);
	}

}
