package com.pushtorefresh.storio.contentresolver.annotations.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentResolverProcessorStub {

    final TestStorIOContentResolverProcessor processor;
    final TypeElement classElement;
    final TestClassAnnotation classAnnotation;

    @NotNull
    static ContentResolverProcessorStub newInstance() {
        return new ContentResolverProcessorStub();
    }

    public ContentResolverProcessorStub() {
        processor = mock(TestStorIOContentResolverProcessor.class, Mockito.CALLS_REAL_METHODS);
        classAnnotation = mock(TestClassAnnotation.class);

        classElement = mock(TypeElement.class);
        Name qualifiedName = mock(Name.class);
        when(qualifiedName.toString()).thenReturn("ClassElementName");
        when(classElement.getQualifiedName()).thenReturn(qualifiedName);
    }

    protected static abstract class TestStorIOContentResolverProcessor extends StorIOContentResolverProcessor {

        // region Public Morozov
        @Override
        public void validateAnnotatedFieldOrMethod(@NotNull Element annotatedElement) {
            super.validateAnnotatedFieldOrMethod(annotatedElement);
        }

        public void validateUris(
                @NotNull TypeElement classElement,
                @Nullable String commonUri,
                @NotNull Map operationUriMap) {
            //noinspection unchecked
            super.validateUris(classElement, commonUri, operationUriMap);
        }
        // endregion

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
