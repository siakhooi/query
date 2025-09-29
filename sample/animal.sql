DROP DATABASE IF EXISTS animaldb;
DROP USER IF EXISTS  animaluser;

CREATE USER animaluser WITH PASSWORD 'password123';

CREATE DATABASE animaldb WITH OWNER = animaluser;

GRANT ALL PRIVILEGES ON DATABASE animaldb TO animaluser;

\c animaldb

CREATE TABLE animals (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    species VARCHAR(50),
    age INT,
    habitat VARCHAR(50),
    diet VARCHAR(50)
);

INSERT INTO animals (name, species, age, habitat, diet) VALUES
('Leo', 'Lion', 5, 'Savannah', 'Carnivore'),
('Ellie', 'Elephant', 10, 'Forest', 'Herbivore'),
('Stripes', 'Tiger', 4, 'Jungle', 'Carnivore'),
('Manny', 'Manatee', 7, 'River', 'Herbivore'),
('Polly', 'Parrot', 2, 'Rainforest', 'Omnivore'),
('George', 'Giraffe', 6, 'Savannah', 'Herbivore'),
('Zara', 'Zebra', 3, 'Savannah', 'Herbivore'),
('Rex', 'Wolf', 4, 'Forest', 'Carnivore'),
('Bubbles', 'Dolphin', 8, 'Ocean', 'Carnivore'),
('Spike', 'Porcupine', 5, 'Forest', 'Herbivore'),
('Nemo', 'Clownfish', 1, 'Coral Reef', 'Omnivore'),
('Koko', 'Gorilla', 9, 'Jungle', 'Herbivore'),
('Sammy', 'Snake', 2, 'Desert', 'Carnivore'),
('Hopper', 'Kangaroo', 6, 'Grassland', 'Herbivore'),
('Fluffy', 'Rabbit', 1, 'Meadow', 'Herbivore'),
('Oscar', 'Owl', 3, 'Forest', 'Carnivore'),
('Tina', 'Tortoise', 50, 'Desert', 'Herbivore'),
('Perry', 'Penguin', 4, 'Antarctica', 'Carnivore'),
('Daisy', 'Deer', 5, 'Forest', 'Herbivore'),
('Max', 'Monkey', 7, 'Jungle', 'Omnivore');

GRANT SELECT ON animals TO animaluser;
