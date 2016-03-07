package com.pushtorefresh.storio.sqlite.integration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class User implements Comparable<User> {

    @Nullable
    private Long id;
    @NonNull
    private final String email;
    @Nullable
    private final String phone;

    User(@Nullable Long id, @NonNull String email, @Nullable String phone) {
        this.id = id;
        this.email = email;
        this.phone = phone;
    }

    @NonNull
    public static User newInstance(@Nullable Long id, @NonNull String email) {
        return newInstance(id, email, null);
    }

    @NonNull
    public static User newInstance(@Nullable Long id, @NonNull String email, @Nullable String phone) {
        return new User(id, email, phone);
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String email() {
        return email;
    }

    @Nullable
    public String phone() {
        return phone;
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
        if (!email.equals(user.email)) return false;
        return !(phone != null ? !phone.equals(user.phone) : user.phone != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + email.hashCode();
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public boolean equalsExceptId(@NonNull User another) {
        if (!email.equals(another.email)) return false;
        return !(phone != null ? !phone.equals(another.phone) : another.phone != null);
    }

    @Override
    public int compareTo(@NonNull User another) {
        return email.compareTo(another.email);
    }
}
