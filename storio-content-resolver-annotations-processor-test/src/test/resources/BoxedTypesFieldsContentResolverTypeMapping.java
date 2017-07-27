package com.pushtorefresh.storio2.contentresolver.annotations;

import com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class BoxedTypesFieldsContentResolverTypeMapping extends ContentResolverTypeMapping<BoxedTypesFields> {
    public BoxedTypesFieldsContentResolverTypeMapping() {
        super(new BoxedTypesFieldsStorIOContentResolverPutResolver(),
                new BoxedTypesFieldsStorIOContentResolverGetResolver(),
                new BoxedTypesFieldsStorIOContentResolverDeleteResolver());
    }
}