package com.SYSC4806;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Customer extends AppUser {
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "purchase_history",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> purchaseHistory;

    @ElementCollection
    @CollectionTable(
            name = "customer_cart",
            joinColumns = @JoinColumn(name = "customer_id")
    )
    @MapKeyJoinColumn(name = "book_id")
    @Column(name = "quantity")
    private Map<Book, Integer> cart = new HashMap<>();

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

    /**
     * add the book to the customer's cart
     * @param book the book to add
     */
    public void addToCart(Book book) {
        cart.merge(book, 1, Integer::sum); // Increment quantity by 1 if the book is already in the cart, or add it with quantity 1 if it's not.
    }

    /**
     * remove the specified book from the cart
     * @param String the book to remove
     *
     */
    public void removeFromCart(String ISBN) {
        // Find the book in the cart by ISBN
        Book bookToRemove = cart.keySet()
                .stream()
                .filter(book -> book.getISBN().equals(ISBN))
                .findFirst()
                .orElse(null);

        if (bookToRemove != null) {
            cart.remove(bookToRemove); // Remove the book from the cart
        }
    }



    /**
     * checkout the items in the cart and remove them if the checkout is successful
     */
    public void checkout() {
        cart.forEach((book, quantity) -> {
            for (int i = 0; i < quantity; i++) {
                this.purchaseHistory.add(book);
            }
        });
        cart.clear();
    }

    /**
     * getter for the cart
     * @return
     */
    public Map<Book, Integer> getCart() {
        return cart;
    }

}
