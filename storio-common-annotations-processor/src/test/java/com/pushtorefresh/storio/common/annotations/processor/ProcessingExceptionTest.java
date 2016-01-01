package com.pushtorefresh.storio.common.annotations.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.reflect.Whitebox.getInternalState;

import javax.lang.model.element.Element;

import org.junit.Before;
import org.junit.Test;

public class ProcessingExceptionTest {

	private Element elementMock;

	@Before
	public void setUp() throws Exception {
		elementMock = mock(Element.class);
	}

	@Test
	public final void processingException() {
		// when
		ProcessingException processingException = new ProcessingException(elementMock, "TEST");

		// then
		assertThat("TEST").as("Constructor must be set detailMessage field.").isEqualTo(getInternalState(processingException, "detailMessage"));
		assertThat(elementMock).as("Constructor must be set element field.").isEqualTo(getInternalState(processingException, "element"));
	}

	@Test
	public final void element() {
		// when
		ProcessingException processingException = new ProcessingException(elementMock, "TEST");

		// then
		assertThat(elementMock).as("Constructor must be set element field.").isEqualTo(processingException.element());
	}

}
