package com.pushtorefresh.storio.contentresolver.annotations.processor;

import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UriValidationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void successIfCommonNameExist() {
        ContentResolverProcessorStub stub = ContentResolverProcessorStub.newInstance();
        stub.processor.validateUris(stub.classElement, "content://testUri", Collections.emptyMap());
    }

    @Test
    public void successIfNamesForOperationsExist() {
        ContentResolverProcessorStub stub = ContentResolverProcessorStub.newInstance();
        stub.processor.validateUris(stub.classElement, "", Collections.singletonMap("operation", "content://testUri"));
    }

    @Test
    public void failIfNameForOperationIsEmpty() {
        ContentResolverProcessorStub stub = ContentResolverProcessorStub.newInstance();

        Map<String, String> operationNameMap = new HashMap<String, String>(3);
        operationNameMap.put("insert", "content://insertUri");
        operationNameMap.put("update", "");
        operationNameMap.put("delete", "content://deleteUri");

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("Uri of ClassElementName annotated " +
                "with TestClassAnnotation is null or empty for operation update");

        stub.processor.validateUris(stub.classElement, "", operationNameMap);
    }

    @Test
    public void failIfAllNamesAreEmpty() {
        ContentResolverProcessorStub stub = ContentResolverProcessorStub.newInstance();

        Map<String, String> operationNameMap = new HashMap<String, String>(3);
        operationNameMap.put("insert", "");
        operationNameMap.put("update", "");
        operationNameMap.put("delete", "");

        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage("Uri of ClassElementName annotated " +
                "with TestClassAnnotation is null or empty"); // Without operation marker

        stub.processor.validateUris(stub.classElement, "", operationNameMap);
    }
}
