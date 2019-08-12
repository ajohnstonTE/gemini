CREATE TABLE `house`
(
    `id`     INTEGER AUTO_INCREMENT PRIMARY KEY,
    `cityId` INTEGER      NOT NULL,
    `owner`  VARCHAR(255) NOT NULL,
    `dog`    VARCHAR(255)
);

INSERT INTO `house` (id, cityId, owner, dog)
VALUES (1, 3, 'Tom', 'Itchy'),
       (2, 5, 'Hank', 'Scratchy'),
       (3, 8, 'Joe', 'Poppy'),
       (4, 10, 'Boe', 'Cat'),
       (5, 7, 'Boe', 'Cow'),
       (6, 10, 'Moe', 'Poppy'),
       (7, 5, 'Boe', 'Spot'),
       (8, 7, 'John', 'Cow'),
       (9, 11, 'Moe', 'Poppy'),
       (10, 12, 'Moe', 'Cupcake'),
       (11, 11, 'Moe', 'Poppy');


CREATE TABLE `doctor`
(
    `id`               INTEGER AUTO_INCREMENT PRIMARY KEY,
    `name`             VARCHAR(255) NOT NULL,
    `age`              INTEGER      NOT NULL,
    `dog`              VARCHAR(255) NULL,
    `numberOfLabCoats` INTEGER      NOT NULL DEFAULT 0,
    `salary`           BIGINT       NOT NULL DEFAULT 0
);

INSERT INTO `doctor` (id, name, age, dog, numberOfLabCoats, salary)
VALUES (1, 'Tom', 55, NULL, 4, 500000),
       (2, 'Hank', 42, 'Spot', 1, 1000000),
       (3, 'Moe', 44, 'Poppy', 10, 202000),
       (4, 'Phil', 30, 'Cat', 3, 1000000),
       (5, 'Richie', 63, NULL, 1, 1000),
       (6, 'Moe', 48, 'Spot', 5, 0),
       (7, 'Jason', 26, NULL, 1, 7000),
       (8, 'Moe', 40, 'Poppy', 3, 82000),
       (9, 'Stephanie', 58, 'Poppy', 1, 1000000);

CREATE TABLE `lawyer`
(
    `id`            INTEGER AUTO_INCREMENT PRIMARY KEY,
    `name`          VARCHAR(255) NOT NULL,
    `age`           INTEGER      NOT NULL,
    `dog`           VARCHAR(255) NULL,
    `numberOfSuits` INTEGER      NOT NULL DEFAULT 0,
    `salary`           BIGINT       NOT NULL DEFAULT 0
);

INSERT INTO `lawyer` (id, name, age, dog, numberOfSuits, salary)
VALUES (1, 'Bart', 20, 'May', 7, 330000),
       (2, 'Jim', 31, 'Flower', 2, 24000),
       (3, 'Joe', 38, NULL, 3, 508000),
       (4, 'Sally', 25, 'Cupcake', 4, 53200),
       (5, 'Moe', 24, NULL, 7, 90100),
       (6, 'Alex', 38, NULL, 1, 136000),
       (7, 'Butch', 70, 'Tails', 2, 405000);