-- Insert into PLACE
INSERT INTO place(id, version, name, address, city) VALUES
                                                  (1, 1, 'Concert Hall A', '123 Music St', 'New York'),
                                                  (2, 1, 'Auditorium B', '456 Harmony Rd', 'Lousiana'),
                                                    (3, 1, 'Concert Hall C', '123 Music St', 'Texas'),
                                                  (4, 1, 'Auditorium D', '456 Harmony Rd', 'Los Angeles'),
                                                    (5, 1, 'Concert Hall E', '123 Music St', 'Montana'),
                                                  (6, 1, 'Auditorium F', '456 Harmony Rd', 'Chicago');

INSERT INTO app_user(id, version, username, name, email, hashed_password) VALUES
                                                                                   (1, 1,'john', 'John Doe', 'john@example.com', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe'),
                                                                                   (2, 1, 'jane', 'Jane Smith', 'jane@example.com', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe'),
                                                                                   (3, 1, 'bob', 'Bob Lee', 'bob@example.com', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe'),
                                                                                   (4, 1, 'emma', 'Emma Jones', 'emma@example.com', '$2b$12$vL0ghQYXcLjtJCaGKZLlXuVv1xMQUmBRfbFNaFc3HNVH41FcPGHDu');

INSERT INTO user_roles(user_id, roles) VALUES (1, 'USER'),
                                              (2, 'USER'),
                                              (3, 'USER'),
                                              (4, 'ADMIN');


INSERT INTO event(id, version, name, date, organiser_id, place_id) VALUES
                                                                       (1, 1, 'Piano Concert', '2025-05-01', 1, 1),
                                                                       (2, 1, 'Cello Recital', '2025-05-05', 2, 2),
                                                                       (3, 1, 'Violin Masterclass', '2025-06-10', 3, 3),
                                                                       (4, 1, 'Guitar Evening', '2025-07-15', 4, 4),
                                                                       (5, 1, 'Flute Symposium', '2025-08-20', 1, 5),
                                                                       (6, 1, 'Trumpet Performance', '2025-09-25', 2, 6),
                                                                       (7, 1, 'Chamber Music Festival', '2025-10-01', 3, 1),
                                                                       (8, 1, 'Orchestra Gala', '2025-11-10', 4, 2),
                                                                       (9, 1, 'Jazz Night', '2025-12-05', 1, 3),
                                                                       (10, 1, 'String Quartet Series', '2025-12-15', 2, 4);

-- Insert into INSTRUMENT
INSERT INTO instrument(id, version, name) VALUES
                                              (1, 1, 'Piano'),
                                              (2, 1, 'Cello'),
                                              (3, 1, 'Violin'),
                                              (4, 1, 'Guitar'),
                                              (5, 1, 'Flute'),
                                              (6, 1, 'Trumpet');


insert into event_instrument (event_id, instrument_id) values
                                                           (1, 1), -- Piano Concert → Piano
                                                           (2, 2), -- Cello Recital → Cello
                                                           (3, 3), -- Violin Masterclass → Violin
                                                           (4, 4), -- Guitar Evening → Guitar
                                                           (5, 5), -- Flute Symposium → Flute
                                                           (6, 6), -- Trumpet Performance → Trumpet
                                                           (7, 1), -- Chamber Music Festival → Piano
                                                           (8, 2), -- Orchestra Gala → Cello
                                                           (9, 3), -- Jazz Night → Violin
                                                           (10, 4); -- String Quartet Series → Guitar