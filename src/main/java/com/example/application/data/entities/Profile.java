package com.example.application.data.entities;
import jakarta.persistence.*;

@Entity
public class Profile extends AbstractEntity {

    private String bio;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

