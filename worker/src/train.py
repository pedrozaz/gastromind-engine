import os
import time
import json
import numpy as np
import pandas as pd
import redis
from sqlalchemy import create_engine, text
from sklearn.decomposition import TruncatedSVD
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy.sql.coercions import expect
from sqlalchemy.testing.config import db_url

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
REDIS_HOST = os.getenv("REDIS_HOST", "localhost")

print(f"--- GastroMind Worker Starting ---")

db_url = f"postgresql://admin:admin@{DB_HOST}:{DB_PORT}/gastromind"
print(f"Connecting to Postgres: {DB_HOST}:{DB_PORT}/gastromind")

try:
    engine = create_engine(db_url)
    with engine.connect() as conn:
        conn.execute(text("SELECT 1"))
    print("Database connected successfully")
except Exception as e:
    print(f"FATAL: Could not connect to database: {e}")
    exit(1)

print("Loading interactions...")
query = "SELECT user_id, restaurant_id, rating FROM interactions"
df = pd.read_sql(query, engine)

print(f"Data loaded: {len(df)} interactions.")

if len(df) < 10:
    print("ERROR: Not enough data to train. Check if DataSeeder ran correctly")
    exit(1)

print("Building pivot table...")
user_item_matrix = df.pivot_table(index='user_id', columns='restaurant_id', values='rating').fillna(0)
print(f"Matrix shape: {user_item_matrix.shape}")

print("Training SVD model...")
X = user_item_matrix.T
SVD = TruncatedSVD(n_components=12,random_state=42)
matrix_reduced = SVD.fit_transform(X)

print(f"Model trained. Reduced matrix shape: {matrix_reduced.shape}")

correlation_matrix = cosine_similarity(matrix_reduced)

print(f"Connecting to Redis at {REDIS_HOST}...")
try:
    r = redis.Redis(host=REDIS_HOST, port=6379, decode_responses=True)
    r.ping()
except Exception as e:
    print(f"FATAL: Could not connect to Redis: {e}")
    exit(1)

restaurant_ids = user_item_matrix.columns
count = 0

pipe = r.pipeline()
for i, restaurant_id in enumerate(restaurant_ids):
    correlation_vector = correlation_matrix[i]
    similar_indices = correlation_vector.argsort()[-6:-1][::-1]
    similar_ids = [restaurant_ids[idx] for idx in similar_indices]

    key = f"rec:restaurant:{restaurant_id}"
    pipe.set(key, json.dumps(similar_ids))
    count += 1

pipe.execute()
print(f"SUCCESS: Exported {count} recommendation vectors to Redis")
