package com.example.demo.model;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true) // Títulos de libros deben ser únicos
    private String title;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    private String languages; // Se guardará como una cadena separada por comas
    private Integer downloadCount;

    public Book() {}

    public Book(BookData bookData) {
        this.title = bookData.title();
        this.languages = String.join(",", bookData.languages()); // Convertir lista a String
        this.downloadCount = bookData.downloadCount();
        this.authors = bookData.authors().stream()
                .map(Author::new)
                .collect(Collectors.toSet());
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Set<Author> getAuthors() { return authors; }
    public void setAuthors(Set<Author> authors) { this.authors = authors; }
    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }
    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }

    @Override
    public String toString() {
        return "---------- LIBRO ---------" +
                "\nTítulo: " + title +
                "\nAutor(es): " + authors.stream().map(Author::getName).collect(Collectors.joining(", ")) +
                "\nIdioma(s): " + languages +
                "\nNúmero de descargas: " + downloadCount +
                "\n--------------------------\n";
    }
}