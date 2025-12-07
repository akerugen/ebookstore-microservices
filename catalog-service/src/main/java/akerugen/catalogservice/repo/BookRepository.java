package akerugen.catalogservice.repo;

import akerugen.catalogservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthorId(Long authorId);
    List<Book> findByGenreId(Long genreId);
    List<Book> findByBookStatusId(Long bookStatusId);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId AND b.genre.id = :genreId")
    List<Book> findByAuthorIdAndGenreId(@Param("authorId") Long authorId, @Param("genreId") Long genreId);

    boolean existsByTitle(String title);
}