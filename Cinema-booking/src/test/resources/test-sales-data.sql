-- Insert test data for cinema_room
INSERT INTO cinema_room (id, name) VALUES
                                       (1, 'Hall A'),
                                       (2, 'Hall B');

-- Insert test data for movie
INSERT INTO movie (id, title, description, duration_min, genre, image_url, trailer_url, director, main_cast, age_rating) VALUES
                                                                                                                             (1, 'Inception', 'A mind-bending thriller about dreams within dreams', 148, 'Sci-Fi', 'https://example.com/inception.jpg', 'https://youtube.com/inception', 'Christopher Nolan', 'Leonardo DiCaprio', 'PG-13'),
                                                                                                                             (2, 'The Matrix', 'A computer hacker learns about the true nature of his reality', 136, 'Sci-Fi', 'https://example.com/matrix.jpg', 'https://youtube.com/matrix', 'Wachowski', 'Keanu Reeves', 'R'),
                                                                                                                             (3, 'Interstellar', 'A team of explorers travel through a wormhole in space', 169, 'Sci-Fi', 'https://example.com/interstellar.jpg', 'https://youtube.com/interstellar', 'Christopher Nolan', 'Matthew McConaughey', 'PG-13');

-- Insert test data for seance
INSERT INTO seance (id, movie_id, cinema_room_id, start_time, end_time, regular_ticket_price, reduced_ticket_price) VALUES
                                                                                                                        (1, 1, 1, '2024-05-01 18:00:00', '2024-05-01 20:28:00', 15.0, 10.0),
                                                                                                                        (2, 1, 1, '2024-05-02 18:00:00', '2024-05-02 20:28:00', 15.0, 10.0),
                                                                                                                        (3, 2, 2, '2024-05-01 19:00:00', '2024-05-01 21:16:00', 15.0, 10.0),
                                                                                                                        (4, 3, 1, '2024-05-03 20:00:00', '2024-05-03 23:49:00', 15.0, 10.0);

-- Insert test data for seat
INSERT INTO seat (id, cinema_room_id, row_number, seat_number) VALUES
                                                                   (1, 1, 1, 1),
                                                                   (2, 1, 1, 2),
                                                                   (3, 1, 1, 3),
                                                                   (4, 1, 2, 1),
                                                                   (5, 1, 2, 2),
                                                                   (6, 2, 1, 1),
                                                                   (7, 2, 1, 2),
                                                                   (8, 2, 2, 1);

-- Insert test data for users
INSERT INTO users (id, username, email, password, first_name, last_name, role) VALUES
                                                                                   (1, 'user1', 'user1@example.com', 'hashedpassword1', 'John', 'Doe', 'USER'),
                                                                                   (2, 'user2', 'user2@example.com', 'hashedpassword2', 'Jane', 'Smith', 'USER'),
                                                                                   (3, 'user3', 'user3@example.com', 'hashedpassword3', 'Bob', 'Johnson', 'USER');

-- Insert test data for reservation with PAID status
INSERT INTO reservation (id, user_id, reservation_code, created_at, expires_at, status, total_price) VALUES
                                                                                                         (1, 1, 'RES001', '2024-05-01 10:00:00', '2024-05-02 10:00:00', 'PAID', 30.0),
                                                                                                         (2, 2, 'RES002', '2024-05-01 11:00:00', '2024-05-02 11:00:00', 'PAID', 10.0),
                                                                                                         (3, 1, 'RES003', '2024-05-02 10:00:00', '2024-05-03 10:00:00', 'PAID', 15.0),
                                                                                                         (4, 3, 'RES004', '2024-05-01 12:00:00', '2024-05-02 12:00:00', 'PAID', 23.0),
                                                                                                         (5, 2, 'RES005', '2024-05-03 15:00:00', '2024-05-04 15:00:00', 'PAID', 15.0);

-- Insert test data for ticket
INSERT INTO ticket (id, reservation_id, seance_id, seat_id, ticket_code, ticket_type, price) VALUES
                                                                                                 (1, 1, 1, 1, 'TICK001', 'REGULAR', 15.0),
                                                                                                 (2, 1, 1, 2, 'TICK002', 'REGULAR', 15.0),
                                                                                                 (3, 2, 1, 4, 'TICK003', 'REDUCED', 10.0),
                                                                                                 (4, 3, 2, 5, 'TICK004', 'REGULAR', 15.0),
                                                                                                 (5, 4, 3, 6, 'TICK005', 'REGULAR', 15.0),
                                                                                                 (6, 4, 3, 7, 'TICK006', 'REDUCED', 8.0),
                                                                                                 (7, 5, 4, 8, 'TICK007', 'REGULAR', 15.0);
