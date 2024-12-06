package com.SYSC4806;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookRecommendation {
    private final List<Customer> users;

    /**
     * Constructor for BookRecommendation
     * @param users all users currently in the system
     */
    public BookRecommendation(List<Customer> users) {
        this.users = users;
    }

    /**
     * Returns book recommendations based on other users
     * @param user the user who we are getting recommendations for
     * @return the list of books to be recommended
     */
    public List<Book> getRecommendation(Customer user) {
        Set<Book> books1 = new HashSet<>(user.getPurchaseHistory());
        Map<Customer, Double> recs = new HashMap<>();
        for (Customer user2 : users) {
            Set<Book> books2 = new HashSet<>(user2.getPurchaseHistory());
            double jaccardDist = (double) intersectionSize(books1, books2) / unionSize(books1, books2);
            if ((jaccardDist != 1.0) && (jaccardDist != 0.0))
                recs.put(user2, (1.0 - jaccardDist));
        }

        List<Customer> similarUsers = recs.entrySet().stream()
                .sorted((Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .toList();

        //take the top 3 users that have the most matching book and recommend
        Set<Book> recommendedBooks = new HashSet<>();
        int recSize = Math.min(similarUsers.size(), 5);
        for (int i = 0; i < recSize; i++) {
            recommendedBooks.addAll(similarUsers.get(i).getPurchaseHistory());
        }
        user.getPurchaseHistory().forEach(recommendedBooks::remove);

        return new ArrayList<>(recommendedBooks);

    }

    /**
     * Calculate intersection of two sets
     * @param set1 first set of intersection
     * @param set2 second set of intersection
     * @return the intersection of two sets
     */
    private int intersectionSize(Set<Book> set1, Set<Book> set2) {
        Set<Book> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.size();
    }

    /**
     * Calculate union of two sets
     * @param set1 first set of union
     * @param set2 second set of union
     * @return the union of two sets
     */
    private int unionSize(Set<Book> set1, Set<Book> set2) {
        Set<Book> union = new HashSet<>(set1);
        union.addAll(set2);
        return union.size();
    }
}
