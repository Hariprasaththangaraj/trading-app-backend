package com.trading.app.login.repo;

import com.trading.app.login.model.User;
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    User findByUsername(String username);
}
