-- =====================================================================
-- TecnoUAM - Sistema de préstamo de equipos tecnológicos
-- Script 2/2: datos mínimos para la demostración
--     psql -U postgres -d tecno_uam_prestamos -f database/02_datos.sql
--
-- Es idempotente: puede ejecutarse varias veces sin duplicar registros.
-- (Al iniciar, el backend también crea estas cuentas y equipos si no existen.)
-- =====================================================================

-- pgcrypto genera hashes BCrypt ($2a$) compatibles con BCryptPasswordEncoder de Spring Security
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Cuentas de prueba (ver README):  admin / Admin123*  (ADMIN)   ·   usuario / Usuario123*  (USER)
INSERT INTO usuarios (username, password, rol)
VALUES ('admin',   crypt('Admin123*',   gen_salt('bf', 10)), 'ADMIN'),
       ('usuario', crypt('Usuario123*', gen_salt('bf', 10)), 'USER')
ON CONFLICT (username) DO NOTHING;

-- Equipos de ejemplo (todos inician DISPONIBLE)
INSERT INTO equipos (codigo_patrimonial, nombre, tipo, estado)
VALUES ('UAM-LAP-001', 'Laptop Dell Latitude 5440',     'PORTATIL',  'DISPONIBLE'),
       ('UAM-LAP-002', 'Laptop HP ProBook 450 G10',     'PORTATIL',  'DISPONIBLE'),
       ('UAM-PRO-001', 'Proyector Epson PowerLite X49', 'PROYECTOR', 'DISPONIBLE'),
       ('UAM-PRO-002', 'Proyector BenQ MW560',          'PROYECTOR', 'DISPONIBLE'),
       ('UAM-TAB-001', 'Tableta Samsung Galaxy Tab S9', 'TABLETA',   'DISPONIBLE')
ON CONFLICT (codigo_patrimonial) DO NOTHING;

-- Consulta de verificación
SELECT id, codigo_patrimonial, nombre, tipo, estado FROM equipos ORDER BY id;
