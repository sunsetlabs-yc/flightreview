CREATE TABLE company (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at timestamptz DEFAULT now()
);

CREATE TABLE flight (
  id UUID PRIMARY KEY,
  flight_number VARCHAR(50) NOT NULL,
  company_name VARCHAR(255) NOT NULL,
  origin VARCHAR(255) NOT NULL,
  destination VARCHAR(255) NOT NULL,
  flight_date DATE NOT NULL,
  created_at timestamptz DEFAULT now()
);

CREATE TABLE review (
  id UUID PRIMARY KEY,
  customer_name VARCHAR(255),
  customer_email VARCHAR(255),
  flight_number VARCHAR(50),
  rating INT,
  description TEXT,
  submitted_at timestamptz DEFAULT now(),
  state VARCHAR(50) NOT NULL,
  company_name VARCHAR(255),
  response_text TEXT,
  response_at timestamptz DEFAULT now()
);

CREATE INDEX idx_review_state ON review(state);
CREATE INDEX idx_review_flight_number ON review(flight_number);
CREATE INDEX idx_review_submitted_at ON review(submitted_at);
CREATE INDEX idx_flight_company ON flight(company_name);
CREATE INDEX idx_flight_date ON flight(flight_date);

-- Insert sample companies
INSERT INTO company (id, name, email, password_hash) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Air France', 'admin@airfrance.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'),
('550e8400-e29b-41d4-a716-446655440002', 'Lufthansa', 'admin@lufthansa.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'),
('550e8400-e29b-41d4-a716-446655440003', 'British Airways', 'admin@britishairways.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'),
('550e8400-e29b-41d4-a716-446655440004', 'KLM Royal Dutch Airlines', 'admin@klm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'),
('550e8400-e29b-41d4-a716-446655440005', 'Emirates', 'admin@emirates.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi');

-- Insert sample flights
INSERT INTO flight (id, flight_number, company_name, origin, destination, flight_date) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'AF123', 'Air France', 'Paris CDG', 'New York JFK', '2024-01-15'),
('660e8400-e29b-41d4-a716-446655440002', 'AF456', 'Air France', 'Paris CDG', 'Tokyo Narita', '2024-01-16'),
('660e8400-e29b-41d4-a716-446655440003', 'AF789', 'Air France', 'Paris CDG', 'Dubai', '2024-01-17'),
('660e8400-e29b-41d4-a716-446655440004', 'AF321', 'Air France', 'Paris CDG', 'London Heathrow', '2024-01-18'),
('660e8400-e29b-41d4-a716-446655440005', 'AF654', 'Air France', 'Paris CDG', 'Sydney', '2024-01-19'),
('660e8400-e29b-41d4-a716-446655440006', 'LH101', 'Lufthansa', 'Frankfurt', 'New York JFK', '2024-01-20'),
('660e8400-e29b-41d4-a716-446655440007', 'LH202', 'Lufthansa', 'Frankfurt', 'Los Angeles', '2024-01-21'),
('660e8400-e29b-41d4-a716-446655440008', 'LH303', 'Lufthansa', 'Frankfurt', 'Singapore', '2024-01-22'),
('660e8400-e29b-41d4-a716-446655440009', 'LH404', 'Lufthansa', 'Frankfurt', 'Bangkok', '2024-01-23'),
('660e8400-e29b-41d4-a716-446655440010', 'LH505', 'Lufthansa', 'Frankfurt', 'Hong Kong', '2024-01-24'),
('660e8400-e29b-41d4-a716-446655440011', 'BA111', 'British Airways', 'London Heathrow', 'New York JFK', '2024-01-25'),
('660e8400-e29b-41d4-a716-446655440012', 'BA222', 'British Airways', 'London Heathrow', 'Dubai', '2024-01-26'),
('660e8400-e29b-41d4-a716-446655440013', 'BA333', 'British Airways', 'London Heathrow', 'Singapore', '2024-01-27'),
('660e8400-e29b-41d4-a716-446655440014', 'BA444', 'British Airways', 'London Heathrow', 'Sydney', '2024-01-28'),
('660e8400-e29b-41d4-a716-446655440015', 'BA555', 'British Airways', 'London Heathrow', 'Tokyo Narita', '2024-01-29'),
('660e8400-e29b-41d4-a716-446655440016', 'KL666', 'KLM Royal Dutch Airlines', 'Amsterdam', 'New York JFK', '2024-01-30'),
('660e8400-e29b-41d4-a716-446655440017', 'KL777', 'KLM Royal Dutch Airlines', 'Amsterdam', 'Dubai', '2024-01-31'),
('660e8400-e29b-41d4-a716-446655440018', 'KL888', 'KLM Royal Dutch Airlines', 'Amsterdam', 'Singapore', '2024-02-01'),
('660e8400-e29b-41d4-a716-446655440019', 'EK999', 'Emirates', 'Dubai', 'New York JFK', '2024-02-02'),
('660e8400-e29b-41d4-a716-446655440020', 'EK000', 'Emirates', 'Dubai', 'London Heathrow', '2024-02-03');