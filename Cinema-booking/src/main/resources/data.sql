-- 1. UŻYTKOWNICY
-- Hasło dla wszystkich: haslo123
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


-- 2. FILMY
INSERT INTO movie (title, description, genre, duration_min, image_url, trailer_url)
VALUES
    ('Cyberpunk: Nowa Era',
     'W świecie, gdzie technologia zatarła granice człowieczeństwa, jeden detektyw musi odnaleźć prawdę.',
     'Sci-Fi',
     165,
     'https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=2525',
     'https://www.youtube.com/watch?v=placeholder')
ON CONFLICT DO NOTHING; -- Zakłada, że nie ma UNIQUE na tytule, ale warto dodać sprawdzenie w kodzie

INSERT INTO movie (title, description, genre, duration_min, image_url, trailer_url)
VALUES
    ('Miłość w Paryżu',
     'Dwoje nieznajomych spotyka się przypadkiem na wieży Eiffla. Czy to przeznaczenie?',
     'Romans',
     110,
     'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?q=80&w=2073',
     'https://www.youtube.com/watch?v=placeholder')
ON CONFLICT DO NOTHING;

INSERT INTO movie (title, description, genre, duration_min, image_url, trailer_url)
VALUES
    ('Cienie w Mroku',
     'Grupa przyjaciół odkrywa opuszczony dwór, który nie jest tak pusty, jak się wydaje.',
     'Horror',
     95,
     'https://plus.unsplash.com/premium_photo-1667425092311-80658bb0c05f?q=80&w=1471',
     'https://www.youtube.com/watch?v=placeholder')
ON CONFLICT DO NOTHING;