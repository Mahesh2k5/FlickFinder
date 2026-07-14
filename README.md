# Flick Finder

A full-stack movie recommendation system built with **React**, **Spring Boot**, **MySQL**, and **Python**.

This project provides a personalized movie discovery experience. It allows users to search for movies, like and add movies to a watchlist, and receive personalized recommendations using a machine learning model that calculates TF-IDF cosine similarity.

## 🏗 Architecture Overview

The system is designed with simplicity and scalability in mind:

1. **Frontend (React + Vite)**: A modern, responsive UI with smooth animations.
2. **Backend (Spring Boot)**: A robust REST API that handles user authentication, connects to the TMDB API for movie metadata, and manages the MySQL database.
3. **Database (MySQL)**: Stores user accounts, interactions (likes/watchlists), cached movie metadata, and ML recommendation scores.
4. **Machine Learning Pipeline (Python)**: An offline batch script (`train_recommendations.py`) that uses `scikit-learn` to compute a cosine-similarity matrix based on movie genres, keywords, cast, and directors. It populates the database with pre-computed recommendations.

## 🚀 Running Locally

### 1. Database Setup
Ensure you have MySQL installed and running locally. Create a database named `movielist`. The application is configured to connect using the username `root` and password `umamahesh@2005` (configurable in `backend/src/main/resources/application.yml`).

### 2. Backend (Spring Boot)
1. Navigate to the `backend/` folder: `cd backend`
2. Run the application: `mvn clean spring-boot:run`
3. The server will start on `http://localhost:8080`.
*(Note: On the very first startup, the backend will automatically seed the database with popular movies from TMDB).*

### 3. Frontend (React)
1. Navigate to the `frontend/` folder: `cd frontend`
2. Install dependencies: `npm install`
3. Start the dev server: `npm run dev`
4. Access the UI at `http://localhost:5173`.

### 4. Running the ML Recommendation Script
The ML script computes similarities for the "For You" page. It is meant to be run periodically (e.g., as a nightly Cron job).
1. Navigate to the `ml-service/` folder: `cd ml-service`
2. Install requirements: `pip install -r requirements.txt`
3. Run the script: `python train_recommendations.py`
4. The script will read all movies in the MySQL database, calculate their similarities, and save the top recommendations back to the database.

## 📦 Deployment

The project is structured to be easily deployed on modern platforms:
- **Frontend**: Can be deployed to Vercel, Netlify, or Render.
- **Backend**: Can be deployed as a Docker container or Native Java app on Render, Heroku, or AWS.
- **ML Script**: A GitHub Action is included in `.github/workflows/ml-batch-job.yml` to automatically run the recommendation script every night against your production database using GitHub Secrets.
