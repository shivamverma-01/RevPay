package com.revpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
