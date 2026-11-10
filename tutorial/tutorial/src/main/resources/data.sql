INSERT INTO category(name) VALUES ('Eurogames');
INSERT INTO category(name) VALUES ('Ameritrash');
INSERT INTO category(name) VALUES ('Familiar');

INSERT INTO author(name, nationality) VALUES ('Alan R. Moon', 'US');
INSERT INTO author(name, nationality) VALUES ('Vital Lacerda', 'PT');
INSERT INTO author(name, nationality) VALUES ('Simone Luciani', 'IT');
INSERT INTO author(name, nationality) VALUES ('Perepau Llistosella', 'ES');
INSERT INTO author(name, nationality) VALUES ('Michael Kiesling', 'DE');
INSERT INTO author(name, nationality) VALUES ('Phil Walker-Harding', 'US');

INSERT INTO game(title, age, category_id, author_id) VALUES ('On Mars', '14', 1, 2);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Aventureros al tren', '8', 3, 1);
INSERT INTO game(title, age, category_id, author_id) VALUES ('1920: Wall Street', '12', 1, 4);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Barrage', '14', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Los viajes de Marco Polo', '12', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Azul', '8', 3, 5);

INSERT INTO customer(name) VALUES ('Ernesto Esvida');
INSERT INTO customer(name) VALUES ('Marta Gil');
INSERT INTO customer(name) VALUES ('Luis Ortega');
INSERT INTO customer(name) VALUES ('Ana Ruiz');

INSERT INTO loan(game_id, customer_id, start_date, end_date) VALUES (1, 1, '2026-10-01', '2026-10-10');
INSERT INTO loan(game_id, customer_id, start_date, end_date) VALUES (2, 1, '2026-10-05', '2026-10-12');
INSERT INTO loan(game_id, customer_id, start_date, end_date) VALUES (1, 2, '2026-10-15', '2026-10-20');
INSERT INTO loan(game_id, customer_id, start_date, end_date) VALUES (3, 3, '2026-10-01', '2026-10-14');
INSERT INTO loan(game_id, customer_id, start_date, end_date) VALUES (4, 2, '2026-11-01', '2026-11-07');
INSERT INTO loan(game_id, customer_id, start_date, end_date) VALUES (5, 3, '2026-11-02', '2026-11-08');
INSERT INTO loan(game_id, customer_id, start_date, end_date) VALUES (6, 1, '2026-12-01', '2026-12-05');