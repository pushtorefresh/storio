package com.pushtorefresh.bamboostorage;

import android.support.annotation.NonNull;

/**
 * Container for type meta and extra info
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
class BambooStorableTypeMetaWithExtra {

    /**
     * Type meta info
     */
    public final BambooStorableTypeMeta typeMeta;

    /**
     * Once time computed value for queries with where by internal id
     */
    public final String whereById;

    BambooStorableTypeMetaWithExtra(@NonNull BambooStorableTypeMeta typeMeta) {
        this.typeMeta = typeMeta;
        whereById = typeMeta.internalIdFieldName() + " = ?";
    }
}
