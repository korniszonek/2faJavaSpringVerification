package com.example._faEmail.model;

import com.example._faEmail.dto.RegisterUserDto;
import jakarta.persistence.*;

@Entity
@Table(name = "your_table name") // this
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //we use long for id
    private String nickname;
    private String password;
    private String email;
    @Column(nullable = false)
    private boolean isVerified = false;

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static UserModel fromDto(RegisterUserDto user, String hashedPass){
        UserModel userModel = new UserModel();
        userModel.setNickname(user.nickname());
        userModel.setEmail(user.email());
        userModel.setPassword(hashedPass);
        return userModel;
    }
}
