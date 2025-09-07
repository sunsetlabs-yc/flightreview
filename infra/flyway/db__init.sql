CREATE TABLE company (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at timestamptz DEFAULT now()
);

CREATE TABLE review (
  id UUID PRIMARY KEY,
  customer_name VARCHAR(255),
  customer_email VARCHAR(255),
  flight_number VARCHAR(50),
  flight_date DATE,
  origin VARCHAR(255),
  destination VARCHAR(255),
  rating INT,
  description TEXT,
  submitted_at timestamptz DEFAULT now(),
  state VARCHAR(50) NOT NULL,
  company_id UUID NULL,
  response_text TEXT,
  response_at timestamptz DEFAULT now()
);

CREATE INDEX idx_review_state ON review(state);
CREATE INDEX idx_review_flight ON review(flight_number);
CREATE INDEX idx_review_submitted_at ON review(submitted_at);