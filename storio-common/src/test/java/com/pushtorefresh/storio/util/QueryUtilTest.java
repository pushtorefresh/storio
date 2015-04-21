package com.pushtorefresh.storio.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class QueryUtilTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullVarargsToList() {
        Object[] varargs = null;
        assertEquals(null, QueryUtil.varargsToList(varargs));
    }

    @Test
    public void emptyVarargsToList() {
        Object[] varargs = {};
        assertEquals(null, QueryUtil.varargsToList(varargs));
    }

    @Test
    public void nonEmptyVarargsToList() {
        Object[] varargs = {"1", "2", "3"};
        List<String> list = QueryUtil.varargsToList(varargs);

        assertNotNull(list);
        assertEquals(varargs.length, list.size());

        for (int i = 0; i < varargs.length; i++) {
            assertEquals(varargs[i], list.get(i));
        }
    }

    @Test
    public void nullItemVarargsToList() {
        Object[] varargs = {"1", null, "3"};
        List<String> strings = QueryUtil.varargsToList(varargs);
        assertNotNull(strings);
        assertEquals("1", strings.get(0));
        assertEquals("null", strings.get(1));
        assertEquals("3", strings.get(2));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListToUnmodifiable() {
        List<String> list = null;
        assertEquals(null, QueryUtil.listToUnmodifiable(list));
    }

    @Test
    public void emptyListToUnmodifiable() {
        List<String> list = new ArrayList<String>();
        assertEquals(null, QueryUtil.listToUnmodifiable(list));
    }

    @Test
    public void nonEmptyListToUnmodifiable() {
        List<String> list = Arrays.asList("1", "2", "3");
        List<String> unmodifiableList = QueryUtil.listToUnmodifiable(list);

        assertNotNull(unmodifiableList);
        assertEquals(list.size(), unmodifiableList.size());

        // don't believe equals from List :)
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), unmodifiableList.get(i));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void listToUnmodifiableIsReallyUnmodifiable() {
        List<String> unmodifiableList = QueryUtil.listToUnmodifiable(Arrays.asList("1", "2", "3"));

        // UnmodifiableCollection is private class :(
        String className = unmodifiableList.getClass().getSimpleName();
        assertTrue(className.equals("UnmodifiableRandomAccessList") || className.equals("UnmodifiableList"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListOfStringsToArray() {
        List<String> list = null;
        assertEquals(null, QueryUtil.listToArray(list));
    }

    @Test
    public void emptyListOfStringsToArray() {
        List<String> list = new ArrayList<String>();
        assertEquals(null, QueryUtil.listToArray(list));
    }

    @Test
    public void nonEmptyListOfStringsToArray() {
        List<String> list = Arrays.asList("1", "2", "3");
        String[] array = QueryUtil.listToArray(list);

        assertNotNull(array);
        assertEquals(list.size(), array.length);

        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), array[i]);
        }
    }
}
