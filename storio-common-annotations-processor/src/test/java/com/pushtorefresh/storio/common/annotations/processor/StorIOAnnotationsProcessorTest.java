package com.pushtorefresh.storio.common.annotations.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.reflect.Whitebox.getInternalState;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.squareup.javapoet.JavaFile;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JavaFile.class, JavaFileObject.class })
public class StorIOAnnotationsProcessorTest {

	private StorIOAnnotationsProcessorDummy storioAnnotationsProcessor;
	private ProcessingEnvironment processingEnvironment;

	@Before
	public void setUp() throws Exception {
		storioAnnotationsProcessor = new StorIOAnnotationsProcessorDummy();
		processingEnvironment = mock(ProcessingEnvironment.class);
	}

	@Test
	public final void initProcessingEnvironment() {
		// when
		storioAnnotationsProcessor.init(processingEnvironment);

		// then
		assertThat(processingEnvironment.getFiler()).as("init must be set filer field.").isEqualTo(getInternalState(storioAnnotationsProcessor, "filer"));
		assertThat(processingEnvironment.getElementUtils()).as("init must be set elementUtils field.").isEqualTo(getInternalState(storioAnnotationsProcessor, "elementUtils"));
		assertThat(processingEnvironment.getMessager()).as("init must be set messager field.").isEqualTo(getInternalState(storioAnnotationsProcessor, "messager"));
	}

	@Test
	public final void getSupportedSourceVersion() {
		// given
		SourceVersion result = storioAnnotationsProcessor.getSupportedSourceVersion();

		// then
		assertThat(SourceVersion.latestSupported()).as("Function must return same result with SourceVersion.latestSupported() function.").isEqualTo(result);
	}
}
