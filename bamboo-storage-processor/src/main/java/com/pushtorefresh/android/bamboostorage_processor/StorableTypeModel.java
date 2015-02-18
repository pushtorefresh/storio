package com.pushtorefresh.android.bamboostorage_processor;

import javax.lang.model.element.Element;

public class StorableTypeModel {

    private final String elementName;
    private final String packageName;
    private final boolean isPublic;
    private final Element element;

    public StorableTypeModel(String elementName, String packageName, boolean isPublic, Element element) {
        this.elementName = elementName;
        this.packageName = packageName;
        this.isPublic = isPublic;
        this.element = element;
    }
}
