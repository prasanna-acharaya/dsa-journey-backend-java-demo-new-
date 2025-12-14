package com.bom.dsa.repository;

import com.bom.dsa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByDsaUniqueCode(String dsaUniqueCode);

    Optional<User> findByEmail(String email);

    boolean existsByDsaUniqueCode(String dsaUniqueCode);

    boolean existsByEmail(String email);
}
