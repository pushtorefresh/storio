package com.pushtorefresh.storio3.contentresolver.annotations;

import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;

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