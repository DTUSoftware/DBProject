SET autocommit = 0;
START TRANSACTION;

CREATE DATABASE IF NOT EXISTS tidsmaskinen;
USE tidsmaskinen;

CREATE TABLE IF NOT EXISTS user
(
    email     varchar(254) PRIMARY KEY,
    firstname varchar(255) NOT NULL,
    lastname  varchar(255) NOT NULL,
    address   varchar(255) NOT NULL,
    birthdate date         NOT NULL,
    gender    boolean
);

CREATE TABLE IF NOT EXISTS sports_union
(
    ID           varchar(255) PRIMARY KEY,
    name         varchar(255) NOT NULL,
    email        varchar(254),
    address      varchar(255),
    phone_number varchar(32)
);

CREATE TABLE IF NOT EXISTS event_type
(
    ID VARCHAR(50) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS age_group
(
    lower_age INT NOT NULL,
    upper_age INT NOT NULL,
    PRIMARY KEY (lower_age, upper_age)
);

CREATE TABLE IF NOT EXISTS event_type_age_group
(
    event_type_id VARCHAR(50) NOT NULL,
    lower_age     INT         NOT NULL,
    upper_age     INT         NOT NULL,
    gender        BOOLEAN     NOT NULL,
    PRIMARY KEY (event_type_id, lower_age, upper_age, gender),
    FOREIGN KEY (event_type_id) REFERENCES event_type (ID),
    FOREIGN KEY (lower_age, upper_age) REFERENCES age_group (lower_age, upper_age)
);

CREATE TABLE IF NOT EXISTS event
(
    date          DATETIME     NOT NULL,
    union_id      varchar(255) NOT NULL,
    event_type_id VARCHAR(50)  NOT NULL,
    PRIMARY KEY (date, union_id, event_type_id),
    FOREIGN KEY (union_id) REFERENCES sports_union (ID),
    FOREIGN KEY (event_type_id) REFERENCES event_type (ID)
);

CREATE TABLE IF NOT EXISTS contender
(
    user_email      VARCHAR(254) NOT NULL,
    unique_event_id INT          NOT NULL,
    event_date      DATETIME     NOT NULL,
    union_id        varchar(255) NOT NULL,
    event_type_id   VARCHAR(50)  NOT NULL,
    time            INT,
    PRIMARY KEY (user_email, event_date, union_id, event_type_id),
    FOREIGN KEY (event_date, union_id, event_type_id) REFERENCES event (date, union_id, event_type_id),
    FOREIGN KEY (user_email) REFERENCES user (email)
);

DELIMITER //
CREATE TRIGGER IF NOT EXISTS check_date
    BEFORE INSERT
    ON user
    FOR EACH ROW
BEGIN
    IF NEW.birthdate > CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error: Your birthdate cannot be later than the current date';
    end if;
END //
DELIMITER ;

DELIMITER //
CREATE FUNCTION IF NOT EXISTS ageCalc (birthDate date) RETURNS INT
BEGIN
    DECLARE age INT;
    SET age = DATEDIFF(current_date, birthDate)/365;
    RETURN age;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE insert_user
END //
DELIMITER ;
CREATE VIEW IF NOT EXISTS results AS
SELECT event_type_age_group.gender, event_type_age_group.lower_age, event_type_age_group.upper_age, contender.user_email, contender.time, ageCalc(user.birthdate) as age
FROM event_type_age_group
INNER JOIN event
         ON event_type_age_group.event_type_id = event.event_type_id
INNER JOIN contender
         ON event.date = contender.event_date
             AND event.union_id = contender.union_id
             AND event.event_type_id = contender.event_type_id
INNER JOIN user
         ON contender.user_email = user.email
             AND user.gender = event_type_age_group.gender
WHERE
      contender.time IS NOT NULL
          && ageCalc(user.birthdate) >= event_type_age_group.lower_age
          && ageCalc(user.birthdate) <= event_type_age_group.upper_age
ORDER BY contender.time;

COMMIT;
SET autocommit = 1;
