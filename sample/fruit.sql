CREATE DATABASE IF NOT EXISTS fruitdb;

CREATE USER IF NOT EXISTS 'fruituser'@'%' IDENTIFIED BY 'fruitpass';
GRANT ALL PRIVILEGES ON fruitdb.* TO 'fruituser'@'%';

USE fruitdb;

DROP TABLE IF EXISTS fruits;
CREATE TABLE fruits (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  color VARCHAR(255),
  taste VARCHAR(255),
  season VARCHAR(255),
  origin VARCHAR(255)
);


INSERT INTO fruits (name, color, taste, season, origin) VALUES
('Apple', 'Red', 'Sweet', 'Autumn', 'Central Asia'),
('Banana', 'Yellow', 'Sweet', 'Summer', 'Southeast Asia'),
('Orange', 'Orange', 'Sweet', 'Winter', 'Southeast Asia'),
('Grape', 'Purple', 'Sweet', 'Autumn', 'Middle East'),
('Strawberry', 'Red', 'Sweet', 'Spring', 'Europe'),
('Mango', 'Yellow', 'Sweet', 'Summer', 'South Asia'),
('Watermelon', 'Green', 'Sweet', 'Summer', 'Northeast Africa'),
('Pineapple', 'Yellow', 'Sweet and Tart', 'Summer', 'South America'),
('Blueberry', 'Blue', 'Sweet', 'Summer', 'North America'),
('Raspberry', 'Red', 'Sweet and Tart', 'Summer', 'Europe');
