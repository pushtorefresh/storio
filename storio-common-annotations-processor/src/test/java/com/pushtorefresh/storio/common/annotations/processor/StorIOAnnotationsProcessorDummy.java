package com.pushtorefresh.storio.common.annotations.processor;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOColumnMeta;
import com.pushtorefresh.storio.common.annotations.processor.introspection.StorIOTypeMeta;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@SuppressWarnings("rawtypes")
public final class StorIOAnnotationsProcessorDummy
		extends StorIOAnnotationsProcessor<StorIOTypeMeta, StorIOColumnMeta> {

	@Override
	protected StorIOTypeMeta processAnnotatedClass(TypeElement classElement, Elements elementUtils) {
		return null;
	}

	@Override
	protected void processAnnotatedFieldsOrMethods(RoundEnvironment roundEnvironment, Map<TypeElement, StorIOTypeMeta> annotatedClasses) {
	}

	@Override
	protected StorIOColumnMeta processAnnotatedFieldOrMethod(Element annotatedField) {
		return null;
	}

	@Override
	protected void processAnnotatedExecutables(@NotNull RoundEnvironment roundEnvironment, @NotNull Map<TypeElement, StorIOTypeMeta> annotatedClasses) {
	}

	@Override
	protected void validateAnnotatedClassesAndColumns(Map<TypeElement, StorIOTypeMeta> annotatedClasses) {
	}

	@Override
	protected Class<? extends Annotation> getTypeAnnotationClass() {
		return Annotation.class;
	}

	@Override
	protected Class<? extends Annotation> getColumnAnnotationClass() {
		return null;
	}

	@Override
	protected Class<? extends Annotation> getCreatorAnnotationClass() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Generator<StorIOTypeMeta> createPutResolver() {
		Generator resolver = new Generator<StorIOTypeMeta>() {

			@Override
			public JavaFile generateJavaFile(StorIOTypeMeta storIOContentResolverTypeMeta) {
				final TypeSpec putResolver = TypeSpec.classBuilder("TEST").build();

				return JavaFile.builder("TEST", putResolver).build();
			}
		};
		return resolver;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Generator<StorIOTypeMeta> createGetResolver() {
		Generator resolver = new Generator<StorIOTypeMeta>() {

			@Override
			public JavaFile generateJavaFile(StorIOTypeMeta storIOContentResolverTypeMeta) {
				final TypeSpec getResolver = TypeSpec.classBuilder("TEST").build();

				return JavaFile.builder("TEST", getResolver).build();
			}
		};
		return resolver;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Generator<StorIOTypeMeta> createDeleteResolver() {
		Generator resolver = new Generator<StorIOTypeMeta>() {

			@Override
			public JavaFile generateJavaFile(StorIOTypeMeta storIOContentResolverTypeMeta) {
				final TypeSpec deleteResolver = TypeSpec.classBuilder("TEST").build();

				return JavaFile.builder("TEST", deleteResolver).build();
			}
		};
		return resolver;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Generator<StorIOTypeMeta> createMapping() {
		Generator mapping = new Generator<StorIOTypeMeta>() {

			@Override
			public JavaFile generateJavaFile(StorIOTypeMeta storIOContentResolverTypeMeta) {
				final TypeSpec mappingResolver = TypeSpec.classBuilder("TEST").build();

				return JavaFile.builder("TEST", mappingResolver).build();
			}
		};
		return mapping;
	}

}
