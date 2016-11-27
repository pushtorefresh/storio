package com.pushtorefresh.storio.common.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.StorIOAnnotationsProcessor;

import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;

import static javax.lang.model.element.ElementKind.CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnnotationProcessorStub {

    final TestStorIOAnnotationsProcessor processor;
    final Element field;
    final Element enclosingElement;
    final TestClassAnnotation classAnnotation;
    final TestFieldAnnotation fieldAnnotation;

    @NotNull
    static AnnotationProcessorStub newInstance() {
        return new AnnotationProcessorStub();
    }

    public AnnotationProcessorStub() {
        processor = mock(TestStorIOAnnotationsProcessor.class, Mockito.CALLS_REAL_METHODS);

        field = mock(Element.class);

        Name name = mock(Name.class);
        when(name.toString()).thenReturn("TestField");
        when(field.getSimpleName()).thenReturn(name);

        enclosingElement = mock(Element.class);
        Name className = mock(Name.class);
        when(className.toString()).thenReturn("TestClass");
        when(enclosingElement.getSimpleName()).thenReturn(className);
        when(enclosingElement.getKind()).thenReturn(CLASS);

        when(field.getEnclosingElement()).thenReturn(enclosingElement);

        classAnnotation = mock(TestClassAnnotation.class);
        fieldAnnotation = mock(TestFieldAnnotation.class);

        when(enclosingElement.getAnnotation(TestClassAnnotation.class)).thenReturn(classAnnotation);
    }

    protected static abstract class TestStorIOAnnotationsProcessor extends StorIOAnnotationsProcessor {

        @Override
        public void validateAnnotatedField(@NotNull Element annotatedField, boolean hasConstructor) {
            super.validateAnnotatedField(annotatedField, hasConstructor);
        }

        @NotNull
        @Override
        public Class<? extends Annotation> getTypeAnnotationClass() {
            return TestClassAnnotation.class;
        }

        @NotNull
        @Override
        protected Class<? extends Annotation> getColumnAnnotationClass() {
            return TestFieldAnnotation.class;
        }
    }

    static abstract class TestClassAnnotation implements Annotation {
    }

    static abstract class TestFieldAnnotation implements Annotation {
    }
}
