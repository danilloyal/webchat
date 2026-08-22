package com.danilloyal.webchat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danilloyal.webchat.model.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByUsername(String username);
}
