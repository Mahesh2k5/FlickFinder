import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';

const MovieDetailModal = ({ movie, onClose, onSelectMovie }) => {
    const { user, likedMovieIds, watchlistMovieIds, toggleLike, toggleWatchlist } = useAuth();
    const [similarMovies, setSimilarMovies] = useState([]);
    const [loadingSimilar, setLoadingSimilar] = useState(false);

    useEffect(() => {
        if (!movie) return;

        const fetchSimilar = async () => {
            setLoadingSimilar(true);
            try {
                const data = await api.recommendations.getSimilar(movie.id);
                setSimilarMovies(data || []);
            } catch (error) {
                console.error('Error fetching similar movies:', error);
                setSimilarMovies([]);
            } finally {
                setLoadingSimilar(false);
            }
        };

        fetchSimilar();
    }, [movie]);

    if (!movie) return null;

    // Helper to safely parse JSON strings from backend DTO (genres, cast, keywords)
    const parseJsonField = (strVal, fallback = []) => {
        try {
            if (!strVal) return fallback;
            const parsed = JSON.parse(strVal);
            if (Array.isArray(parsed)) {
                return parsed.map(item => typeof item === 'object' ? (item.name || item.toString()) : item);
            }
            return fallback;
        } catch {
            return fallback;
        }
    };

    const genres = parseJsonField(movie.genres);
    const cast = parseJsonField(movie.cast);
    
    const isLiked = likedMovieIds.has(movie.id);
    const isWatchlisted = watchlistMovieIds.has(movie.id);

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close-btn" onClick={onClose}>&times;</button>
                
                <div className="modal-body-layout">
                    <div className="modal-poster-col">
                        <img 
                            src={movie.posterUrl || 'https://via.placeholder.com/300x450?text=No+Poster'} 
                            alt={movie.title} 
                            className="modal-poster"
                        />
                        {user && (
                            <div className="modal-actions">
                                <button 
                                    className={`action-btn-like ${isLiked ? 'active' : ''}`}
                                    onClick={() => toggleLike(movie)}
                                    title={isLiked ? "Unlike" : "Like"}
                                >
                                    {isLiked ? '❤️ Liked' : '🤍 Like'}
                                </button>
                                <button 
                                    className={`action-btn-watchlist ${isWatchlisted ? 'active' : ''}`}
                                    onClick={() => toggleWatchlist(movie)}
                                    title={isWatchlisted ? "Remove from watchlist" : "Add to watchlist"}
                                >
                                    {isWatchlisted ? '🔖 Saved' : '➕ Watchlist'}
                                </button>
                            </div>
                        )}
                    </div>
                    
                    <div className="modal-details-col">
                        <h2 className="modal-title">{movie.title}</h2>
                        <p className="modal-year">{movie.year}</p>
                        
                        {genres.length > 0 && (
                            <div className="modal-genres">
                                {genres.map(genre => (
                                    <span key={genre} className="genre-badge">{genre}</span>
                                ))}
                            </div>
                        )}
                        
                        <div className="modal-info-section">
                            {movie.director && (
                                <p><strong>Director:</strong> {movie.director}</p>
                            )}
                            {cast.length > 0 && (
                                <p><strong>Starring:</strong> {cast.join(', ')}</p>
                            )}
                        </div>
                        
                        <div className="modal-overview">
                            <h3>Overview</h3>
                            <p>{movie.overview || 'No description available for this title.'}</p>
                        </div>
                    </div>
                </div>

                <div className="modal-recommendations-section">
                    <h3>Similar Movies (ML Computed)</h3>
                    {loadingSimilar ? (
                        <p className="modal-status">Loading recommendations...</p>
                    ) : similarMovies.length === 0 ? (
                        <p className="modal-status empty">No similar movies found in catalog.</p>
                    ) : (
                        <div className="modal-similar-list">
                            {similarMovies.map(simMovie => (
                                <div 
                                    key={simMovie.id} 
                                    className="modal-similar-card"
                                    onClick={() => onSelectMovie(simMovie)}
                                >
                                    <img 
                                        src={simMovie.posterUrl || 'https://via.placeholder.com/150x225?text=No+Image'} 
                                        alt={simMovie.title} 
                                    />
                                    <h4>{simMovie.title}</h4>
                                    <p>{simMovie.year}</p>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default MovieDetailModal;
