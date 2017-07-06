package com.pushtorefresh.storio.sqlite.queries;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableSet;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;

public class UpdateQueryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotAllowNullTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Table name is null or empty"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        UpdateQuery.builder().table(null);
    }

    @Test
    public void shouldNotAllowEmptyTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(equalTo("Table name is null or empty"));
        expectedException.expectCause(nullValue(Throwable.class));

        UpdateQuery.builder().table("");
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertThat(updateQuery.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertThat(updateQuery.whereArgs()).isNotNull();
        assertThat(updateQuery.whereArgs()).isEmpty();
    }

    @Test
    public void completeBuilderShouldNotAllowNullTable() {
        try {
            //noinspection ConstantConditions
            UpdateQuery.builder()
                    .table("test_table")
                    .table(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Table name is null or empty")
                    .hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowEmptyTable() {
        try {
            UpdateQuery.builder()
                    .table("test_table")
                    .table("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasMessage("Table name is null or empty")
                    .hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldUpdateTable() {
        UpdateQuery query = UpdateQuery.builder()
                .table("old_table")
                .table("new_table")
                .build();

        assertThat(query.table()).isEqualTo("new_table");
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final List<String> tags = singletonList("test_tag");

        final UpdateQuery firstQuery = UpdateQuery.builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .affectsTags(tags)
                .build();

        final UpdateQuery secondQuery = firstQuery.toBuilder().build();

        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .where("test_where")
                .whereArgs(whereArgs)
                .build();

        assertThat(updateQuery.whereArgs()).isEqualTo(asList(whereArgs));
    }

    @Test
    public void affectsTagsCollectionShouldRewrite() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("table")
                .affectsTags(new HashSet<String>((singletonList("first_call_collection"))))
                .affectsTags(new HashSet<String>((singletonList("second_call_collection"))))
                .build();

        assertThat(updateQuery.affectsTags()).isEqualTo(singleton("second_call_collection"));
    }

    @Test
    public void affectsTagsVarargShouldRewrite() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("table")
                .affectsTags("first_call_vararg")
                .affectsTags("second_call_vararg")
                .build();

        assertThat(updateQuery.affectsTags()).isEqualTo(singleton("second_call_vararg"));
    }

    @Test
    public void affectsTagsCollectionAllowsNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("table")
                .affectsTags(new HashSet<String>((singletonList("first_call_collection"))))
                .affectsTags(null)
                .build();

        assertThat(updateQuery.affectsTags()).isEmpty();
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final Set<String> tags = unmodifiableSet(new HashSet<String>() {{
            add("test_tag");
        }});

        final UpdateQuery updateQuery = UpdateQuery.builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .affectsTags(tags)
                .build();

        assertThat(updateQuery.table()).isEqualTo(table);
        assertThat(updateQuery.where()).isEqualTo(where);
        assertThat(updateQuery.whereArgs()).isEqualTo(asList(whereArgs));
        assertThat(updateQuery.affectsTags()).isEqualTo(tags);
    }

    @Test
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        try {
            UpdateQuery.builder()
                    .table("test_table")
                    .whereArgs("someArg") // Without WHERE clause!
                    .build();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("You can not use whereArgs without where clause");
        }
    }

    @Test
    public void shouldAllowNullWhereArgsWithoutWhereClause() {
        //noinspection NullArgumentToVariableArgMethod
        UpdateQuery.builder()
                .table("test_table")
                .whereArgs((Object[]) null)
                .build();

        // We don't expect any exceptions here
    }

    @Test
    public void shouldNotAllowNullTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        UpdateQuery.builder()
                .table("table")
                .affectsTags((String) null)
                .build();
    }

    @Test
    public void shouldNotAllowEmptyTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        UpdateQuery.builder()
                .table("table")
                .affectsTags("")
                .build();
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(UpdateQuery.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(UpdateQuery.class)
                .check();
    }
}
