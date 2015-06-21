package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.squareup.javapoet.ClassName;

class Common {

    static final ClassName ANDROID_NON_NULL_ANNOTATION_CLASS_NAME = ClassName.get("android.support.annotation", "NonNull");

    static final String INDENT = "    "; // 4 spaces
}
