package com.pushtorefresh.storio2.contentresolver.annotations;

import com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping;

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