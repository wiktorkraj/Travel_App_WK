-- Tworzenie tabeli kontynentów
CREATE TABLE continent (
                           continent_id SERIAL PRIMARY KEY,
                           continent_name VARCHAR(100) NOT NULL
);

-- Tworzenie tabeli krajów
CREATE TABLE country (
                         country_id SERIAL PRIMARY KEY,
                         country_name VARCHAR(100) NOT NULL,
                         continent_id INT REFERENCES continent(continent_id)
);

-- Tworzenie tabeli miast
CREATE TABLE city (
                      city_id SERIAL PRIMARY KEY,
                      city_name VARCHAR(100) NOT NULL,
                      country_id INT REFERENCES country(country_id)
);

-- Tworzenie tabeli lotnisk
CREATE TABLE airport (
                         airport_id SERIAL PRIMARY KEY,
                         airport_name VARCHAR(100) NOT NULL,
                         city_id INT REFERENCES city(city_id)
);

-- Tworzenie tabeli hoteli
CREATE TABLE hotel (
                       hotel_id SERIAL PRIMARY KEY,
                       hotel_name VARCHAR(100) NOT NULL,
                       standard_in_stars INT,
                       hotel_description TEXT,
                       city_id INT REFERENCES city(city_id)
);

-- Tworzenie tabeli podróżnych
CREATE TABLE traveler (
                          traveler_id SERIAL PRIMARY KEY,
                          traveler_first_name VARCHAR(100) NOT NULL,
                          traveler_last_name VARCHAR(100) NOT NULL,
                          age INT
);

-- Tworzenie tabeli zakupów podróży
CREATE TABLE trip_purchase (
                               trip_purchase_id SERIAL PRIMARY KEY,
                               trip_id INT REFERENCES trip(trip_id),
                               traveler_id INT REFERENCES traveler(traveler_id),
                               trip_price NUMERIC(10, 2) NOT NULL
);

-- Tworzenie tabeli podróży
CREATE TABLE trip (
                      trip_id SERIAL PRIMARY KEY,
                      city_id INT REFERENCES city(city_id),
                      airport_id INT REFERENCES airport(airport_id),
                      hotel_id INT REFERENCES hotel(hotel_id),
                      city_from_id INT REFERENCES city(city_id),
                      airport_from_id INT REFERENCES airport(airport_id),
                      city_to_id INT REFERENCES city(city_id),
                      airport_to_id INT REFERENCES airport(airport_id),
                      hotel_to_id INT REFERENCES hotel(hotel_id),
                      departure_date DATE,
                      return_date DATE,
                      trip_duration_in_days INT,
                      trip_type VARCHAR(10),
                      price_for_adult NUMERIC(10, 2),
                      price_for_kid NUMERIC(10, 2),
                      is_promoted BOOLEAN,
                      number_of_spots_for_adults INT,
                      number_of_spots_for_kids INT
);
