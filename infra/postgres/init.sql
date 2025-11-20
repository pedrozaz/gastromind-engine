DROP TABLE IF EXISTS interactions;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS users;


CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE restaurants (
    restaurant_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    cuisine_type VARCHAR(50),
    price_range INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE interactions (
    interaction_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) REFERENCES users(user_id),
    restaurant_id VARCHAR(50) REFERENCES restaurants(restaurant_id),
    rating INT CHECK ( rating >= 1 AND rating <= 5 ),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_interactions_user ON interactions(user_id);
CREATE INDEX idx_interactions_rest ON interactions(restaurant_id);