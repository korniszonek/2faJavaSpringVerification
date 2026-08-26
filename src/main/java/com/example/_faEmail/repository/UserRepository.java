package com.example._faEmail.repository;

import com.example._faEmail.model.UserModel;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel,Long> {
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<UserModel> findByNickname(String nickname);
}
