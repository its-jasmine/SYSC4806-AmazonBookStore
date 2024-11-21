package com.SYSC4806;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends AppUser {
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "purchase_history",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> purchaseHistory;

    /**
     * Constructor for Customer
     */
    public Customer(){
        super();
        purchaseHistory = new ArrayList<>();
    }

    /**
     * Constructor for Customer
     * @param username of Customer
     * @param password of Customer
     */
    public Customer(String username, String password) {
        super(username, password);
        purchaseHistory = new ArrayList<>();
    }

    /**
     * Adds a book to the purchase history
     * @param book to add to purchase history
     */
    public void addToHistory(Book book) {
        this.purchaseHistory.add(book);
    }

    /**
     * @return the customer's purchase history
     */
    public List<Book> getPurchaseHistory() {
        return purchaseHistory;
    }

}
