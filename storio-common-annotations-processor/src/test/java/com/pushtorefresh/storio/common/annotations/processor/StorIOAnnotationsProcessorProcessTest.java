package com.pushtorefresh.storio.common.annotations.processor;

import static javax.tools.Diagnostic.Kind.ERROR;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.squareup.javapoet.JavaFile;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JavaFile.class, JavaFileObject.class })
public class StorIOAnnotationsProcessorProcessTest {

	private StorIOAnnotationsProcessorDummy storioAnnotationsProcessor;
	private RoundEnvironment roundEnvironmentMock;
	private TypeElement typeElementMock;
	private ProcessingEnvironment processingEnvironment;
	private Set<Element> elementsAnnotatedWithStorIOType;

	@Before
	public void setUp() throws Exception {
		storioAnnotationsProcessor = new StorIOAnnotationsProcessorDummy();
		roundEnvironmentMock = mock(RoundEnvironment.class);
		typeElementMock = mock(TypeElement.class);
		processingEnvironment = mock(ProcessingEnvironment.class);
		elementsAnnotatedWithStorIOType = new HashSet<Element>(Arrays.asList(typeElementMock));
		doReturn(elementsAnnotatedWithStorIOType).when(roundEnvironmentMock).getElementsAnnotatedWith(Annotation.class);
	}

	@Test
	public final void processTrue() throws Exception {
		// given
		Elements elementUtilsMock = mock(Elements.class);
		when(processingEnvironment.getElementUtils()).thenReturn(elementUtilsMock);

		Filer filerMock = mock(Filer.class);
		when(processingEnvironment.getFiler()).thenReturn(filerMock);

		JavaFileObject javaFileObject = mock(JavaFileObject.class);
		when(filerMock.createSourceFile(anyString())).thenReturn(javaFileObject);

		Writer writerMock = mock(Writer.class);
		when(javaFileObject.openWriter()).thenReturn(writerMock);

		storioAnnotationsProcessor.init(processingEnvironment);

		// when
		boolean result = storioAnnotationsProcessor.process(null, roundEnvironmentMock);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public final void processExceptionExpected() throws Exception {
		// given
		Messager messager = mock(Messager.class);
		when(processingEnvironment.getMessager()).thenReturn(messager);

		when(processingEnvironment.getFiler()).thenReturn(null);

		storioAnnotationsProcessor.init(processingEnvironment);

		// when
		storioAnnotationsProcessor.process(null, roundEnvironmentMock);

		// then
		// Filer value set to null, filerSourceFile can't be generated and
		// problem occures.
		verify(messager).printMessage(ERROR, "Problem occurred with StorIOProcessor: null");
	}
}
