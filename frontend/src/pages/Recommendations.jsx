import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';

const Recommendations = ({ onCardClick }) => {
    const { user, likes, watchlist } = useAuth();
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRecommendations = async () => {
            if (!user) return;
            setLoading(true);
            setError(null);
            try {
                const data = await api.recommendations.getForYou();
                setMovies(data || []);
            } catch (err) {
                console.error('Failed to fetch recommendations:', err);
                setError(err.message || 'Could not load recommendations.');
            } finally {
                setLoading(false);
            }
        };

        fetchRecommendations();
    }, [user, likes.length, watchlist.length]); // Refresh if likes/watchlist counts change (user interacts)

    if (!user) {
        return (
            <div className="recommendations-container">
                <div className="status-card">
                    <p>Please log in to view personalized recommendations.</p>
                </div>
            </div>
        );
    }

    const hasInteractions = likes.length > 0 || watchlist.length > 0;

    return (
        <div className="recommendations-container">
            <div className="recommendations-header">
                <h2>For You</h2>
                <p>Personalized suggestions powered by our Content-Based & Collaborative Filtering ML model.</p>
            </div>

            {!hasInteractions && (
                <div className="ml-tip-banner">
                    <span className="tip-icon">💡</span>
                    <div className="tip-content">
                        <strong>Tip for better recommendations:</strong> You haven't liked or watchlisted any movies yet. 
                        We are showing trending movies. Try liking a few movies to personalize your feed!
                    </div>
                </div>
            )}

            {loading ? (
                <p className="status-message">Loading recommendations...</p>
            ) : error ? (
                <p className="status-message error">{error}</p>
            ) : movies.length === 0 ? (
                <div className="empty-state">
                    <p>No recommendations available right now.</p>
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
    );
};

export default Recommendations;
