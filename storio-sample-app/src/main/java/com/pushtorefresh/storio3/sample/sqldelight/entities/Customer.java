package com.pushtorefresh.storio3.sample.sqldelight.entities;

//
//import android.content.ContentValues;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver;
//
//import auto.parcel.AutoParcel;
//
//import static com.pushtorefresh.storio3.sample.sqldelight.SQLUtils.makeSimpleContentValuesInsertPutResolver;
//
//@AutoParcel
//public abstract class Customer implements CustomerModel {
//
//    @NonNull
//    public static final Factory<Customer> FACTORY = new Factory<Customer>(new Creator<Customer>() {
//        @Override
//        public Customer create(
//                @Nullable Long id,
//                @NonNull String name,
//                @NonNull String surname,
//                @NonNull String city
//        ) {
//            return builder()
//                    .id(id)
//                    .name(name)
//                    .surname(surname)
//                    .city(city)
//                    .build();
//        }
//    });
//
//    @NonNull
//    public static final Mapper<Customer> CURSOR_MAPPER = new Mapper<Customer>(FACTORY);
//
//    @NonNull
//    public static final PutResolver<ContentValues> CV_PUT_RESOLVER = makeSimpleContentValuesInsertPutResolver(TABLE_NAME);
//
//    @NonNull
//    public static Builder builder() {
//        return new AutoParcel_Customer.Builder();
//    }
//
//    @AutoParcel.Builder
//    public interface Builder {
//
//        @NonNull
//        Builder id(@Nullable Long id);
//
//        @NonNull
//        Builder name(@NonNull String name);
//
//        @NonNull
//        Builder surname(@NonNull String surname);
//
//        @NonNull
//        Builder city(@NonNull String city);
//
//        @NonNull
//        Customer build();
//    }
//}
