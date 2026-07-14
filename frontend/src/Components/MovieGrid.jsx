import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';

const MovieGrid = ({ movies, setSearchTerm, onCardClick }) => {
    const { user, likedMovieIds, watchlistMovieIds, toggleLike, toggleWatchlist } = useAuth();
    
    const categories = [
        { name: 'Marvel', query: 'marvel' },
        { name: 'Action', query: 'action' },
        { name: 'Crime', query: 'crime' },
        { name: 'Comedy', query: 'comedy' },
        { name: 'Drama', query: 'drama' },
        { name: 'Horror', query: 'horror' }
    ];

    const [inputValue, setInputValue] = useState('');

    useEffect(() => {
        const handler = setTimeout(() => {
            if (inputValue !== '') {
                setSearchTerm(inputValue);
            }
        }, 500);

        return () => {
            clearTimeout(handler);
        };
    }, [inputValue, setSearchTerm]);

    const handleSearch = (event) => {
        setInputValue(event.target.value);
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter' && inputValue !== '') {
            setSearchTerm(inputValue);
        }
    };

    return (
        <div className="movie-container">
            <div className="search-section">
                <div className="type">
                    {categories.map((category) => (
                        <button
                            key={category.query}
                            onClick={() => {
                                setInputValue('');
                                setSearchTerm(category.query);
                            }}
                        >
                            {category.name}
                        </button>
                    ))}
                </div>
                <input
                    placeholder="Search for a Movie"
                    type="text"
                    className="search"
                    value={inputValue}
                    onChange={handleSearch}
                    onKeyDown={handleKeyDown}
                />
            </div>
            
            <div className="movie-grid">
                {movies.map((movie) => {
                    const movieId = movie.id || movie.imdbID;
                    const poster = movie.posterUrl || movie.Poster;
                    const title = movie.title || movie.Title;
                    const year = movie.year || movie.Year;
                    
                    const isLiked = likedMovieIds.has(movie.id);
                    const isWatchlisted = watchlistMovieIds.has(movie.id);

                    return (
                        <div 
                            key={movieId} 
                            className="movie-card"
                            onClick={() => onCardClick && onCardClick(movie)}
                        >
                            <div className="movie-poster">
                                <img 
                                    src={poster && poster !== "N/A" ? poster : 'https://via.placeholder.com/300x450?text=No+Image'} 
                                    alt={title} 
                                />
                                {user && (
                                    <div className="card-overlay-actions">
                                        <button 
                                            className={`card-action-btn ${isLiked ? 'liked' : ''}`}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                toggleLike(movie);
                                            }}
                                        >
                                            {isLiked ? '❤️' : '🤍'}
                                        </button>
                                        <button 
                                            className={`card-action-btn ${isWatchlisted ? 'saved' : ''}`}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                toggleWatchlist(movie);
                                            }}
                                        >
                                            {isWatchlisted ? '🔖' : '➕'}
                                        </button>
                                    </div>
                                )}
                            </div>
                            <div className="movie-info">
                                <h3>{title}</h3>
                                <p>{year}</p>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default MovieGrid;