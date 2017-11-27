package com.pushtorefresh.storio3.contentresolver.annotations;

import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;

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