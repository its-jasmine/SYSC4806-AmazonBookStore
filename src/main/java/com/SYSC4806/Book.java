package com.SYSC4806;

import jakarta.persistence.*;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.round;


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
        SelfHelp;
    }
    @Id
    private String ISBN;

    private String title;
    private double price;
    private String author;

    private String publisher;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private int numCopiesInStock;
    private int numCopiesSold;
    private LocalDateTime dateAdded;

    private String imageId; // need to retreve the cover image from Open Library API

    private String workId; // needed to retreive description from Open Library API

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "book_reviews",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Review> reviews;

    public Book() {
        this.reviews = new ArrayList<>();
    }
    public Book(String ISBN, String title, String author, String publisher, double price, Genre genre, int numCopiesInStock) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.numCopiesInStock = numCopiesInStock;
        this.numCopiesSold = 0;
        this.dateAdded = LocalDateTime.now();
        this.ISBN = validateISBN(ISBN);
        this.imageId = "";
        this.workId = "";
        this.price = round(price * 100.0) / 100.0; // rounds value to 2 decimal places
        this.reviews = new ArrayList<>();
    }

    public void incrementNumCopiesSold() {
        setNumCopiesSold(this.numCopiesSold + 1);
    }

    @Override
    public String toString() {
        return "Book [ ISBN=" + ISBN + ", title=" + title + ", author=" + author + ", publisher=" + publisher +
                ", genre=" + genre + ", numCopiesInStock=" + numCopiesInStock + ", price=" + price + ", numCopiesSold=" + numCopiesSold +
                ", dateAdded=" + dateAdded.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +"]";

    }

    /**
     * Validates and normalizes ISBN value.
     * @param isbn - international standard book number
     * @return normalized ISBN value
     */
    private static String validateISBN(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }

        // Normalize ISBN by removing spaces and dashes
        String normalizedIsbn = isbn.replace("-", "").replace(" ", "");

        // Check length
        if (normalizedIsbn.length() != 10 && normalizedIsbn.length() != 13) {
            throw new IllegalArgumentException("ISBN must be either 10 or 13 characters long.");
        }

        // Ensure only digits (and possibly 'X' for ISBN-10)
        if (!normalizedIsbn.matches("\\d{9}[\\dX]|\\d{13}")) {
            throw new IllegalArgumentException("ISBN contains invalid characters.");
        }

        return normalizedIsbn;
    }


    // ############################################### Setters & Getters ###############################################

    public String getISBN() {
        return ISBN;
    }
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
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

    public int getNumCopiesInStock() {
        return numCopiesInStock;
    }
    public void setNumCopiesInStock(int numCopies) {
        this.numCopiesInStock = numCopies;
    }
    public int getNumCopiesSold() {
        return numCopiesSold;
    }
    public void setNumCopiesSold(int numCopiesSold) {
        this.numCopiesSold = numCopiesSold;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    /**
     * Checks if two Books are equal
     * @param o the other book to compare to
     * @return if they are equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(publisher, book.publisher) &&
                Objects.equals(genre, book.genre);
    }

    /**
     * Override the hashing function for Book
     * @return integer hash of Book
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, author, publisher, genre);
    }

    /**
     * Add a review to the book
     * @param review to add to book
     */
    public void addReview(String review, int rating, String username) {
        this.reviews.add(new Review(rating, username, review));
    }

    /**
     * Add a review to the book
     * @return reviews for the book
     */
    public List<Review> getReviews() {
        return this.reviews;
    }

    /**
     * Get the overall rating of the book based on ratings
     * @return the overall rating
     */
    public String getOverallRating() {
        if (this.reviews.isEmpty()) {
            return "0"; // Return 0 if the map is empty to avoid division by zero
        }

        int sum = 0;
        int count = 0;

        // Iterate through the entries of the map
        for (Review review : reviews) {
            sum += review.getRating();
            count++;
        }
        return String.format("%.1f", ((double) sum / count));

    }


}
