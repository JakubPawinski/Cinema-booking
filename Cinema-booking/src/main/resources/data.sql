-- 1. UŻYTKOWNICY
-- Hasło dla wszystkich: haslo123
-- (Hash: $2a$10$LJpw/4we1OxisyGsLlsWMeBnw2zKfy0IJRClWckyQIqW2OJOtv3Z2)

INSERT INTO users (email, first_name, last_name, password, role, username)
VALUES
    ('jan.kowalski@example.com', 'Jan', 'Kowalski', '$2a$10$LJpw/4we1OxisyGsLlsWMeBnw2zKfy0IJRClWckyQIqW2OJOtv3Z2', 'USER', 'jan.kowalski')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, first_name, last_name, password, role, username)
VALUES
    ('anna.nowak@example.com', 'Anna', 'Nowak', '$2a$10$LJpw/4we1OxisyGsLlsWMeBnw2zKfy0IJRClWckyQIqW2OJOtv3Z2', 'USER', 'anna.nowak')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, first_name, last_name, password, role, username)
VALUES
    ('admin@kino.pl', 'Admin', 'Systemowy', '$2a$10$LJpw/4we1OxisyGsLlsWMeBnw2zKfy0IJRClWckyQIqW2OJOtv3Z2', 'ADMIN', 'admin')
ON CONFLICT (email) DO NOTHING;


-- 2. FILMY (Zaktualizowane o reżysera, obsadę i wiek)

-- Film 1: Cyberpunk
INSERT INTO movie (title, description, genre, duration_min, image_url, trailer_url, director, main_cast, age_rating)
VALUES
    ('Cyberpunk: Nowa Era',
     'W świecie, gdzie technologia zatarła granice człowieczeństwa, jeden detektyw musi odnaleźć prawdę.',
     'Sci-Fi',
     165,
     'https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=2525',
     'https://www.youtube.com/watch?v=qQkVqJJjSc0',
     'Denis Villeneuve',
     'Ryan Gosling, Harrison Ford, Ana de Armas',
     '16+')
ON CONFLICT DO NOTHING;

-- Film 2: Romans
INSERT INTO movie (title, description, genre, duration_min, image_url, trailer_url, director, main_cast, age_rating)
VALUES
    ('Miłość w Paryżu',
     'Dwoje nieznajomych spotyka się przypadkiem na wieży Eiffla. Czy to przeznaczenie?',
     'Romans',
     110,
     'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?q=80&w=2073',
     'https://www.youtube.com/watch?v=27i1e6d32',
     'Woody Allen',
     'Owen Wilson, Rachel McAdams',
     '12+')
ON CONFLICT DO NOTHING;

-- Film 3: Horror
INSERT INTO movie (title, description, genre, duration_min, image_url, trailer_url, director, main_cast, age_rating)
VALUES
    ('Cienie w Mroku',
     'Grupa przyjaciół odkrywa opuszczony dwór, który nie jest tak pusty, jak się wydaje.',
     'Horror',
     95,
     'https://plus.unsplash.com/premium_photo-1667425092311-80658bb0c05f?q=80&w=1471',
     'https://www.youtube.com/watch?v=k10ETZ41q5o',
     'James Wan',
     'Patrick Wilson, Vera Farmiga',
     '18+')
ON CONFLICT DO NOTHING;

-- 1. SALE KINOWE (Z wymuszonymi ID, abyśmy wiedzieli gdzie wstawiać miejsca)
-- ID: 1 = IMAX, ID: 2 = Standard, ID: 3 = VIP

INSERT INTO cinema_room (id, name) VALUES (1, 'Sala 1 - IMAX') ON CONFLICT (id) DO NOTHING;
INSERT INTO cinema_room (id, name) VALUES (2, 'Sala 2 - Standard') ON CONFLICT (id) DO NOTHING;
INSERT INTO cinema_room (id, name) VALUES (3, 'Sala 3 - VIP') ON CONFLICT (id) DO NOTHING;

-- Reset sekwencji ID dla sal (opcjonalne, ale dobre dla Postgresa)
-- ALTER SEQUENCE cinema_room_id_seq RESTART WITH 4;


-- 2. MIEJSCA DLA SALI 1 (IMAX) - 10 Rzędów po 10 Miejsc (100 miejsc)
-- Generowanie miejsc: Rząd 1
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES
                                                               (1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 1, 5), (1, 1, 6), (1, 1, 7), (1, 1, 8), (1, 1, 9), (1, 1, 10);
-- Rząd 2
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES
                                                               (1, 2, 1), (1, 2, 2), (1, 2, 3), (1, 2, 4), (1, 2, 5), (1, 2, 6), (1, 2, 7), (1, 2, 8), (1, 2, 9), (1, 2, 10);
-- Rząd 3
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES
                                                               (1, 3, 1), (1, 3, 2), (1, 3, 3), (1, 3, 4), (1, 3, 5), (1, 3, 6), (1, 3, 7), (1, 3, 8), (1, 3, 9), (1, 3, 10);
-- Rząd 4
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES
                                                               (1, 4, 1), (1, 4, 2), (1, 4, 3), (1, 4, 4), (1, 4, 5), (1, 4, 6), (1, 4, 7), (1, 4, 8), (1, 4, 9), (1, 4, 10);
-- Rząd 5
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES
                                                               (1, 5, 1), (1, 5, 2), (1, 5, 3), (1, 5, 4), (1, 5, 5), (1, 5, 6), (1, 5, 7), (1, 5, 8), (1, 5, 9), (1, 5, 10);


-- 3. MIEJSCA DLA SALI 2 (Standard) - 5 Rzędów po 8 Miejsc (40 miejsc)
-- Rząd 1
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES (2, 1, 1), (2, 1, 2), (2, 1, 3), (2, 1, 4), (2, 1, 5), (2, 1, 6), (2, 1, 7), (2, 1, 8);
-- Rząd 2
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES (2, 2, 1), (2, 2, 2), (2, 2, 3), (2, 2, 4), (2, 2, 5), (2, 2, 6), (2, 2, 7), (2, 2, 8);
-- Rząd 3
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES (2, 3, 1), (2, 3, 2), (2, 3, 3), (2, 3, 4), (2, 3, 5), (2, 3, 6), (2, 3, 7), (2, 3, 8);
-- Rząd 4
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES (2, 4, 1), (2, 4, 2), (2, 4, 3), (2, 4, 4), (2, 4, 5), (2, 4, 6), (2, 4, 7), (2, 4, 8);
-- Rząd 5
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES (2, 5, 1), (2, 5, 2), (2, 5, 3), (2, 5, 4), (2, 5, 5), (2, 5, 6), (2, 5, 7), (2, 5, 8);


-- 4. MIEJSCA DLA SALI 3 (VIP) - 3 Rzędy po 4 Miejsca (12 miejsc)
INSERT INTO seat (cinema_room_id, row_number, seat_number) VALUES
                                                               (3, 1, 1), (3, 1, 2), (3, 1, 3), (3, 1, 4),
                                                               (3, 2, 1), (3, 2, 2), (3, 2, 3), (3, 2, 4),
                                                               (3, 3, 1), (3, 3, 2), (3, 3, 3), (3, 3, 4);