package com.pushtorefresh.storio.sqlite.annotations.processor.test;

import com.google.testing.compile.JavaFileObjects;
import com.pushtorefresh.storio.sqlite.annotations.processor.StorIOSQLiteProcessor;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class StorIOSQLiteAnnotationsProcessorTest {

    @Test
    public void shouldNotCompileIfNestedClassAnnotatedWithTypeAnnotation() {
        JavaFileObject model = JavaFileObjects.forResource("NestedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteType can't be applied to nested or inner classes: ActualClass");
    }

    @Test
    public void shouldNotCompileIfInnerClassAnnotatedWithTypeAnnotation() {
        JavaFileObject model = JavaFileObjects.forResource("InnerClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteType can't be applied to nested or inner classes: ActualClass");
    }

    @Test
    public void shouldNotCompileIfNotClassAnnotatedWithTypeAnnotations() {
        JavaFileObject model = JavaFileObjects.forResource("AnnotatedInterface.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteType can be applied only to classes not to AnnotatedInterface");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldNotInsideClass() {
        JavaFileObject model = JavaFileObjects.forResource("AnnotatedFieldNotInsideClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please apply StorIOSQLiteColumn only to members of class (fields or methods) - not to members of"
                        + " AnnotatedFieldNotInsideClass");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldInsideNotAnnotatedClass() {
        JavaFileObject model = JavaFileObjects.forResource(
                "AnnotatedFieldInsideNotAnnotatedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please annotate class AnnotatedFieldInsideNotAnnotatedClass with StorIOSQLiteType");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsPrivateAndDoesNotHaveAccessors() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithoutAccessors.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldNotCompileIfAnnotatedMethodIsPrivate() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateMethod.java");

        assert_().about(javaSource())
            .that(model)
            .processedWith(new StorIOSQLiteProcessor())
            .failsToCompile()
            .withErrorContaining("StorIOSQLiteColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsFinal() {
        JavaFileObject model = JavaFileObjects.forResource("FinalField.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to final field: id");
    }

    @Test
    public void shouldCompileIfAnnotatedMethodIsFinal() {
        JavaFileObject model = JavaFileObjects.forResource("FinalMethod.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError();
    }

    @Test
    public void shouldNotCompileIfAnnotatedMethodHasParameters() {
        JavaFileObject model = JavaFileObjects.forResource("MethodWithParameters.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to method with parameters: id");
    }

    @Test
    public void shouldNotCompileIfCreatorNotInsideClass() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorNotInsideClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please apply StorIOSQLiteCreator to constructor or factory method of class - not to CreatorNotInsideClass");
    }

    @Test
    public void shouldNotCompileIfCreatorInsideNotAnnotatedClass() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorInsideNotAnnotatedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Please annotate class CreatorInsideNotAnnotatedClass with StorIOSQLiteType");
    }

    @Test
    public void shouldNotCompileIfCreatorIsPrivate() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateCreator.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteCreator can not be applied to private methods or constructors: <init>");
    }

    @Test
    public void shouldNotCompileIfCreatorMethodIsNotStatic() {
        JavaFileObject model = JavaFileObjects.forResource("NonStaticCreatorMethod.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteCreator can not be applied to non-static methods: creator");
    }

    @Test
    public void shouldNotCompileIfCreatorMethodReturnsDifferentType() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorMethodWithDifferentReturnType.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteCreator can not be applied to method with return type different from"
                        + " CreatorMethodWithDifferentReturnType");
    }

    @Test
    public void shouldNotCompileIfTableIsEmpty() {
        JavaFileObject model = JavaFileObjects.forResource("EmptyTable.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Table name of EmptyTable annotated with StorIOSQLiteType is empty");
    }

    @Test
    public void shouldNotCompileIfThereIsAColumnWithSameName() {
        JavaFileObject model = JavaFileObjects.forResource("SameColumnName.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Column name already used in this class: id");
    }

    @Test
    public void shouldNotCompileIfClassContainsBothAnnotatedFieldsAndMethods() {
        JavaFileObject model = JavaFileObjects.forResource("MixedFieldsAndMethods.java");

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
                .withErrorContaining("Unsupported type of field or method for StorIOSQLiteColumn annotation, if you need to serialize/deserialize"
                        + " field of that type -> please write your own resolver");
    }

    @Test
    public void shouldNotCompileIfIgnoreNullIsUsedOnPrimitive() {
        JavaFileObject model = JavaFileObjects.forResource("IgnoreNullOnPrimitive.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("ignoreNull should not be used for primitive type: id");
    }

    @Test
    public void shouldNotCompileIfColumnNameIsEmpty() {
        JavaFileObject model = JavaFileObjects.forResource("EmptyColumnName.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Column name is empty: id");
    }

    @Test
    public void shouldNotCompileIfClassContainsMultipleCreators() {
        JavaFileObject model = JavaFileObjects.forResource("MultipleCreators.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Only one creator method or constructor is allowed: MultipleCreators");
    }

    @Test
    public void shouldNotCompileIfClassIsEmpty() {
        JavaFileObject model = JavaFileObjects.forResource("EmptyClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation should have at least one field or method marked with"
                        + " StorIOSQLiteColumn annotation: EmptyClass");
    }

    @Test
    public void shouldNotCompileIfClassDoesNotHaveKey() {
        JavaFileObject model = JavaFileObjects.forResource("NoKey.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation should have at least one KEY field or method marked with"
                        + " StorIOSQLiteColumn annotation: NoKey");
    }

    @Test
    public void shouldNotCompileIfClassNeedsCreatorAndDoesNotHaveOne() {
        JavaFileObject model = JavaFileObjects.forResource("NoCreator.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation needs factory method or constructor marked with"
                        + " StorIOSQLiteCreator annotation: NoCreator");
    }

    @Test
    public void shouldNotCompileIfCreatorsNumberOfArgumentsDoNotMatchWithColumnsNumber() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorWithWrongNumberOfArguments.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOSQLiteType annotation needs factory method or constructor marked with"
                        + " StorIOSQLiteCreator annotation with the same amount of parameters as the number of columns: CreatorWithWrongNumberOfArguments");
    }

    @Test
    public void shouldNotCompileIfNoArgConstructorIsAbsent() {
        JavaFileObject model = JavaFileObjects.forResource("AbsenceOfNoArgConstructor.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("constructor AbsenceOfNoArgConstructor in class com.pushtorefresh.storio.sqlite.annotations.AbsenceOfNoArgConstructor"
                        + " cannot be applied to given types;\n"
                        + "  required: long\n"
                        + "  found: no arguments\n"
                        + "  reason: actual and formal argument lists differ in length");
    }

    @Test
    public void shouldCompileWithPrimitiveFields() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitiveFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitiveFieldsSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitiveFieldsStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitiveFieldsStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitiveFieldsStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithBoxedTypesFields() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesFieldsSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesFieldsStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesFieldsStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesFieldsStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithBoxedTypesFieldsMarkedAsIgnoreNull() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningPrimitivesAndConstructorAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitiveMethodsConstructor.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitiveMethodsConstructorSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitiveMethodsConstructorStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitiveMethodsConstructorStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitiveMethodsConstructorStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndConstructorAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsConstructor.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsConstructorSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndMarkedAsIgnoreNullAndConstructorAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningPrimitivesAndFactoryMethodAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethod.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndFactoryMethodAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethod.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndMarkedAsIgnoreNullAndFactoryMethodAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsPrivateAndDoesNotHaveSetter() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithoutSetter.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsPrivateAndDoesNotHaveGetter() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithoutGetter.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOSQLiteColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldCompileIfAnnotatedFieldIsPrivateAndHasIsGetter() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithIsGetter.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError();
    }

    @Test
    public void shouldCompileIfAnnotatedFieldIsPrivateAndHasNameStartingWithIs() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithNameStartingWithIs.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError();
    }

    @Test
    public void shouldCompileWithPrivatePrimitiveFieldsWithCorrespondingAccessors() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitivePrivateFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitivePrivateFieldsSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitivePrivateFieldsStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitivePrivateFieldsStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitivePrivateFieldsStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithPrivateBoxedTypesFieldsWithCorrespondingAccessors() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesPrivateFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesPrivateFieldsSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithPrivateBoxedTypesFieldsWithCorrespondingAccessorsAndMarkedAsIgnoreNull() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullSQLiteTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullStorIOSQLiteDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullStorIOSQLiteGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullStorIOSQLitePutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOSQLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }
}
