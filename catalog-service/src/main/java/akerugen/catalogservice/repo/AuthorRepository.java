package akerugen.catalogservice.repo;

import akerugen.catalogservice.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> findByFullNameContaining(@Param("name") String name);

    Optional<Author> findByFirstNameAndLastNameAndPatronymic(String firstName, String lastName, String patronymic);

    boolean existsByFirstNameAndLastNameAndPatronymic(String firstName, String lastName, String patronymic);
}