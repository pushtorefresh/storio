package com.pushtorefresh.storio.sqlite.integration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class User implements Comparable<User> {

    @NonNull
    private final String email;
    @Nullable
    private Long id;

    User(@Nullable Long id, @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    @NonNull
    public static User newInstance(@Nullable Long id, @NonNull String email) {
        return new User(id, email);
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String email() {
        return email;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + email.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", id=" + id +
                '}';
    }

    public boolean equalsExceptId(@NonNull User another) {
        return email.equals(another.email);
    }

    @Override
    public int compareTo(@NonNull User another) {
        return email.compareTo(another.email);
    }
}
