import { useState, useEffect } from 'react'
import './App.css'
import NavBar from './Components/NavBar'
import Content from './Components/Content'
import { HashRouter as Router, Route, Routes } from 'react-router-dom';
import MovieGrid from './Components/MovieGrid'
import { AuthProvider } from './context/AuthContext'
import Login from './pages/Login'
import Register from './pages/Register'
import Profile from './pages/Profile'
import Recommendations from './pages/Recommendations'
import SimilarSearch from './pages/SimilarSearch'
import MovieDetailModal from './Components/MovieDetailModal'
import { api } from './api/client'

function AppContent() {
  const [searchTerm, setSearchTerm] = useState('marvel');
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedMovie, setSelectedMovie] = useState(null);

  useEffect(() => {
    const fetchMovies = async () => {
      setLoading(true);
      setError(null);
      try {
        let data;
        if (searchTerm && searchTerm.trim() !== '') {
          data = await api.movies.search(searchTerm);
        } else {
          data = await api.movies.getTrending();
        }
        setMovies(data || []);
      } catch (err) {
        console.error('Error fetching movies from API:', err);
        setError(err);
        setMovies([]);
      } finally {
        setLoading(false);
      }
    };

    fetchMovies();
  }, [searchTerm]);

  return (
    <Router>
      <div className="app">
        <NavBar setSearchTerm={setSearchTerm} />
        <hr />
        <Routes>
          <Route
            path="/"
            element={
              <Content 
                searchTerm={searchTerm} 
                setSearchTerm={setSearchTerm} 
                movies={movies} 
                loading={loading} 
                error={error} 
                onCardClick={setSelectedMovie}
              />
            }
          />
          <Route
            path="/tvshows"
            element={
              <MovieGrid 
                movies={movies} 
                setSearchTerm={setSearchTerm} 
                loading={loading} 
                error={error} 
                onCardClick={setSelectedMovie}
              />
            }
          />
          <Route
            path="/movies"
            element={
              <MovieGrid 
                movies={movies} 
                setSearchTerm={setSearchTerm} 
                loading={loading} 
                error={error} 
                onCardClick={setSelectedMovie}
              />
            }
          />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/profile" element={<Profile onCardClick={setSelectedMovie} />} />
          <Route path="/recommendations" element={<Recommendations onCardClick={setSelectedMovie} />} />
          <Route path="/similar" element={<SimilarSearch onCardClick={setSelectedMovie} />} />
        </Routes>

        {selectedMovie && (
          <MovieDetailModal 
            movie={selectedMovie} 
            onClose={() => setSelectedMovie(null)} 
            onSelectMovie={setSelectedMovie}
          />
        )}
      </div>
    </Router>
  )
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}

export default App;
