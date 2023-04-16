package com.boukriinfo.ecommerce.repositories;

import com.boukriinfo.ecommerce.entities.RefreshToken;
import com.boukriinfo.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    @Modifying//This annotation is used to indicate that a method modifies the database.
    int deleteByUser(User user);

}
