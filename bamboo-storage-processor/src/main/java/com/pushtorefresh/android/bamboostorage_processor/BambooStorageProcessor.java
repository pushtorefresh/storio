package com.pushtorefresh.android.bamboostorage_processor;

import com.google.auto.service.AutoService;
import com.pushtorefresh.android.bamboostorage.annotation.StorableType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class BambooStorageProcessor extends AbstractProcessor {

    private Types types;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        types = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(StorableType.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<StorableTypeModel> storableTypeModels = parseStorableTypes(roundEnv);

            for (StorableTypeModel storableTypeModel : storableTypeModels) {
                generateParserForStorableType(storableTypeModel);
                generateSerializerForStorableType(storableTypeModel);
            }
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "BambooStorage annotation processor problem: " + e.getMessage());
        }

        return false;
    }

    private Set<StorableTypeModel> parseStorableTypes(RoundEnvironment roundEnv) throws Exception {
        Set<? extends Element> elementsAnnotatedWithStorableType
                = roundEnv.getElementsAnnotatedWith(StorableType.class);

        Set<StorableTypeModel> storableTypeModels
                = new HashSet<StorableTypeModel>(elementsAnnotatedWithStorableType.size());

        for (Element element : elementsAnnotatedWithStorableType) {
            validateClassModifiers(element);

            //storableTypeModels.add(new StorableTypeModel(element.getSimpleName(), element.))
        }

        return storableTypeModels;
    }

    private void validateClassModifiers(Element element) throws BadClassException {
        Set<Modifier> modifiers = element.getModifiers();

        if (modifiers.contains(Modifier.PRIVATE)) {
            throw new BadClassException("class annotated with " + StorableType.class.getCanonicalName() + " can not be private!");
        }
    }

    private void generateParserForStorableType(StorableTypeModel storableTypeModel) {

    }

    private void generateSerializerForStorableType(StorableTypeModel storableTypeModel) {

    }
}
