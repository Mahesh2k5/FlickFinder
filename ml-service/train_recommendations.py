import json
import numpy as np
import pandas as pd
import mysql.connector
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import sys
import os

# Database Configuration
DB_HOST = os.environ.get("DB_HOST", "localhost")
DB_USER = os.environ.get("DB_USER", "root")
DB_PASSWORD = os.environ.get("DB_PASSWORD", "umamahesh@2005")
DB_NAME = os.environ.get("DB_NAME", "movielist")
DB_PORT = os.environ.get("DB_PORT", "3306")

def connect_db():
    try:
        return mysql.connector.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
            port=int(DB_PORT)
        )
    except Exception as e:
        print(f"Error connecting to database: {e}")
        sys.exit(1)

def extract_names(json_str):
    if not json_str: return ""
    try:
        data = json.loads(json_str)
        if isinstance(data, list):
            return " ".join([item.get('name', '') if isinstance(item, dict) else str(item) for item in data])
        return ""
    except:
        return ""

def main():
    print("Connecting to MySQL Database...")
    db = connect_db()
    cursor = db.cursor(dictionary=True)
    
    # 1. Fetch Movies
    print("Fetching movies from database...")
    cursor.execute("SELECT id, title, overview, genres, keywords, movie_cast, director FROM movies")
    movies = cursor.fetchall()
    
    if not movies:
        print("No movies found in database. Exiting.")
        return
        
    df = pd.DataFrame(movies)
    
    # 2. Preprocess Text Data
    print("Preprocessing movie attributes...")
    df['genres'] = df['genres'].apply(extract_names)
    df['keywords'] = df['keywords'].apply(extract_names)
    df['movie_cast'] = df['movie_cast'].apply(extract_names)
    df['director'] = df['director'].fillna("")
    df['overview'] = df['overview'].fillna("")
    
    # Create a 'combined_features' string for each movie
    df['combined_features'] = df['genres'] + " " + df['keywords'] + " " + df['movie_cast'] + " " + df['director'] + " " + df['overview']
    
    # 3. Calculate TF-IDF & Cosine Similarity
    print("Calculating TF-IDF vectors...")
    tfidf = TfidfVectorizer(stop_words='english')
    tfidf_matrix = tfidf.fit_transform(df['combined_features'])
    
    print("Calculating Cosine Similarities...")
    similarity = cosine_similarity(tfidf_matrix)
    
    # 4. Save Recommendations back to the Database
    print("Clearing old recommendations...")
    cursor.execute("DELETE FROM movie_recommendations")
    db.commit()
    
    print("Saving new recommendations to database...")
    insert_query = "INSERT INTO movie_recommendations (movie_id, recommended_movie_id, score) VALUES (%s, %s, %s)"
    recommendations_data = []
    
    for idx, row in df.iterrows():
        movie_id = row['id']
        # Get similar movie indices, sorted descending by similarity score
        similar_indices = similarity[idx].argsort()[::-1]
        
        # Save top 10 similar movies (excluding itself)
        top_k = 0
        for i in similar_indices:
            if i != idx:
                rec_movie_id = df.iloc[i]['id']
                score = similarity[idx][i]
                recommendations_data.append((int(movie_id), int(rec_movie_id), float(score)))
                top_k += 1
                if top_k >= 10:
                    break
                    
    cursor.executemany(insert_query, recommendations_data)
    db.commit()
    
    print(f"Successfully computed and stored {len(recommendations_data)} movie recommendations!")
    
    cursor.close()
    db.close()

if __name__ == "__main__":
    main()
