package com.pushtorefresh.storio.internal;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pushtorefresh.storio.test.Asserts.assertThatListIsImmutable;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class InternalQueriesTest {

    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker
                .forClass(InternalQueries.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please")
                .check();
    }

    //region Tests for Queries.unmodifiableNonNullListOfStrings()

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = null;
        assertEquals(emptyList(), InternalQueries.unmodifiableNonNullListOfStrings(array));
    }

    @Test
    public void emptyArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = {};
        assertEquals(emptyList(), InternalQueries.unmodifiableNonNullListOfStrings(array));
    }

    @Test
    public void nonEmptyArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = {"1", "2", "3"};
        List<String> list = InternalQueries.unmodifiableNonNullListOfStrings(array);

        assertEquals(array.length, list.size());

        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], list.get(i));
        }
    }

    @Test
    public void nullItemInArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = {"1", null, "3"};
        List<String> strings = InternalQueries.unmodifiableNonNullListOfStrings(array);

        assertEquals("1", strings.get(0));
        assertEquals("null", strings.get(1));
        assertEquals("3", strings.get(2));
    }

    //endregion

    //region Tests for Queries.unmodifiableNonNullListOfStrings()

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListToUnmodifiableNonNullListOfStrings() {
        List<Object> list = null;
        assertEquals(emptyList(), InternalQueries.unmodifiableNonNullListOfStrings(list));
    }

    @Test
    public void emptyListToUnmodifiableNonNullListOfStrings() {
        List<Object> list = new ArrayList<Object>();
        assertEquals(emptyList(), InternalQueries.unmodifiableNonNullListOfStrings(list));
    }

    @Test
    public void nonEmptyListToUnmodifiableNonNullListOfStrings() {
        List<String> strings = asList("1", "2", "3");
        assertEquals(strings, InternalQueries.unmodifiableNonNullListOfStrings(strings));
    }

    @Test
    public void nonEmptyListWithNullElementToUnmodifiableNonNullListOfStrings() {
        List<String> strings = asList("1", null, "3");
        List<String> result = InternalQueries.unmodifiableNonNullListOfStrings(strings);

        assertEquals("1", result.get(0));
        assertEquals("null", result.get(1));
        assertEquals("3", result.get(2));
    }


    //endregion

    //region Tests for Queries.unmodifiableNonNullList()

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListToUnmodifiableNonNullList() {
        List<String> list = null;
        assertEquals(emptyList(), InternalQueries.unmodifiableNonNullList(list));
    }

    @Test
    public void emptyListToUnmodifiableNonNullList() {
        List<String> list = new ArrayList<String>();
        assertEquals(emptyList(), InternalQueries.unmodifiableNonNullList(list));
    }

    @Test
    public void nonEmptyListToUnmodifiableNonNullList() {
        List<String> list = asList("1", "2", "3");
        List<String> unmodifiableList = InternalQueries.unmodifiableNonNullList(list);

        assertEquals(list.size(), unmodifiableList.size());

        // don't believe equals from List :)
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), unmodifiableList.get(i));
        }
    }

    @Test
    public void listToUnmodifiableNonNullListIsReallyUnmodifiable() {
        List<String> unmodifiableList = InternalQueries.unmodifiableNonNullList(asList("1", "2", "3"));
        assertThatListIsImmutable(unmodifiableList);
    }

    //endregion

    //region Tests for Queries.unmodifiableNonNullSet()

    @Test
    public void nullSetToUnmodifiableNonNullSet() {
        assertEquals(emptySet(), InternalQueries.unmodifiableNonNullSet(null));
    }

    @Test
    public void emptySetToUnmodifiableNonNullSet() {
        assertEquals(emptySet(), InternalQueries.unmodifiableNonNullSet(new HashSet<Object>()));
    }

    @Test
    public void nonEmptySetToUnmodifiableNonNullSet() {
        Set<String> testSet = new HashSet<String>();

        testSet.add("1");
        testSet.add("2");
        testSet.add("3");

        Set<String> unmodifiableSet = InternalQueries.unmodifiableNonNullSet(testSet);

        assertEquals(testSet, unmodifiableSet);
    }

    //endregion

    //region Tests for Queries.nonNullArrayOfStrings()

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListOfStringsToNonNullArrayOfStrings() {
        List<String> list = null;
        assertTrue(Arrays.equals(new String[]{}, InternalQueries.nonNullArrayOfStrings(list)));
    }

    @Test
    public void emptyListOfStringsToNonNullArrayOfStrings() {
        List<String> list = new ArrayList<String>();
        assertTrue(Arrays.equals(new String[]{}, InternalQueries.nonNullArrayOfStrings(list)));
    }

    @Test
    public void nonEmptyListOfStringsToNonNullArrayOfStrings() {
        List<String> list = asList("1", "2", "3");
        String[] array = InternalQueries.nonNullArrayOfStrings(list);

        assertEquals(list.size(), array.length);

        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), array[i]);
        }
    }

    //endregion

    //region Tests for Queries.nullableArrayOfStrings()

    @Test
    public void nullListOfStringsToNullableArrayOfString() {
        assertEquals(null, InternalQueries.nullableArrayOfStrings(null));
    }

    @Test
    public void emptyListOfStringsToNullableArrayOfString() {
        List<String> list = new ArrayList<String>();
        assertEquals(null, InternalQueries.nullableArrayOfStrings(list));
    }

    @Test
    public void nonEmptyListOfStringsToNullableArrayOfStrings() {
        List<String> list = asList("1", "2", "3");
        String[] array = InternalQueries.nullableArrayOfStrings(list);

        assertNotNull(array);
        assertEquals(list.size(), array.length);

        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), array[i]);
        }
    }

    //endregion

    //region Tests for Queries.nonNullString()

    @Test
    public void nonNullStringFromNull() {
        assertEquals("", InternalQueries.nonNullString(null));
    }

    @Test
    public void nonNullStringFromEmptyString() {
        assertEquals("", InternalQueries.nonNullString(""));
    }

    @Test
    public void nonNullStringFromNormalString() {
        assertEquals("123", InternalQueries.nonNullString("123"));
    }

    //endregion

    //region Tests for Queries.nullableString()

    @Test
    public void nullableStringFromNull() {
        assertEquals(null, InternalQueries.nullableString(null));
    }

    @Test
    public void nullableStringFromEmptyString() {
        assertEquals(null, InternalQueries.nullableString(""));
    }

    @Test
    public void nullableStringFromNormalString() {
        assertEquals("123", InternalQueries.nullableString("123"));
    }

    //endregion
}
