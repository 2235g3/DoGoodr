package com.vidalia.backend.repository;

import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsBySecondaryEmailIgnoreCase(String secondaryEmail);
    boolean existsByPhoneNumber(String phoneNumber);
    List<User> findAllByRole(Role role);
}
