package com.pushtorefresh.storio.contentresolver.annotations;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class PrimitiveFieldsContentResolverTypeMapping extends ContentResolverTypeMapping<PrimitiveFields> {
    public PrimitiveFieldsContentResolverTypeMapping() {
        super(new PrimitiveFieldsStorIOContentResolverPutResolver(),
                new PrimitiveFieldsStorIOContentResolverGetResolver(),
                new PrimitiveFieldsStorIOContentResolverDeleteResolver());
    }
}