const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

/**
 * Clean fetch-based request helper that handles response statuses,
 * parses JSON, and injects JWT authorization headers if a token is present.
 */
async function request(path, options = {}) {
    const token = localStorage.getItem('token');
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };
    
    // Inject the JWT token if logged in
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const config = {
        ...options,
        headers,
    };
    
    const response = await fetch(`${API_URL}${path}`, config);
    
    // HTTP 204 No Content has no body
    if (response.status === 204) {
        return null;
    }
    
    if (!response.ok) {
        // Attempt to parse validation/business errors from backend
        const errorData = await response.json().catch(() => ({ message: 'An unexpected error occurred' }));
        throw new Error(errorData.message || `Request failed with status ${response.status}`);
    }
    
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

export const api = {
    auth: {
        login: (email, password) => request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        }),
        register: (username, email, password) => request('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, email, password })
        }),
        me: () => request('/users/me')
    },
    movies: {
        search: (query) => request(`/movies/search?q=${encodeURIComponent(query)}`),
        getTrending: () => request('/movies/trending'),
        getById: (id) => request(`/movies/${id}`),
        getByTmdbId: (tmdbId) => request(`/movies/tmdb/${tmdbId}`)
    },
    likes: {
        get: () => request('/likes'),
        add: (movieId) => request(`/likes/${movieId}`, { method: 'POST' }),
        remove: (movieId) => request(`/likes/${movieId}`, { method: 'DELETE' })
    },
    watchlist: {
        get: () => request('/watchlist'),
        add: (movieId) => request(`/watchlist/${movieId}`, { method: 'POST' }),
        remove: (movieId) => request(`/watchlist/${movieId}`, { method: 'DELETE' })
    },
    recommendations: {
        getForYou: () => request('/recommendations/for-you'),
        getSimilar: (movieId) => request(`/recommendations/similar?movieId=${movieId}`),
        getSimilarByTitle: (title) => request('/recommendations/similar-by-title', {
            method: 'POST',
            body: JSON.stringify({ title })
        })
    }
};
