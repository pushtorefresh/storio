package com.pushtorefresh.storio.contentresolver.integration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class User implements Comparable<User> {

    @Nullable
    private Long id;

    @NonNull
    private String email;

    private User(@Nullable Long id, @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    @NonNull
    public static User newInstance(@NonNull Long id, @NonNull String email) {
        return new User(id, email);
    }

    @Nullable
    public Long id() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @NonNull
    public String email() {
        return email;
    }

    public boolean equalsExceptId(@NonNull User other) {
        return email.equals(other.email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull User another) {
        return email == null ? 0 : email.compareTo(another.email);
    }
}
