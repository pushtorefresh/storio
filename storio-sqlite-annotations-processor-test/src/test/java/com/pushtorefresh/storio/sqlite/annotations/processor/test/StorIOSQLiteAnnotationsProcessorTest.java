package com.pushtorefresh.storio.sqlite.annotations.processor.test;

import com.google.testing.compile.JavaFileObjects;
import com.pushtorefresh.storio.sqlite.annotations.processor.StorIOSQLiteProcessor;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class StorIOSQLiteAnnotationsProcessorTest {

    @Test
    public void shouldNotCompileIfPrivateClassAnnotatedWithTypeAnnotation() {
        JavaFileObject model = JavaFileObjects
                .forResource("PrivateClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteType can not be applied to private class: ActualClass");
    }

    @Test
    public void shouldNotCompileIfNotClassAnnotatedWithTypeAnnotations() {
        JavaFileObject model = JavaFileObjects
                .forResource("AnnotatedInterface.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteType can be applied only to classes not to AnnotatedInterface");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldNotInsideClass() {
        JavaFileObject model = JavaFileObjects
                .forResource("AnnotatedFieldNotInsideClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please apply StorIOSQLiteColumn only to members of class (fields or methods)" +
                        " - not to members of AnnotatedFieldNotInsideClass");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldInsideNotAnnotatedClass() {
        JavaFileObject model = JavaFileObjects
                .forResource("AnnotatedFieldInsideNotAnnotatedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please annotate class AnnotatedFieldInsideNotAnnotatedClass with StorIOSQLiteType");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsPrivate() {
        JavaFileObject model = JavaFileObjects
                .forResource("PrivateField.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to private field or method: id");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsFinal() {
        JavaFileObject model = JavaFileObjects
                .forResource("FinalField.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to final field: id");
    }

    @Test
    public void shouldCompileIfAnnotatedMethodIsFinal() {
        JavaFileObject model = JavaFileObjects
                .forResource("FinalMethod.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError();
    }

    @Test
    public void shouldNotCompileIfAnnotatedMethodHasParameters() {
        JavaFileObject model = JavaFileObjects
                .forResource("MethodWithParameters.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to method with parameters: id");
    }

    @Test
    public void shouldNotCompileIfCreatorNotInsideClass() {
        JavaFileObject model = JavaFileObjects
                .forResource("CreatorNotInsideClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please apply StorIOSQLiteCreator to constructor or factory method of class - not to CreatorNotInsideClass");
    }

    @Test
    public void shouldNotCompileIfCreatorInsideNotAnnotatedClass() {
        JavaFileObject model = JavaFileObjects
                .forResource("CreatorInsideNotAnnotatedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please annotate class CreatorInsideNotAnnotatedClass with StorIOSQLiteType");
    }

    @Test
    public void shouldNotCompileIfCreatorIsPrivate() {
        JavaFileObject model = JavaFileObjects
                .forResource("PrivateCreator.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteCreator can not be applied to private methods or constructors: <init>");
    }

    @Test
    public void shouldNotCompileIfCreatorMethodIsNotStatic() {
        JavaFileObject model = JavaFileObjects
                .forResource("NonStaticCreatorMethod.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteCreator can not be applied to non-static methods: creator");
    }

    @Test
    public void shouldNotCompileIfCreatorMethodReturnsDifferentType() {
        JavaFileObject model = JavaFileObjects
                .forResource("CreatorMethodWithDifferentReturnType.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteCreator can not be applied to method with return type different " +
                        "from CreatorMethodWithDifferentReturnType");
    }

    @Test
    public void shouldNotCompileIfTableIsEmpty() {
        JavaFileObject model = JavaFileObjects
                .forResource("EmptyTable.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Table name of EmptyTable annotated with StorIOSQLiteType is empty");
    }

    @Test
    public void shouldNotCompileIfThereIsAColumnWithSameName() {
        JavaFileObject model = JavaFileObjects
                .forResource("SameColumnName.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Column name already used in this class: id");
    }

    @Test
    public void shouldNotCompileIfClassContainsBothAnnotatedFieldsAndMethods() {
        JavaFileObject model = JavaFileObjects
                .forResource("MixedFieldsAndMethods.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Can't apply StorIOSQLiteColumn annotation to both fields and methods in a same class: MixedFieldsAndMethods");
    }

    @Test
    public void shouldNotCompileIfTypeIsUnsupported() {
        JavaFileObject model = JavaFileObjects
                .forResource("UnsupportedType.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Unsupported type of field or method for StorIOSQLiteColumn annotation," +
                        " if you need to serialize/deserialize field of that type -> please write your own resolver");
    }

    @Test
    public void shouldNotCompileIfIgnoreNullIsUsedOnPrimitive() {
        JavaFileObject model = JavaFileObjects
                .forResource("IgnoreNullOnPrimitive.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("ignoreNull should not be used for primitive type: id");
    }

    @Test
    public void shouldNotCompileIfColumnNameIsEmpty() {
        JavaFileObject model = JavaFileObjects
                .forResource("EmptyColumnName.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Column name is empty: id");
    }

    @Test
    public void shouldNotCompileIfClassContainsMultipleCreators() {
        JavaFileObject model = JavaFileObjects
                .forResource("MultipleCreators.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Only one creator method or constructor is allowed: MultipleCreators");
    }

    @Test
    public void shouldNotCompileIfClassIsEmpty() {
        JavaFileObject model = JavaFileObjects
                .forResource("EmptyClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation should have at least one field or method marked with " +
                        "StorIOSQLiteColumn annotation: EmptyClass");
    }

    @Test
    public void shouldNotCompileIfClassDoesNotHaveKey() {
        JavaFileObject model = JavaFileObjects
                .forResource("NoKey.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation should have at least one KEY field or method marked with " +
                        "StorIOSQLiteColumn annotation: NoKey");
    }

    @Test
    public void shouldNotCompileIfClassNeedsCreatorAndDoesNotHaveOne() {
        JavaFileObject model = JavaFileObjects
                .forResource("NoCreator.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation needs factory method or constructor marked with " +
                        "StorIOSQLiteCreator annotation: NoCreator");
    }

    @Test
    public void shouldNotCompileIfCreatorsNumberOfArgumentsDoNotMatchWithColumnsNumber() {
        JavaFileObject model = JavaFileObjects
                .forResource("CreatorWithWrongNumberOfArguments.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation needs factory method or constructor marked with " +
                        "StorIOSQLiteCreator annotation with the same amount of parameters as the number of columns: " +
                        "CreatorWithWrongNumberOfArguments");
    }

}