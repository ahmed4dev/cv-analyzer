package com.cvanalyzer.services;

import com.cvanalyzer.models.User;
import com.cvanalyzer.models.dto.SignupRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(SignupRequest signUpRequest);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
