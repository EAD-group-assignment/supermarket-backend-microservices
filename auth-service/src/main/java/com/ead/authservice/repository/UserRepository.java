package com.ead.authservice.repository;

import com.ead.authservice.enums.RoleType;
import com.ead.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional <User> findByEmail(String email);

    @Query(value = """
      select u from User u\s
      where u.email = :email and u.role = :role\s
      """)
    Optional <User> findByEmailAndRole(String email, RoleType role);
}
