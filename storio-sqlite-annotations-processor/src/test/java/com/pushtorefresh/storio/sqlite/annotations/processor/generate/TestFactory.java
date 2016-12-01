package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestFactory {

    private TestFactory() {
        throw new IllegalStateException("No instances please");
    }

    @NotNull
    public static Element createElementMock(@NotNull TypeKind typeKind) {
        final Element objectElement = mock(Element.class);
        final TypeMirror typeMirror = mock(TypeMirror.class);
        when(objectElement.asType()).thenReturn(typeMirror);
        when(typeMirror.getKind()).thenReturn(typeKind);
        return objectElement;
    }

    @NotNull
    public static StorIOSQLiteColumnMeta createColumnMetaMock(
            @NotNull Element element,
            @NotNull String columnName,
            @NotNull String fieldName,
            boolean isKey,
            boolean ignoreNull,
            @Nullable JavaType javaType) {

        final StorIOSQLiteColumn storIOSQLiteColumn = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn.name()).thenReturn(columnName);
        when(storIOSQLiteColumn.key()).thenReturn(isKey);
        when(storIOSQLiteColumn.ignoreNull()).thenReturn(ignoreNull);

        //noinspection ConstantConditions
        return new StorIOSQLiteColumnMeta(
                null,
                element,
                fieldName,
                javaType,
                storIOSQLiteColumn
        );
    }
}
