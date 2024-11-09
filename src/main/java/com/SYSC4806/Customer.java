package com.SYSC4806;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import java.util.List;

@Entity
public class Customer extends AppUser {

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Book> books;

    public Customer(){
        super();
    }
    public Customer(String username, String password) {
        super(username, password);
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }
}
