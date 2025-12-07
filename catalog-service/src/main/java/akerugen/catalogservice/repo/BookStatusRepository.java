package akerugen.catalogservice.repo;

import akerugen.catalogservice.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookStatusRepository extends JpaRepository<BookStatus, Long> {

    Optional<BookStatus> findByName(String name);
    boolean existsByName(String name);
}