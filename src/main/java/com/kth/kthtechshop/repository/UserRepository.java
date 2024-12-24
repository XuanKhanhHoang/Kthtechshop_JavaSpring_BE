package com.kth.kthtechshop.repository;

import com.kth.kthtechshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);

    @Query("SELECT u FROM User u JOIN u.userFacebook uf WHERE u.email = :email AND uf.facebookId = :facebookId")
    Optional<User> findByEmailAndFacebookId(@Param("email") String email, @Param("facebookId") String facebookId);
}
