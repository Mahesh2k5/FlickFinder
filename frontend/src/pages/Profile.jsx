import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

const Profile = ({ onCardClick }) => {
    const { user, likes, watchlist } = useAuth();
    const [activeTab, setActiveTab] = useState('likes'); // 'likes' or 'watchlist'

    if (!user) {
        return (
            <div className="profile-container">
                <div className="status-card">
                    <p>Please log in to view your profile.</p>
                </div>
            </div>
        );
    }

    const currentList = activeTab === 'likes' ? likes : watchlist;

    return (
        <div className="profile-container">
            <div className="profile-header">
                <div className="profile-avatar">
                    {user.username.charAt(0).toUpperCase()}
                </div>
                <div className="profile-info-block">
                    <h2>{user.username}</h2>
                    <p className="profile-email">{user.email}</p>
                    <p className="profile-meta">Member since {new Date(user.createdAt || Date.now()).toLocaleDateString()}</p>
                </div>
            </div>

            <div className="profile-tabs">
                <button 
                    className={`profile-tab ${activeTab === 'likes' ? 'active' : ''}`}
                    onClick={() => setActiveTab('likes')}
                >
                    Liked Movies ({likes.length})
                </button>
                <button 
                    className={`profile-tab ${activeTab === 'watchlist' ? 'active' : ''}`}
                    onClick={() => setActiveTab('watchlist')}
                >
                    Watchlist ({watchlist.length})
                </button>
            </div>

            <div className="profile-content">
                {currentList.length === 0 ? (
                    <div className="empty-state">
                        <p>No movies in this list yet.</p>
                        <p className="empty-subtext">Explore films on the Home screen to add them here.</p>
                    </div>
                ) : (
                    <div className="movie-grid">
                        {currentList.map((movie) => (
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

export default Profile;
