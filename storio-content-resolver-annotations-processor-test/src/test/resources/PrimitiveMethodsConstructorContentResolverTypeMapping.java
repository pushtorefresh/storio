package com.pushtorefresh.storio2.contentresolver.annotations;

import com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class PrimitiveMethodsConstructorContentResolverTypeMapping extends ContentResolverTypeMapping<PrimitiveMethodsConstructor> {
    public PrimitiveMethodsConstructorContentResolverTypeMapping() {
        super(new PrimitiveMethodsConstructorStorIOContentResolverPutResolver(),
                new PrimitiveMethodsConstructorStorIOContentResolverGetResolver(),
                new PrimitiveMethodsConstructorStorIOContentResolverDeleteResolver());
    }
}