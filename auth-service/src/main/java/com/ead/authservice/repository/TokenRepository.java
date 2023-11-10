package com.ead.authservice.repository;

import com.ead.authservice.model.Token;
import com.ead.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);

    @Query(value = """
      select t from Token t\s
      inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Long id);

    @Query(value = """
      select t from Token t\s
      inner join User u\s
      on t.user.id = u.id\s
      where t.token = :token and (t.expired = false or t.revoked = false)\s
      """)
    Optional<User> findUserByToken(String token);
}
