package com.pushtorefresh.storio.contentresolver.annotations.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;

// AutoService doesn't work with Kotlin classes for some reason
@AutoService(Processor.class)
public class StorIOContentResolverDummyProcessor extends StorIOContentResolverProcessor {
}