import React, { createContext, useState, useEffect, useContext, useMemo } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [loading, setLoading] = useState(true);
    
    // Caches for likes and watchlist to synchronize state instantly across components
    const [likes, setLikes] = useState([]);
    const [watchlist, setWatchlist] = useState([]);

    // Derived Sets for O(1) lookups
    const likedMovieIds = useMemo(() => new Set(likes.map(m => m.id)), [likes]);
    const watchlistMovieIds = useMemo(() => new Set(watchlist.map(m => m.id)), [watchlist]);

    const fetchInteractions = async () => {
        try {
            const [likesData, watchlistData] = await Promise.all([
                api.likes.get(),
                api.watchlist.get()
            ]);
            setLikes(likesData || []);
            setWatchlist(watchlistData || []);
        } catch (error) {
            console.error('Error fetching interactions:', error);
        }
    };

    // Auto-login check on app load
    useEffect(() => {
        const verifySession = async () => {
            if (token) {
                try {
                    const userData = await api.auth.me();
                    setUser(userData);
                    await fetchInteractions();
                } catch (error) {
                    console.error('Session verification failed, logging out:', error);
                    logout();
                }
            }
            setLoading(false);
        };
        verifySession();
    }, [token]);

    const login = async (email, password) => {
        setLoading(true);
        try {
            const data = await api.auth.login(email, password);
            localStorage.setItem('token', data.token);
            setToken(data.token);
            setUser(data.user);
            // Fetch interactions after logging in
            const [likesData, watchlistData] = await Promise.all([
                api.likes.get(),
                api.watchlist.get()
            ]);
            setLikes(likesData || []);
            setWatchlist(watchlistData || []);
            return data.user;
        } catch (error) {
            setLoading(false);
            throw error;
        }
    };

    const register = async (username, email, password) => {
        setLoading(true);
        try {
            const data = await api.auth.register(username, email, password);
            localStorage.setItem('token', data.token);
            setToken(data.token);
            setUser(data.user);
            setLikes([]);
            setWatchlist([]);
            return data.user;
        } catch (error) {
            setLoading(false);
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        setToken(null);
        setUser(null);
        setLikes([]);
        setWatchlist([]);
    };

    const toggleLike = async (movie) => {
        if (!user) return;
        const isLiked = likedMovieIds.has(movie.id);
        
        // Optimistic UI updates
        if (isLiked) {
            setLikes(prev => prev.filter(m => m.id !== movie.id));
            try {
                await api.likes.remove(movie.id);
            } catch (error) {
                console.error('Failed to remove like:', error);
                // Rollback on error
                setLikes(prev => [...prev, movie]);
            }
        } else {
            setLikes(prev => [...prev, movie]);
            try {
                await api.likes.add(movie.id);
            } catch (error) {
                console.error('Failed to add like:', error);
                // Rollback on error
                setLikes(prev => prev.filter(m => m.id !== movie.id));
            }
        }
    };

    const toggleWatchlist = async (movie) => {
        if (!user) return;
        const isAdded = watchlistMovieIds.has(movie.id);
        
        // Optimistic UI updates
        if (isAdded) {
            setWatchlist(prev => prev.filter(m => m.id !== movie.id));
            try {
                await api.watchlist.remove(movie.id);
            } catch (error) {
                console.error('Failed to remove from watchlist:', error);
                // Rollback on error
                setWatchlist(prev => [...prev, movie]);
            }
        } else {
            setWatchlist(prev => [...prev, movie]);
            try {
                await api.watchlist.add(movie.id);
            } catch (error) {
                console.error('Failed to add to watchlist:', error);
                // Rollback on error
                setWatchlist(prev => prev.filter(m => m.id !== movie.id));
            }
        }
    };

    const value = {
        user,
        token,
        loading,
        likes,
        watchlist,
        likedMovieIds,
        watchlistMovieIds,
        login,
        register,
        logout,
        toggleLike,
        toggleWatchlist,
        refreshInteractions: fetchInteractions
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
