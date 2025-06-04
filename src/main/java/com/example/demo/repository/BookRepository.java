package com.example.demo.repository;

import com.literatura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleContainsIgnoreCase(String title);

    @Query("SELECT b FROM Book b WHERE b.languages LIKE %:language%")
    List<Book> findByLanguage(String language);
}

// src/main/java/com/literatura/repository/AuthorRepository.java
package com.literatura.repository;

import com.literatura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameContainsIgnoreCase(String name);

    @Query("SELECT a FROM Author a WHERE a.birthYear <= :year AND (a.deathYear IS NULL OR a.deathYear >= :year)")
    List<Author> findAuthorsAliveInYear(Integer year);
}
