package org.example;

import java.util.Comparator;

import static org.example.Main.users;

public class User {
    private static long idCounter = users.stream()
            .map(User::getId)
            .filter(vId -> vId != users.stream().max(Comparator.comparingLong(User::getId))
            .orElse(new User()).getId())
            .findFirst()
            .orElse((long) 0);
    private long id;
    private String firstName;
    private String lastName;
    private String avatar;
    private String email;

    public User() {
        id = ++idCounter;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return id == user.id;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastname='" + lastName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}