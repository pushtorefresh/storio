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
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(array)).isEqualTo(emptyList());
    }

    @Test
    public void emptyArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = {};
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(array)).isEqualTo(emptyList());
    }

    @Test
    public void nonEmptyArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = {"1", "2", "3"};
        List<String> list = InternalQueries.unmodifiableNonNullListOfStrings(array);

        assertThat(list).hasSize(array.length);

        for (int i = 0; i < array.length; i++) {
            assertThat(list.get(i)).isEqualTo(array[i]);
        }
    }

    @Test
    public void nullItemInArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = {"1", null, "3"};
        List<String> strings = InternalQueries.unmodifiableNonNullListOfStrings(array);

        assertThat(strings.get(0)).isEqualTo("1");
        assertThat(strings.get(1)).isEqualTo("null");
        assertThat(strings.get(2)).isEqualTo("3");
    }

    //endregion

    //region Tests for Queries.unmodifiableNonNullListOfStrings()

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListToUnmodifiableNonNullListOfStrings() {
        List<Object> list = null;
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(list)).isEqualTo(emptyList());
    }

    @Test
    public void emptyListToUnmodifiableNonNullListOfStrings() {
        List<Object> list = new ArrayList<Object>();
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(list)).isEqualTo(emptyList());
    }

    @Test
    public void nonEmptyListToUnmodifiableNonNullListOfStrings() {
        List<String> strings = asList("1", "2", "3");
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(strings)).isEqualTo(strings);
    }

    @Test
    public void nonEmptyListWithNullElementToUnmodifiableNonNullListOfStrings() {
        List<String> strings = asList("1", null, "3");
        List<String> result = InternalQueries.unmodifiableNonNullListOfStrings(strings);

        assertThat(result.get(0)).isEqualTo("1");
        assertThat(result.get(1)).isEqualTo("null");
        assertThat(result.get(2)).isEqualTo("3");
    }

    //endregion

    //region Tests for Queries.unmodifiableNonNullList()

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListToUnmodifiableNonNullList() {
        List<String> list = null;
        assertThat(InternalQueries.unmodifiableNonNullList(list)).isEqualTo(emptyList());
    }

    @Test
    public void emptyListToUnmodifiableNonNullList() {
        List<String> list = new ArrayList<String>();
        assertThat(InternalQueries.unmodifiableNonNullList(list)).isEqualTo(emptyList());
    }

    @Test
    public void nonEmptyListToUnmodifiableNonNullList() {
        List<String> list = asList("1", "2", "3");
        List<String> unmodifiableList = InternalQueries.unmodifiableNonNullList(list);

        assertThat(unmodifiableList).hasSize(list.size());

        // don't believe equals from List :)
        for (int i = 0; i < list.size(); i++) {
            assertThat(list.get(i)).isEqualTo(unmodifiableList.get(i));
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
        assertThat(InternalQueries.unmodifiableNonNullSet(null)).isEqualTo(emptySet());
    }

    @Test
    public void emptySetToUnmodifiableNonNullSet() {
        assertThat(InternalQueries.unmodifiableNonNullSet(new HashSet<Object>())).isEqualTo(emptySet());
    }

    @Test
    public void nonEmptySetToUnmodifiableNonNullSet() {
        Set<String> testSet = new HashSet<String>();

        testSet.add("1");
        testSet.add("2");
        testSet.add("3");

        Set<String> unmodifiableSet = InternalQueries.unmodifiableNonNullSet(testSet);

        assertThat(unmodifiableSet).isEqualTo(testSet);
    }

    //endregion

    //region Tests for Queries.nonNullArrayOfStrings()

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListOfStringsToNonNullArrayOfStrings() {
        List<String> list = null;
        assertThat(Arrays.equals(new String[]{}, InternalQueries.nonNullArrayOfStrings(list))).isTrue();
    }

    @Test
    public void emptyListOfStringsToNonNullArrayOfStrings() {
        List<String> list = new ArrayList<String>();
        assertThat(Arrays.equals(new String[]{}, InternalQueries.nonNullArrayOfStrings(list))).isTrue();
    }

    @Test
    public void nonEmptyListOfStringsToNonNullArrayOfStrings() {
        List<String> list = asList("1", "2", "3");
        String[] array = InternalQueries.nonNullArrayOfStrings(list);

        assertThat(array.length).isEqualTo(list.size());

        for (int i = 0; i < list.size(); i++) {
            assertThat(array[i]).isEqualTo(list.get(i));
        }
    }

    //endregion

    //region Tests for Queries.nullableArrayOfStrings()

    @Test
    public void nullListOfStringsToNullableArrayOfString() {
        assertThat(InternalQueries.nullableArrayOfStrings(null)).isNull();
    }

    @Test
    public void emptyListOfStringsToNullableArrayOfString() {
        List<String> list = new ArrayList<String>();
        assertThat(InternalQueries.nullableArrayOfStrings(list)).isNull();
    }

    @Test
    public void nonEmptyListOfStringsToNullableArrayOfStrings() {
        List<String> list = asList("1", "2", "3");
        String[] array = InternalQueries.nullableArrayOfStrings(list);

        assertThat(array).isNotNull();

        //noinspection ConstantConditions
        assertThat(array.length).isEqualTo(list.size());

        for (int i = 0; i < list.size(); i++) {
            assertThat(array[i]).isEqualTo(list.get(i));
        }
    }

    //endregion

    //region Tests for Queries.nonNullString()

    @Test
    public void nonNullStringFromNull() {
        assertThat(InternalQueries.nonNullString(null)).isEqualTo("");
    }

    @Test
    public void nonNullStringFromEmptyString() {
        assertThat(InternalQueries.nonNullString("")).isEqualTo("");
    }

    @Test
    public void nonNullStringFromNormalString() {
        assertThat(InternalQueries.nonNullString("123")).isEqualTo("123");
    }

    //endregion

    //region Tests for Queries.nullableString()

    @Test
    public void nullableStringFromNull() {
        assertThat(InternalQueries.nullableString(null)).isNull();
    }

    @Test
    public void nullableStringFromEmptyString() {
        assertThat(InternalQueries.nullableString("")).isNull();
    }

    @Test
    public void nullableStringFromNormalString() {
        assertThat(InternalQueries.nullableString("123")).isEqualTo("123");
    }

    //endregion
}
