CREATE DATABASE IF NOT EXISTS tidsmaskinen;
USE tidsmaskinen;

CREATE TABLE IF NOT EXISTS user (
    email varchar(254) PRIMARY KEY,
    firstname varchar(255) NOT NULL,
    lastname varchar(255) NOT NULL,
    address varchar(255) NOT NULL,
    birthdate date NOT NULL,
    gender boolean
);

CREATE TABLE IF NOT EXISTS sports_union (
    ID varchar(255) PRIMARY KEY,
    name varchar(255) NOT NULL,
    email varchar(254),
    address varchar(255),
    phone_number varchar(32)
);

CREATE TABLE IF NOT EXISTS event_type (
    ID VARCHAR(50) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS age_group (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    lower_age INT NOT NULL,
    upper_age INT NOT NULL
);

CREATE TABLE IF NOT EXISTS event_type_age_group (
    event_type_id VARCHAR(50) NOT NULL,
    age_group_id INT NOT NULL,
    gender boolean,
    PRIMARY KEY (event_type_id, age_group_id),
    FOREIGN KEY (event_type_id) REFERENCES event_type(ID),
    FOREIGN KEY (age_group_id) REFERENCES age_group(ID)
);

CREATE TABLE IF NOT EXISTS event (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    union_id varchar(255) NOT NULL,
    event_type_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (union_id) REFERENCES sports_union(ID),
    FOREIGN KEY (event_type_id) REFERENCES event_type(ID)
);

CREATE TABLE IF NOT EXISTS contender (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_email VARCHAR(254) NOT NULL,
    time INT,
    FOREIGN KEY (event_id) REFERENCES event(ID),
    FOREIGN KEY (user_email) REFERENCES user(email)
);
