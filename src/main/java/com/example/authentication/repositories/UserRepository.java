package com.example.authentication.repositories;

import com.example.authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select v.user from VerificationToken v where v.token = :token")
    Optional<User> findUserByToken(@Param("token") String token);
}
