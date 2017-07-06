package com.pushtorefresh.storio.contentresolver.annotations;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;

/**
 * Generated mapping with collection of resolvers.
 */
public class PrimitiveMethodsFactoryMethodContentResolverTypeMapping extends ContentResolverTypeMapping<PrimitiveMethodsFactoryMethod> {
    public PrimitiveMethodsFactoryMethodContentResolverTypeMapping() {
        super(new PrimitiveMethodsFactoryMethodStorIOContentResolverPutResolver(),
                new PrimitiveMethodsFactoryMethodStorIOContentResolverGetResolver(),
                new PrimitiveMethodsFactoryMethodStorIOContentResolverDeleteResolver());
    }
}