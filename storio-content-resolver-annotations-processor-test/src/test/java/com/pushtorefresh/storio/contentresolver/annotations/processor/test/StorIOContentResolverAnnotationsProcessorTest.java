package com.pushtorefresh.storio.contentresolver.annotations.processor.test;

import com.google.testing.compile.JavaFileObjects;
import com.pushtorefresh.storio.contentresolver.annotations.processor.StorIOContentResolverProcessor;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class StorIOContentResolverAnnotationsProcessorTest {

    @Test
    public void shouldNotCompileIfNestedClassAnnotatedWithTypeAnnotation() {
        JavaFileObject model = JavaFileObjects.forResource("NestedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverType can't be applied to nested or inner classes: ActualClass");
    }

    @Test
    public void shouldNotCompileIfInnerClassAnnotatedWithTypeAnnotation() {
        JavaFileObject model = JavaFileObjects.forResource("InnerClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverType can't be applied to nested or inner classes: ActualClass");
    }

    @Test
    public void shouldNotCompileIfNotClassAnnotatedWithTypeAnnotations() {
        JavaFileObject model = JavaFileObjects.forResource("AnnotatedInterface.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverType can be applied only to classes not to AnnotatedInterface");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldNotInsideClass() {
        JavaFileObject model = JavaFileObjects.forResource("AnnotatedFieldNotInsideClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Please apply StorIOContentResolverColumn only to members of class (fields or methods)" +
                        " - not to members of AnnotatedFieldNotInsideClass");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldInsideNotAnnotatedClass() {
        JavaFileObject model = JavaFileObjects.forResource("AnnotatedFieldInsideNotAnnotatedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Please annotate class AnnotatedFieldInsideNotAnnotatedClass with StorIOContentResolverType");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsPrivateAndDoesNotHaveAccessors() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithoutAccessors.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldNotCompileIfAnnotatedMethodIsPrivate() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateMethod.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsFinal() {
        JavaFileObject model = JavaFileObjects.forResource("FinalField.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverColumn can not be applied to final field: id");
    }

    @Test
    public void shouldCompileIfAnnotatedMethodIsFinal() {
        JavaFileObject model = JavaFileObjects.forResource("FinalMethod.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError();
    }

    @Test
    public void shouldNotCompileIfAnnotatedMethodHasParameters() {
        JavaFileObject model = JavaFileObjects.forResource("MethodWithParameters.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverColumn can not be applied to method with parameters: id");
    }

    @Test
    public void shouldNotCompileIfCreatorNotInsideClass() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorNotInsideClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Please apply StorIOContentResolverCreator to constructor or factory method of class - not to CreatorNotInsideClass");
    }

    @Test
    public void shouldNotCompileIfCreatorInsideNotAnnotatedClass() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorInsideNotAnnotatedClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Please annotate class CreatorInsideNotAnnotatedClass with StorIOContentResolverType");
    }

    @Test
    public void shouldNotCompileIfCreatorIsPrivate() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateCreator.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverCreator can not be applied to private methods or constructors: <init>");
    }

    @Test
    public void shouldNotCompileIfCreatorMethodIsNotStatic() {
        JavaFileObject model = JavaFileObjects.forResource("NonStaticCreatorMethod.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverCreator can not be applied to non-static methods: creator");
    }

    @Test
    public void shouldNotCompileIfCreatorMethodReturnsDifferentType() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorMethodWithDifferentReturnType.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverCreator can not be applied to method with return type different from CreatorMethodWithDifferentReturnType");
    }

    @Test
    public void shouldNotCompileWithoutAnyUris() {
        JavaFileObject model = JavaFileObjects.forResource("NoUris.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Uri of NoUris annotated with StorIOContentResolverType is empty");
    }

    @Test
    public void shouldNotCompileWithoutInsertUri() {
        JavaFileObject model = JavaFileObjects.forResource("NoInsertUri.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Uri of NoInsertUri annotated with StorIOContentResolverType is empty for operation insert");
    }

    @Test
    public void shouldNotCompileWithoutDeleteUri() {
        JavaFileObject model = JavaFileObjects.forResource("NoDeleteUri.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Uri of NoDeleteUri annotated with StorIOContentResolverType is empty for operation delete");
    }

    @Test
    public void shouldNotCompileWithoutUpdateUri() {
        JavaFileObject model = JavaFileObjects.forResource("NoUpdateUri.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Uri of NoUpdateUri annotated with StorIOContentResolverType is empty for operation update");
    }


    @Test
    public void shouldNotCompileIfThereIsAColumnWithSameName() {
        JavaFileObject model = JavaFileObjects.forResource("SameColumnName.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Column name already used in this class: id");
    }

    @Test
    public void shouldNotCompileIfClassContainsBothAnnotatedFieldsAndMethods() {
        JavaFileObject model = JavaFileObjects.forResource("MixedFieldsAndMethods.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Can't apply StorIOContentResolverColumn annotation to both fields and methods in a same class: MixedFieldsAndMethods");
    }

    @Test
    public void shouldNotCompileIfTypeIsUnsupported() {
        JavaFileObject model = JavaFileObjects
                .forResource("UnsupportedType.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Unsupported type of field or method for StorIOContentResolverColumn annotation, if you need to"
                        + " serialize/deserialize field of that type -> please write your own resolver");
    }

    @Test
    public void shouldNotCompileIfIgnoreNullIsUsedOnPrimitive() {
        JavaFileObject model = JavaFileObjects.forResource("IgnoreNullOnPrimitive.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("ignoreNull should not be used for primitive type: id");
    }

    @Test
    public void shouldNotCompileIfColumnNameIsEmpty() {
        JavaFileObject model = JavaFileObjects.forResource("EmptyColumnName.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Column name is empty: id");
    }

    @Test
    public void shouldNotCompileIfClassContainsMultipleCreators() {
        JavaFileObject model = JavaFileObjects.forResource("MultipleCreators.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Only one creator method or constructor is allowed: MultipleCreators");
    }

    @Test
    public void shouldNotCompileIfClassIsEmpty() {
        JavaFileObject model = JavaFileObjects.forResource("EmptyClass.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOContentResolverType annotation should have at least one field or method marked with "
                        + "StorIOContentResolverColumn annotation: EmptyClass");
    }

    @Test
    public void shouldNotCompileIfClassDoesNotHaveKey() {
        JavaFileObject model = JavaFileObjects.forResource("NoKey.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOContentResolverType annotation should have at least one KEY field or method marked"
                        + " with StorIOContentResolverColumn annotation: NoKey");
    }

    @Test
    public void shouldNotCompileIfClassNeedsCreatorAndDoesNotHaveOne() {
        JavaFileObject model = JavaFileObjects.forResource("NoCreator.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOContentResolverType annotation needs factory method or constructor marked with "
                        + "StorIOContentResolverCreator annotation: NoCreator");
    }

    @Test
    public void shouldNotCompileIfCreatorsNumberOfArgumentsDoNotMatchWithColumnsNumber() {
        JavaFileObject model = JavaFileObjects.forResource("CreatorWithWrongNumberOfArguments.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("Class marked with StorIOContentResolverType annotation needs factory method or constructor marked with StorIOContentResolverCreator"
                        + " annotation with the same amount of parameters as the number of columns: CreatorWithWrongNumberOfArguments");
    }

    @Test
    public void shouldNotCompileIfNoArgConstructorIsAbsent() {
        JavaFileObject model = JavaFileObjects.forResource("AbsenceOfNoArgConstructor.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("constructor AbsenceOfNoArgConstructor in class " +
                        "com.pushtorefresh.storio.contentresolver.annotations.AbsenceOfNoArgConstructor cannot be applied to given types;\n" +
                        "  required: long\n" +
                        "  found: no arguments\n" +
                        "  reason: actual and formal argument lists differ in length");
    }

    @Test
    public void shouldCompileWithPrimitiveFields() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitiveFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitiveFieldsContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitiveFieldsStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitiveFieldsStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitiveFieldsStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithBoxedTypesFields() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesFieldsContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesFieldsStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesFieldsStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesFieldsStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithBoxedTypesFieldsMarkedAsIgnoreNull() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesFieldsIgnoreNullStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningPrimitivesAndConstructorAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitiveMethodsConstructor.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitiveMethodsConstructorContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitiveMethodsConstructorStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitiveMethodsConstructorStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitiveMethodsConstructorStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndConstructorAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsConstructor.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsConstructorContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndMarkedAsIgnoreNullAndConstructorAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsConstructorIgnoreNullStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningPrimitivesAndFactoryMethodAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethod.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitiveMethodsFactoryMethodStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndFactoryMethodAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethod.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithMethodsReturningBoxedTypesAndMarkedAsIgnoreNullAndFactoryMethodAsCreator() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesMethodsFactoryMethodIgnoreNullStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsPrivateAndDoesNotHaveSetter() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithoutSetter.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldNotCompileIfAnnotatedFieldIsPrivateAndDoesNotHaveGetter() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithoutGetter.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .failsToCompile()
                .withErrorContaining("StorIOContentResolverColumn can not be applied to private field without corresponding getter and setter or private method: id");
    }

    @Test
    public void shouldCompileIfAnnotatedFieldIsPrivateAndHasIsGetter() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithIsGetter.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError();
    }

    @Test
    public void shouldCompileIfAnnotatedFieldIsPrivateAndHasNameStartingWithIs() {
        JavaFileObject model = JavaFileObjects.forResource("PrivateFieldWithNameStartingWithIs.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError();
    }

    @Test
    public void shouldCompileWithPrivatePrimitiveFieldsWithCorrepsondingAccessors() {
        JavaFileObject model = JavaFileObjects.forResource("PrimitivePrivateFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("PrimitivePrivateFieldsContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("PrimitivePrivateFieldsStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("PrimitivePrivateFieldsStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("PrimitivePrivateFieldsStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithPrivateBoxedTypesFieldsWithCorrespondingAccessors() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesPrivateFields.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesPrivateFieldsContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }

    @Test
    public void shouldCompileWithPrivateBoxedTypesFieldsWithCorresondingAccessorsAndMarkedAsIgnoreNull() {
        JavaFileObject model = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNull.java");

        JavaFileObject generatedTypeMapping = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullContentResolverTypeMapping.java");
        JavaFileObject generatedDeleteResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullStorIOContentResolverDeleteResolver.java");
        JavaFileObject generatedGetResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullStorIOContentResolverGetResolver.java");
        JavaFileObject generatedPutResolver = JavaFileObjects.forResource("BoxedTypesPrivateFieldsIgnoreNullStorIOContentResolverPutResolver.java");

        assert_().about(javaSource())
                .that(model)
                .processedWith(new StorIOContentResolverProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedTypeMapping, generatedDeleteResolver, generatedGetResolver, generatedPutResolver);
    }
}