import React, { useState } from "react";
import MovieGrid from "./MovieGrid";

const Content = ({ movies, loading, error, setSearchTerm, onCardClick }) => {
    const [showWelcome, setShowWelcome] = useState(true);

    const handleDiscoverClick = () => {
        setShowWelcome(false);
    };

    return (
        <div className="content">
            {showWelcome ? (
                <div className="welcome-section">
                    <h1>Bringing the Big Screen to your Home</h1>
                    <p>Discover, critique and celebrate movies with honest reviews and fresh takes on every film.</p>
                    <button 
                        className="discover" 
                        onClick={handleDiscoverClick}
                    >
                        Discover Films here...
                    </button>
                    {loading && <p className="status-message">Loading...</p>}
                    {error && <p className="status-message error">{error.message}</p>}
                </div>
            ) : (
                <div className="movies-section">
                    <MovieGrid 
                        movies={movies} 
                        setSearchTerm={setSearchTerm}
                        onCardClick={onCardClick}
                    />
                    {loading && <p className="status-message">Loading...</p>}
                    {error && <p className="status-message error">{error.message}</p>}
                </div>
            )}
        </div>
    );
};

export default Content;