package com.pushtorefresh.storio3.sample.many_to_many_sample.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteType;

import java.util.List;

@StorIOSQLiteType(table = "persons")
public class Person {

    @StorIOSQLiteColumn(key = true, name = "_person_id")
    @Nullable
    Long id;

    @StorIOSQLiteColumn(name = "name")
    @NonNull
    String name;

    @Nullable
    private List<Car> cars;

    public Person() {
    }

    public Person(@Nullable Long id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    public Person(@Nullable Long id, @NonNull String name, @Nullable List<Car> cars) {
        this(id, name);
        this.cars = cars;
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String name() {
        return name;
    }

    @Nullable
    public List<Car> cars() {
        return cars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (!name.equals(person.name)) return false;
        return cars != null ? cars.equals(person.cars) : person.cars == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + (cars != null ? cars.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cars=" + cars +
                '}';
    }
}
