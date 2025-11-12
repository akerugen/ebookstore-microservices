package akerugen.userservice.repo;

import akerugen.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // т.к. unique = true у полей username и email
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
}