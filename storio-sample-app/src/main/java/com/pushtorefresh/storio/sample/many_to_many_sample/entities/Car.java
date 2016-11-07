package com.pushtorefresh.storio.sample.many_to_many_sample.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.List;

@StorIOSQLiteType(table = CarTable.TABLE)
public class Car {

    @StorIOSQLiteColumn(key = true, name = CarTable.COLUMN_ID)
    @Nullable
    Long id;

    @StorIOSQLiteColumn(name = CarTable.COLUMN_MODEL)
    @NonNull
    String model;

    @Nullable
    private List<Person> persons;

    public Car() {
        // keep for type mapping generator
    }

    public Car(@NonNull String model) {
        this(null, model, null);
    }
    public Car(@Nullable Long id, @NonNull String model, @Nullable List<Person> persons) {
        this.id = id;
        this.model = model;
        this.persons = persons;
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String mark() {
        return model;
    }

    @Nullable
    public List<Person> persons() {
        return persons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (id != null ? !id.equals(car.id) : car.id != null) return false;
        if (!model.equals(car.model)) return false;
        return persons != null ? persons.equals(car.persons) : car.persons == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + model.hashCode();
        result = 31 * result + (persons != null ? persons.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", persons=" + persons +
                '}';
    }
}
