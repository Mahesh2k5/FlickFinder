import React, { useState } from 'react';
import { api } from '../api/client';

const SimilarSearch = ({ onCardClick }) => {
    const [title, setTitle] = useState('');
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [searched, setSearched] = useState(false);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!title.trim()) return;

        setLoading(true);
        setError(null);
        setSearched(true);
        try {
            const data = await api.recommendations.getSimilarByTitle(title);
            setMovies(data || []);
        } catch (err) {
            console.error('Error finding similar movies:', err);
            setError(err.message || 'Could not find similar movies. Make sure the title exists.');
            setMovies([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="similar-search-container">
            <div className="similar-search-header">
                <h2>Similar Movies Finder</h2>
                <p>Enter any movie title, and our FastAPI microservice will calculate its feature vector to recommend similar films.</p>
            </div>

            <form onSubmit={handleSearch} className="similar-search-form">
                <input
                    type="text"
                    placeholder="Enter movie title (e.g., Inception, The Dark Knight, Toy Story)..."
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    required
                />
                <button type="submit" disabled={loading}>
                    {loading ? 'Searching...' : 'Find Similar'}
                </button>
            </form>

            <div className="similar-search-results">
                {loading ? (
                    <p className="status-message">Calculating vectors and searching similar movies...</p>
                ) : error ? (
                    <p className="status-message error">{error}</p>
                ) : searched && movies.length === 0 ? (
                    <div className="empty-state">
                        <p>No similar movies found.</p>
                        <p className="empty-subtext">Try searching for other mainstream movies or popular seeding titles.</p>
                    </div>
                ) : (
                    <div className="movie-grid">
                        {movies.map((movie) => (
                            <div 
                                key={movie.id} 
                                className="movie-card"
                                onClick={() => onCardClick(movie)}
                            >
                                <div className="movie-poster">
                                    <img 
                                        src={movie.posterUrl || 'https://via.placeholder.com/300x450?text=No+Image'} 
                                        alt={movie.title} 
                                    />
                                </div>
                                <div className="movie-info">
                                    <h3>{movie.title}</h3>
                                    <p>{movie.year}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default SimilarSearch;
