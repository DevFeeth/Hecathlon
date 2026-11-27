INSERT INTO sport (id, name_fr, name_en, tagline_fr, tagline_en, description_fr, description_en, image, position) VALUES
('running', 'Course à pied', 'Running', 'Dès le premier kilomètre', 'From the very first kilometre',
 'Le bon équipement pour chaque foulée. Chaussures, textile et accessoires pour courir plus loin, à votre rythme.',
 'The right gear for every stride. Shoes, clothing and accessories to run further, at your own pace.',
 '/images/sports/running.jpg', 1),
('football', 'Football', 'Football', 'Le terrain vous attend', 'The pitch is waiting',
 'Du terrain synthétique au gazon, tout pour jouer : chaussures, ballons et textile pour toute l''équipe.',
 'From artificial turf to grass, everything to play: boots, balls and clothing for the whole team.',
 '/images/sports/football.jpg', 2),
('cycling', 'Cyclisme', 'Cycling', 'Prenez la route', 'Hit the road',
 'Sur route ou en ville, roulez en sécurité : vélos, casques et équipements pour toutes vos sorties.',
 'On the road or in town, ride safely: bikes, helmets and gear for every ride.',
 '/images/sports/cycling.jpg', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_type (id, name_fr, name_en, position) VALUES
('shoes', 'Chaussures', 'Shoes', 1),
('clothing', 'Textile', 'Clothing', 2),
('accessories', 'Accessoires', 'Accessories', 3),
('balls', 'Ballons', 'Balls', 4),
('bikes', 'Vélos', 'Bikes', 5),
('helmets', 'Casques', 'Helmets', 6),
('gear', 'Équipements', 'Gear', 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, brand, sport_id, type_id, short_description, description, rating, review_count, price, old_price,
                     best_seller, featured, highlighted, image, position) VALUES
('pace-500', 'Chaussures running homme Pace 500', 'Élan Running', 'running', 'shoes', 'Amorti confort · 3 couleurs',
 'Votre alliée pour les sorties régulières sur route. Un amorti généreux, une foulée fluide et un maintien rassurant, du premier au dernier kilomètre.',
 4.7, 248, 89.99, 109.99, false, true, true, '/images/products/pace-500.jpg', 1),
('flow-700', 'Chaussures running femme Flow 700', 'Élan Running', 'running', 'shoes', 'Légèreté · 2 couleurs',
 'Légère et dynamique, la Flow 700 accompagne les coureuses qui veulent gagner en vitesse sans sacrifier le confort.',
 4.8, 94, 99.99, 119.99, false, false, false, '/images/products/flow-700.jpg', 2),
('air-100', 'T-shirt running homme Air 100', 'Élan Running', 'running', 'clothing', 'Respirant · 5 couleurs',
 'Un t-shirt léger qui évacue la transpiration pour rester au sec, même quand le rythme s''accélère.',
 4.6, 186, 19.99, NULL, true, false, false, '/images/products/air-100.jpg', 3),
('light-500', 'Short running homme Light 500', 'Élan Running', 'running', 'clothing', 'Poche zippée · 2 couleurs',
 'Un short léger avec slip intégré et poche zippée pour garder vos clés à portée de main.',
 4.5, 128, 24.99, NULL, false, false, false, '/images/products/light-500.jpg', 4),
('bottle-500', 'Gourde souple running 500 ml', 'Élan Running', 'running', 'accessories', 'Sans BPA · 500 ml',
 'Une gourde souple qui se compresse à mesure que vous buvez : aucun ballottement pendant la course.',
 4.7, 146, 12.99, NULL, false, false, false, '/images/products/bottle-500.jpg', 5),
('gps-900', 'Montre GPS Connect 900', 'Élan Connect', 'running', 'accessories', 'GPS intégré · Autonomie 20 h',
 'Allure, distance, fréquence cardiaque : suivez chaque séance et progressez grâce au GPS intégré.',
 4.6, 76, 129.99, 149.99, false, false, false, '/images/products/gps-900.jpg', 6),
('strike-900', 'Chaussures de football Strike 900 FG', 'Élan Football', 'football', 'shoes', 'Terrain sec · 2 couleurs',
 'Une chaussure de précision pour terrains secs, avec une tige fine pour un toucher de balle optimal.',
 4.6, 186, 69.99, NULL, true, true, false, '/images/products/strike-900.jpg', 7),
('agility-500', 'Chaussures de football enfant Agility 500', 'Élan Football', 'football', 'shoes', 'Terrain sec · Scratch',
 'Une chaussure facile à enfiler grâce à son scratch, pensée pour les jeunes joueurs sur terrain sec.',
 4.5, 88, 34.99, 39.99, false, false, false, '/images/products/agility-500.jpg', 8),
('match-500', 'Ballon de football Match 500 taille 5', 'Élan Football', 'football', 'balls', 'Cousu machine · Taille 5',
 'Un ballon cousu machine, rond et stable, pour les matchs comme pour les entraînements.',
 4.8, 94, 24.99, 29.99, false, true, false, '/images/products/match-500.jpg', 9),
('training-100', 'Ballon d''entraînement Training 100 taille 5', 'Élan Football', 'football', 'balls', 'Résistant · Toutes surfaces',
 'Un ballon résistant pour enchaîner les séances d''entraînement sur toutes les surfaces.',
 4.4, 203, 9.99, NULL, false, false, false, '/images/products/training-100.jpg', 10),
('jersey-500', 'Maillot de football Club 500', 'Élan Football', 'football', 'clothing', 'Respirant · 4 couleurs',
 'Un maillot respirant et léger, idéal pour les matchs et les entraînements en club.',
 4.5, 67, 19.99, NULL, false, false, false, '/images/products/jersey-500.jpg', 11),
('socks-100', 'Chaussettes de football F100', 'Élan Football', 'football', 'clothing', 'Renforcées · 6 couleurs',
 'Des chaussettes montantes renforcées aux zones d''usure, compatibles avec les protège-tibias.',
 4.6, 154, 6.99, NULL, false, false, false, '/images/products/socks-100.jpg', 12),
('strada-500', 'Vélo de route Strada 500', 'Élan Vélo', 'cycling', 'bikes', 'Cadre aluminium · 16 vitesses',
 'Un vélo de route en aluminium, léger et polyvalent, pour vos premières longues sorties.',
 4.7, 58, 699.99, 799.99, false, false, false, '/images/products/strada-500.jpg', 13),
('urban-100', 'Vélo de ville Urban 100', 'Élan Vélo', 'cycling', 'bikes', 'Panier · Éclairage intégré',
 'Un vélo de ville confortable avec panier et éclairage intégré pour vos trajets du quotidien.',
 4.5, 41, 329.99, NULL, false, false, false, '/images/products/urban-100.jpg', 14),
('aero-700', 'Casque vélo route Aero 700', 'Élan Vélo', 'cycling', 'helmets', 'Ventilé · 3 tailles',
 'Un casque route léger et bien ventilé, avec molette de réglage pour un maintien précis.',
 4.6, 75, 79.99, 99.99, false, true, false, '/images/products/aero-700.jpg', 15),
('kid-500', 'Casque vélo enfant Kid 500', 'Élan Vélo', 'cycling', 'helmets', 'Réglable · 2 tailles',
 'Un casque enfant réglable et bien visible pour rouler en sécurité dès les premiers tours de roue.',
 4.8, 132, 24.99, NULL, true, false, false, '/images/products/kid-500.jpg', 16),
('rain-500', 'Veste de pluie vélo Rain 500', 'Élan Vélo', 'cycling', 'gear', 'Imperméable · Respirante',
 'Une veste imperméable et respirante qui se range dans sa poche, pour ne jamais être surpris par la pluie.',
 4.5, 112, 59.99, NULL, false, false, true, '/images/products/rain-500.jpg', 17),
('gloves-500', 'Gants vélo Road 500', 'Élan Vélo', 'cycling', 'gear', 'Rembourrés · Mitaines',
 'Des mitaines rembourrées qui absorbent les vibrations et protègent vos mains en cas de chute.',
 4.4, 97, 14.99, 17.99, false, false, false, '/images/products/gloves-500.jpg', 18)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_size (product_id, position, size)
SELECT p.id, s.position - 1, s.size
FROM product p
CROSS JOIN LATERAL unnest(
    CASE
        WHEN p.id IN ('pace-500', 'flow-700', 'strike-900') THEN ARRAY['39', '40', '41', '42', '43', '44', '45', '46']
        WHEN p.id = 'agility-500' THEN ARRAY['30', '31', '32', '33', '34', '35', '36', '37', '38']
        WHEN p.id IN ('air-100', 'light-500', 'jersey-500', 'rain-500') THEN ARRAY['XS', 'S', 'M', 'L', 'XL']
        WHEN p.id = 'socks-100' THEN ARRAY['35-38', '39-42', '43-46']
        WHEN p.id IN ('strada-500', 'urban-100', 'aero-700') THEN ARRAY['S', 'M', 'L']
        WHEN p.id = 'kid-500' THEN ARRAY['XS', 'S']
        WHEN p.id = 'gloves-500' THEN ARRAY['S', 'M', 'L', 'XL']
        ELSE ARRAY[]::varchar[]
    END) WITH ORDINALITY AS s(size, position)
ON CONFLICT DO NOTHING;

INSERT INTO "user" (email, password, first_name, last_name, newsletter, phone, address, address_complement, postal_code, city, country,
                    created_at, account_non_expired, account_non_locked, credentials_non_expired, enabled) VALUES
('camille.martin@example.com', '$2a$10$55peRB9zD38e06WYaHXq4.8hqtoEK1kLcbEEVQERL4at65Q2CNpeK', 'Camille', 'Martin', true,
 '0470123456', 'Rue de Bruxelles 61', NULL, '5000', 'Namur', 'Belgique', DATE '2025-03-12', true, true, true, true),
('lucas.dubois@example.com', '$2a$10$nMxSExVuKg3/p0eWRfTnU.epa8q7UdDZ9DjI8o7bICGNdeiND5u02', 'Lucas', 'Dubois', false,
 '0485654321', 'Avenue de la Gare 12', 'Boîte 3', '4000', 'Liège', 'Belgique', DATE '2025-09-02', true, true, true, true)
ON CONFLICT (email) DO NOTHING;
