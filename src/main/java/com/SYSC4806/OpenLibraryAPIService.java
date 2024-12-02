package com.SYSC4806;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Class to make REST requests to the Open Library API (https://openlibrary.org/dev/docs/api/books).
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
public class OpenLibraryAPIService {
    public static final String SEARCH_BASE_URL = "https://openlibrary.org/search.json";
    public static final String FIELDS_FOR_BOOK_CONSTRUCTION = "isbn,title,author_name,publisher,cover_i,key";
    public static final String ENGLISH_LANGUAGE_FILTER = "eng";

    /**
     * Fetching book data from the Open Library API
     * @param subject of books to fetch
     * @param limit of number of books to fetch
     * @param sortFilter to sort the fetched results
     * @return response containing the book data
     */
    public static ResponseEntity<String> fetchSearchResponse(String subject, int limit, String sortFilter){
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = SEARCH_BASE_URL+ "?" + "subject=" + subject + "&" +
                "limit=" + limit + "&" +
                "fields=" + FIELDS_FOR_BOOK_CONSTRUCTION + "&" +
                "language=" + ENGLISH_LANGUAGE_FILTER + "&" +
                "sort=" +sortFilter;
        System.out.println(apiUrl);
        return restTemplate.getForEntity(apiUrl, String.class);
    }

    /**
     * Fetches the description of the book with the given workId.
     * @param workId - id needed by the Open Library API to easily fetch the book description
     * @return description of book
     */
    public static String fetchDescription(String workId) {
        if (workId.isEmpty()) return "";

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://openlibrary.org" + workId + ".json";
        System.out.println(apiUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        String description = "";
        try {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            JsonNode data = root.get("description");
            description = data.has("value") ? data.get("value").asText() : data.asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return description;
    }
}
