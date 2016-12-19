package com.pushtorefresh.storio.common.annotations.processor.introspection;

import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;
import com.pushtorefresh.storio.common.annotations.processor.SkipNotAnnotatedClassWithAnnotatedParentException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static org.mockito.Mockito.when;

public class AnnotatedFieldValidationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void failIfEnclosingElementIsNotType() throws SkipNotAnnotatedClassWithAnnotatedParentException {
        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
        when(stub.enclosingElement.getKind()).thenReturn(METHOD);

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("Please apply TestFieldAnnotation to fields or methods of class: TestField");
        stub.processor.validateAnnotatedFieldOrMethod(stub.field);
    }

//    @Test
//    public void failIfThereIsNoAnnotation() {
//        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
//        when(stub.enclosingElement.getAnnotation(AnnotationProcessorStub.TestClassAnnotation.class)).thenReturn(null);
//
//        expectedException.expect(ProcessingException.class);
//        expectedException.expectMessage("Please annotate class TestClass with TestClassAnnotation");
//        stub.processor.validateAnnotatedFieldOrMethod(stub.field);
//    }

    @Test
    public void failIfFieldPrivate() throws SkipNotAnnotatedClassWithAnnotatedParentException {
        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
        Set<Modifier> modifiers = new HashSet<Modifier>();
        modifiers.add(Modifier.PRIVATE);
        when(stub.field.getModifiers()).thenReturn(modifiers);
        when(stub.field.getKind()).thenReturn(FIELD);

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("TestFieldAnnotation can not be applied to private field: TestField");
        stub.processor.validateAnnotatedFieldOrMethod(stub.field);
    }

    @Test
    public void failIfFieldFinal() throws SkipNotAnnotatedClassWithAnnotatedParentException {
        AnnotationProcessorStub stub = AnnotationProcessorStub.newInstance();
        Set<Modifier> modifiers = new HashSet<Modifier>();
        modifiers.add(Modifier.FINAL);
        when(stub.field.getModifiers()).thenReturn(modifiers);
        when(stub.field.getKind()).thenReturn(FIELD);

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("TestFieldAnnotation can not be applied to final field: TestField");
        stub.processor.validateAnnotatedFieldOrMethod(stub.field);
    }
}
