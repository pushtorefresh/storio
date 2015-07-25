package com.pushtorefresh.storio.test;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableList;
import static org.mockito.Mockito.mock;

public class ToStringChecker<T> {

    @NonNull
    private final Class<T> clazz;

    ToStringChecker(@NonNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <T> ToStringChecker.Builder<T> forClass(@NonNull Class<T> clazz) {
        return new Builder<T>(clazz);
    }

    public static class Builder<T> {

        @NonNull
        private final Class<T> clazz;

        Builder(@NonNull Class<T> clazz) {
            this.clazz = clazz;
        }

        public void check() {
            new ToStringChecker<T>(clazz).check();
        }
    }

    public void check() {
        try {
            final T object = createObject();
            final List<Field> fieldsForToString = getFieldsForToString();

            writeSampleValuesToFields(object, fieldsForToString);

            final String objectToString = object.toString();
            verifyThatFieldsPresentedInToString(object, objectToString, fieldsForToString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @NonNull
    private T createObject() {
        Constructor[] constructors = clazz.getDeclaredConstructors();

        for (Constructor constructor : constructors) {
            try {
                constructor.setAccessible(true);

                List<Object> sampleParameters = new ArrayList<Object>();

                for (Class parameterType : constructor.getParameterTypes()) {
                    sampleParameters.add(createSampleValueOfType(parameterType));
                }

                //noinspection unchecked
                return (T) constructor.newInstance(sampleParameters.toArray());
            } catch (Exception uh) {
                // No luck with this constructor, let's try another one
            }
        }

        throw new IllegalStateException("Tried all declared constructors, no luck :(");
    }

    @NonNull
    private List<Field> getFieldsForToString() {
        Field[] allFields = clazz.getDeclaredFields();

        List<Field> fieldsForToString = new ArrayList<Field>(allFields.length);

        for (Field field : allFields) {
            // Transient field should not be part of toString()
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            if (field.getName().equals("$jacocoData")) {
                // Jacoco coverage tool can add field for internal usage
                // It wont affect release code, of course.
                continue;
            }

            fieldsForToString.add(field);
        }

        return unmodifiableList(fieldsForToString);
    }

    private void writeSampleValuesToFields(@NonNull T object, @NonNull List<Field> fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            writeSampleValueToField(object, field);
        }
    }

    private void writeSampleValueToField(@NonNull Object object, @NonNull Field field) {
        final Object value = createSampleValueOfType(field.getType());

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    Object createSampleValueOfType(@NonNull Class<?> type) {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return true;
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return 1;
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return 1L;
        } else if (type.equals(String.class)) {
            return "some_string";
        } else if (type.equals(List.class)) {
            return asList("1", "2", "3");
        } else if (type.equals(Map.class)) {
            return singletonMap("map_key", "map_value");
        } else if (type.equals(Set.class)) {
            return singleton("set_item");
        } else if (type.equals(Uri.class)) {
            return mock(Uri.class);
        } else {
            throw new IllegalStateException("Can not set sample value to the field of type " + type);
        }
    }

    private void verifyThatFieldsPresentedInToString(@NonNull T object, @NonNull String objectToString, @NonNull List<Field> fields) {
        try {
            for (Field field : fields) {
                final String fieldValueToString = field.get(object).toString();

                if (// For regular fields
                        !objectToString.contains(field.getName() + "=" + fieldValueToString)
                                // IDEA generates ='value' for Strings
                                && !objectToString.contains(field.getName() + "='" + fieldValueToString)) {
                    throw new AssertionError("toString() does not contain field = "
                            + field.getName() + ", object = " + object);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
