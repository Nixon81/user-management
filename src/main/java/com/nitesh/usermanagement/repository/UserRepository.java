package com.nitesh.usermanagement.repository;

import com.nitesh.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @Repository
 * Marks this interface as a Spring-managed repository bean.
 * It also helps Spring translate database exceptions into
 * Spring-specific exceptions.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * JpaRepository<User, Long> provides:
     *
     * - save(User user)              → INSERT / UPDATE
     * - findById(Long id)             → SELECT by ID
     * - findAll()                     → SELECT all users
     * - deleteById(Long id)           → DELETE by ID
     * - existsById(Long id)           → Check existence
     *
     * We get ALL of this for free.
     */

    /**
     * Custom query method (derived query)
     * Spring automatically generates SQL from the method name.
     *
     * This will become:
     * SELECT * FROM users WHERE email = ?
     */
    User findByEmail(String email);

    // Search users by name (partial match, case-insensitive)
    List<User> findByNameContainingIgnoreCase(String name);

    // Search users by email (partial match, case-insensitive)
    List<User> findByEmailContainingIgnoreCase(String email);

}
