package com.movielist.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movielist.entity.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class TmdbClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl = "https://api.themoviedb.org/3";

    public TmdbClient(RestTemplate restTemplate, ObjectMapper objectMapper,
            @Value("${tmdb.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        System.out.println("DEBUG: Loaded TMDB API Key: " + (apiKey != null
                ? (apiKey.substring(0, Math.min(4, apiKey.length())) + "..." + " (length: " + apiKey.length() + ")")
                : "null"));
    }

    private List<JsonNode> loadMockMovies() {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/mock_movies.json");
            if (is != null) {
                return objectMapper.readValue(is, new com.fasterxml.jackson.core.type.TypeReference<List<JsonNode>>() {
                });
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not load mock_movies.json: " + e.getMessage());
        }
        return List.of();
    }

    public List<JsonNode> searchMovies(String query) {
        try {
            String url;
            String lowerQuery = query.toLowerCase().trim();
            java.util.Map<String, String> genreMap = java.util.Map.of(
                "action", "28",
                "comedy", "35",
                "crime", "80",
                "drama", "18",
                "horror", "27"
            );
            
            if (genreMap.containsKey(lowerQuery)) {
                url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/discover/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("with_genres", genreMap.get(lowerQuery))
                    .queryParam("sort_by", "popularity.desc")
                    .build()
                    .toUriString();
            } else if (lowerQuery.equals("marvel")) {
                url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/discover/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("with_companies", "420")
                    .queryParam("sort_by", "popularity.desc")
                    .build()
                    .toUriString();
            } else {
                url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("query", query)
                        .build()
                        .toUriString();
            }

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            List<JsonNode> results = new ArrayList<>();
            if (response != null && response.has("results")) {
                response.get("results").forEach(results::add);
            }
            return results;
        } catch (Exception e) {
            System.out.println(
                    "WARNING: TMDB API search failed. Falling back to local mock data. Error: " + e.getMessage());
            List<JsonNode> results = new ArrayList<>();
            String lowercaseQuery = query.toLowerCase();
            for (JsonNode movie : loadMockMovies()) {
                if (movie.has("title") && movie.get("title").asText().toLowerCase().contains(lowercaseQuery)) {
                    results.add(movie);
                }
            }
            return results;
        }
    }

    public JsonNode getMovieDetails(long tmdbId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/" + tmdbId)
                    .queryParam("api_key", apiKey)
                    .queryParam("append_to_response", "credits,keywords")
                    .build()
                    .toUriString();
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            System.out.println("WARNING: TMDB API getMovieDetails failed for ID " + tmdbId
                    + ". Falling back to local mock data. Error: " + e.getMessage());
            for (JsonNode movie : loadMockMovies()) {
                if (movie.has("id") && movie.get("id").asLong() == tmdbId) {
                    return movie;
                }
            }
            // If not found in mock list, return a minimal dummy object
            com.fasterxml.jackson.databind.node.ObjectNode dummy = objectMapper.createObjectNode();
            dummy.put("id", tmdbId);
            dummy.put("title", "Unknown Movie");
            return dummy;
        }
    }

    public List<JsonNode> getTrendingMovies() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/trending/movie/week")
                    .queryParam("api_key", apiKey)
                    .build()
                    .toUriString();

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            List<JsonNode> results = new ArrayList<>();
            if (response != null && response.has("results")) {
                response.get("results").forEach(results::add);
            }
            return results;
        } catch (Exception e) {
            System.out.println("WARNING: TMDB API getTrendingMovies failed. Falling back to local mock data. Error: "
                    + e.getMessage());
            return loadMockMovies();
        }
    }

    public List<JsonNode> getPopularMovies(int page) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/popular")
                    .queryParam("api_key", apiKey)
                    .queryParam("page", page)
                    .build()
                    .toUriString();

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            List<JsonNode> results = new ArrayList<>();
            if (response != null && response.has("results")) {
                response.get("results").forEach(results::add);
            }
            return results;
        } catch (Exception e) {
            System.out.println("WARNING: TMDB API getPopularMovies failed for page " + page
                    + ". Falling back to local mock data. Error: " + e.getMessage());
            return loadMockMovies();
        }
    }

    public List<JsonNode> getSimilarMovies(long tmdbId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/movie/" + tmdbId + "/similar")
                    .queryParam("api_key", apiKey)
                    .build()
                    .toUriString();

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            List<JsonNode> results = new ArrayList<>();
            if (response != null && response.has("results")) {
                response.get("results").forEach(results::add);
            }
            return results;
        } catch (Exception e) {
            System.out.println("WARNING: TMDB API getSimilarMovies failed for ID " + tmdbId
                    + ". Falling back to local mock data. Error: " + e.getMessage());
            return loadMockMovies();
        }
    }

    public Movie mapToEntity(JsonNode node) {
        Movie movie = new Movie();
        movie.setTmdbId(node.get("id").asLong());
        movie.setTitle(node.has("title") ? node.get("title").asText() : node.get("name").asText());
        if (node.has("release_date") && !node.get("release_date").asText().isBlank()) {
            String date = node.get("release_date").asText();
            movie.setYear(Integer.parseInt(date.substring(0, 4)));
        }
        movie.setPosterUrl(node.has("poster_path") && !node.get("poster_path").isNull()
                ? "https://image.tmdb.org/t/p/w500" + node.get("poster_path").asText()
                : null);
        movie.setOverview(node.has("overview") ? node.get("overview").asText() : "");
        return movie;
    }

    public void enrichMovieFromDetails(Movie movie, JsonNode details) throws Exception {
        if (details.has("genres")) {
            movie.setGenres(objectMapper.writeValueAsString(details.get("genres")));
        }
        if (details.has("keywords") && details.get("keywords").has("keywords")) {
            movie.setKeywords(objectMapper.writeValueAsString(details.get("keywords").get("keywords")));
        }
        if (details.has("credits")) {
            JsonNode credits = details.get("credits");
            if (credits.has("cast")) {
                var castList = credits.get("cast");
                var topCast = objectMapper.createArrayNode();
                int limit = Math.min(5, castList.size());
                for (int i = 0; i < limit; i++) {
                    topCast.add(castList.get(i).get("name").asText());
                }
                movie.setCast(objectMapper.writeValueAsString(topCast));
            }
            if (credits.has("crew")) {
                for (JsonNode crew : credits.get("crew")) {
                    if ("Director".equals(crew.get("job").asText())) {
                        movie.setDirector(crew.get("name").asText());
                        break;
                    }
                }
            }
        }
        if (details.has("overview")) {
            movie.setOverview(details.get("overview").asText());
        }
    }
}
