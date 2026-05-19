-- ============================================
-- TRANSFERMARKT ANALYST - DATENBANK IMPORT
-- ============================================
-- Führe diese SQL in der H2-Konsole aus:
-- 1. Öffne http://localhost:8080/h2-console
-- 2. Verbinde mit jdbc:h2:file:./data/transferdb
-- 3. Führe dieses Skript aus (kopieren + einfügen)
-- ============================================

-- Tabellen erstellen
CREATE TABLE IF NOT EXISTS PLAYER_PROFILES (
    player_id VARCHAR(50),
    player_name VARCHAR(200),
    position VARCHAR(100),
    date_of_birth VARCHAR(20),
    citizenship VARCHAR(100),
    foot VARCHAR(50),
    current_club_id VARCHAR(50),
    current_club_name VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS PLAYER_MARKET_VALUES (
    player_id VARCHAR(50),
    date_unix VARCHAR(50),
    wert VARCHAR(50)
);

-- Spieler-Profile importieren (Passe den Pfad an!)
INSERT INTO PLAYER_PROFILES (player_id, player_name, position, date_of_birth, citizenship, foot, current_club_id, current_club_name)
SELECT
    "player_id",
    "player_name",
    "position",
    "date_of_birth",
    "citizenship",
    "foot",
    "current_club_id",
    "current_club_name"
FROM CSVREAD('./football-datasets/datalake/transfermarkt/player_profiles/player_profiles.csv',
    'player_id,player_slug,player_name,player_image_url,name_in_home_country,date_of_birth,place_of_birth,country_of_birth,height,citizenship,is_eu,position,main_position,foot,current_club_id,current_club_name,joined,contract_expires,outfitter,social_media_url,player_agent_id,player_agent_name,contract_option,date_of_last_contract_extension,on_loan_from_club_id,on_loan_from_club_name,contract_there_expires,second_club_url,second_club_name,third_club_url,third_club_name,fourth_club_url,fourth_club_name,date_of_death')
WHERE "player_id" != 'player_id';

-- Aktuelle Marktwerte importieren
INSERT INTO PLAYER_MARKET_VALUES (player_id, date_unix, wert)
SELECT
    "player_id",
    "date_unix",
    "value"
FROM CSVREAD('./football-datasets/datalake/transfermarkt/player_latest_market_value/player_latest_market_value.csv',
    'player_id,date_unix,value',
    'charset=UTF-8')
WHERE "player_id" != 'player_id';

-- Prüfen
SELECT COUNT(*) as Spieler FROM PLAYER_PROFILES;
SELECT COUNT(*) as Marktwerte FROM PLAYER_MARKET_VALUES;

-- Beispiel: Zeige Top-Spieler
SELECT player_name, position, wert as market_value
FROM PLAYER_PROFILES p
JOIN PLAYER_MARKET_VALUES mv ON p.player_id = mv.player_id
WHERE wert > '0'
LIMIT 10;