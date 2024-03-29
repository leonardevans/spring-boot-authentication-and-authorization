package com.lemutugi.repository;

import com.lemutugi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByMobile(Long mobile);
    boolean existsByMobileAndIdNot(Long mobile, Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Page<User> findAll(Pageable pageable);

    @Query(value = "SELECT u FROM User u WHERE u.username LIKE %?1% OR u.fName LIKE %?1% OR u.lName LIKE %?1% OR u.email LIKE %?1%", nativeQuery = false)
    Page<User> search(Pageable pageable, String search);
}
