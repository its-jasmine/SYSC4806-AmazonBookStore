package com.SYSC4806;

import jakarta.persistence.*;

@Entity
@Table(
        name = "book",
        uniqueConstraints = @UniqueConstraint(columnNames = {"title", "author", "publisher", "genre"})
)
public class Book {
    public enum Genre {
        Fiction,
        NonFiction,
        Mystery,
        SciFi,
        Fantasy,
        Romance,
        Memoirs,
        SelfHelp
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Ensure you have the right strategy for your database
    private Integer id;

    private String title;
    private String author;
    private String publisher;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private int numCopies;

    public Book() {}


    public Book(String title, String author, String publisher, Genre genre, int numCopies) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.numCopies = numCopies;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public Genre getGenre() {
        return genre;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getNumCopies() {
        return numCopies;
    }

    public void setNumCopies(int numCopies) {
        this.numCopies = numCopies;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + title + ", author=" + author + ", publisher=" + publisher + ", genre=" + genre + "]";
    }

}
