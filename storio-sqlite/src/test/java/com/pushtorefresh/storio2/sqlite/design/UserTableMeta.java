package com.pushtorefresh.storio2.sqlite.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio2.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio2.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.UpdateQuery;

import static org.mockito.Mockito.mock;

class UserTableMeta {

    static final String TABLE = "users";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_EMAIL = "email";
    static final PutResolver<User> PUT_RESOLVER = new DefaultPutResolver<User>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull User object) {
            return InsertQuery.builder()
                    .table(TABLE)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull User user) {
            return UpdateQuery.builder()
                    .table(TABLE)
                    .where(COLUMN_EMAIL + " = ?")
                    .whereArgs(user.email())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull User object) {
            return mock(ContentValues.class);
        }
    };
    static final GetResolver<User> GET_RESOLVER = new DefaultGetResolver<User>() {
        @NonNull
        @Override
        public User mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {
            return mock(User.class);
        }
    };
    static final DeleteResolver<User> DELETE_RESOLVER = new DefaultDeleteResolver<User>() {
        @NonNull
        @Override
        public DeleteQuery mapToDeleteQuery(@NonNull User user) {
            return DeleteQuery.builder()
                    .table(TABLE)
                    .where(COLUMN_EMAIL + " = ?")
                    .whereArgs(user.email())
                    .build();
        }
    };

    private UserTableMeta() {
        throw new IllegalStateException("No instances please");
    }
}
