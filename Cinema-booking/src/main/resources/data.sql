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