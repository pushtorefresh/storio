package com.pushtorefresh.storio.contentresolver.annotations;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;

/**
 * Generated mapping with collection of resolvers
 */
public class PrimitivePrivateFieldsContentResolverTypeMapping extends ContentResolverTypeMapping<PrimitivePrivateFields> {
    public PrimitivePrivateFieldsContentResolverTypeMapping() {
        super(new PrimitivePrivateFieldsStorIOContentResolverPutResolver(),
                new PrimitivePrivateFieldsStorIOContentResolverGetResolver(),
                new PrimitivePrivateFieldsStorIOContentResolverDeleteResolver());
    }
}
