package com.pushtorefresh.storio.common.annotations.processor.introspection;

import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StorIOTypeMetaTest {
	
	private Annotation annotationMock;
	
	@Before
	public void setUp() throws Exception {
		annotationMock = mock(Annotation.class);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void constructor() {
		// when
		StorIOTypeMeta storioTypeMeta = new StorIOTypeMeta("TEST", "TEST", annotationMock, true);
		
		// then
		assertThat("TEST").as("Constructor must be set simpleName field.").isEqualTo(storioTypeMeta.simpleName);
		assertThat("TEST").as("Constructor must be set packageName field.").isEqualTo(storioTypeMeta.packageName);
		assertThat(annotationMock).as("Constructor must be set storIOType field.").isEqualTo(storioTypeMeta.storIOType);
		assertThat(true).as("Constructor must be set needCreator field.").isEqualTo(storioTypeMeta.needCreator);
	}

	@Test
	public final void equalsAndHashCode() {
		EqualsVerifier.forClass(StorIOTypeMeta.class).suppress(Warning.REFERENCE_EQUALITY).usingGetClass().verify();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void toStringValitadion() {
		// given
		StorIOTypeMeta storioTypeMeta = new StorIOTypeMeta("TEST", "TEST", annotationMock, true);
		String expectedString = "StorIOTypeMeta{" +
                "simpleName='TEST" + '\'' +
                ", packageName='TEST" + '\'' +
                ", storIOType=" + annotationMock +
				", needCreator=true" +
				", creator=null" +
                ", columns=" + storioTypeMeta.columns +
                '}';
		
		// when
		String toString = storioTypeMeta.toString();

		// then
		assertThat(expectedString).as("toString method should be equal with expectedString.").isEqualTo(toString);
	}

}
