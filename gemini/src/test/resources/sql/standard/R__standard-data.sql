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
    `numberOfLabCoats` INTEGER      NOT NULL DEFAULT 0
);

INSERT INTO `doctor` (id, name, age, dog, numberOfLabCoats)
VALUES (1, 'Tom', 55, NULL, 4),
       (2, 'Hank', 42, 'Spot', 1),
       (3, 'Moe', 44, 'Poppy', 10),
       (4, 'Phil', 30, 'Cat', 3),
       (5, 'Richie', 63, NULL, 1),
       (6, 'Moe', 48, 'Spot', 5),
       (7, 'Jason', 26, NULL, 1),
       (8, 'Moe', 40, 'Poppy', 3),
       (9, 'Stephanie', 58, 'Poppy', 1);

CREATE TABLE `lawyer`
(
    `id`            INTEGER AUTO_INCREMENT PRIMARY KEY,
    `name`          VARCHAR(255) NOT NULL,
    `age`           INTEGER      NOT NULL,
    `dog`           VARCHAR(255) NULL,
    `numberOfSuits` INTEGER      NOT NULL DEFAULT 0
);

INSERT INTO `lawyer` (id, name, age, dog, numberOfSuits)
VALUES (1, 'Bart', 20, 'May', 7),
       (2, 'Jim', 31, 'Flower', 2),
       (3, 'Joe', 38, NULL, 3),
       (4, 'Sally', 25, 'Cupcake', 4),
       (5, 'Moe', 24, NULL, 7),
       (6, 'Alex', 38, NULL, 1),
       (7, 'Butch', 70, 'Tails', 2);