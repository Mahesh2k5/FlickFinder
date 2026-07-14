import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const NavBar = ({ setSearchTerm }) => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleNavClick = (category) => {
        if (setSearchTerm) {
            setSearchTerm(category);
        }
    };

    const handleLogoutClick = () => {
        logout();
        navigate('/');
    };

    return (
        <nav className="navbar">
            <Link to="/" className="icon-link" onClick={() => handleNavClick('marvel')}>
                <div className="icon">
                    <h1>Flick Finder</h1>
                </div>
            </Link>
            <div className="navs">
                <Link to="/" className="nav-link" onClick={() => handleNavClick('marvel')}>
                    Home
                </Link>
                <Link to="/tvshows" className="nav-link" onClick={() => handleNavClick('series')}>
                    TV Shows
                </Link>
                <Link to="/movies" className="nav-link" onClick={() => handleNavClick('movie')}>
                    Movies
                </Link>
                {user && (
                    <>
                        <Link to="/recommendations" className="nav-link">
                            For You
                        </Link>
                        <Link to="/similar" className="nav-link">
                            Find Similar
                        </Link>
                    </>
                )}
            </div>
            
            <div className="nav-auth">
                {user ? (
                    <div className="nav-user-menu">
                        <Link to="/profile" className="nav-user-profile">
                            <span className="user-avatar-small">
                                {user.username.charAt(0).toUpperCase()}
                            </span>
                            <span className="user-name-label">{user.username}</span>
                        </Link>
                        <button className="logout-btn" onClick={handleLogoutClick}>
                            Sign Out
                        </button>
                    </div>
                ) : (
                    <div className="nav-auth-links">
                        <Link to="/login" className="login-link-btn">Sign In</Link>
                        <Link to="/register" className="register-link-btn">Register</Link>
                    </div>
                )}
            </div>
        </nav>
    );
};

export default NavBar;