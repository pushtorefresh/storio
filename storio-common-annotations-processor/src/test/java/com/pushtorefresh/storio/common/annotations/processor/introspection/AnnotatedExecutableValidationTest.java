package com.pushtorefresh.storio.common.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

import static javax.lang.model.element.ElementKind.METHOD;
import static org.mockito.Mockito.when;

public class AnnotatedExecutableValidationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void failIfEnclosingElementIsNotClass() {
        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
        when(stub.enclosingElement.getKind()).thenReturn(METHOD);

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("Please apply TestCreatorAnnotation to constructor or factory method of class: TestClass");
        stub.processor.validateAnnotatedExecutable(stub.creator);
    }

    @Test
    public void failIfThereIsNoAnnotation() {
        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
        when(stub.enclosingElement.getAnnotation(AnnotationProcessorStub.TestClassAnnotation.class)).thenReturn(null);

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("Please annotate class TestClass with TestClassAnnotation");
        stub.processor.validateAnnotatedExecutable(stub.creator);
    }

    @Test
    public void failIfExecutablePrivate() {
        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
        Set<Modifier> modifiers = new HashSet<Modifier>();
        modifiers.add(Modifier.PRIVATE);
        when(stub.creator.getModifiers()).thenReturn(modifiers);

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("TestCreatorAnnotation can not be applied to private methods or constructors");
        stub.processor.validateAnnotatedExecutable(stub.creator);
    }

    @Test
    public void failIfFactoryMethodNotStatic() {
        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
        Set<Modifier> modifiers = new HashSet<Modifier>();
        when(stub.creator.getModifiers()).thenReturn(modifiers);
        when(stub.creator.getKind()).thenReturn(METHOD);

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("TestCreatorAnnotation can not be applied to non-static methods");
        stub.processor.validateAnnotatedExecutable(stub.creator);
    }
}
