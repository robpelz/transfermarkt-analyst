package com.transfermarkt.transfermarkt_analyst.service;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TeamLogoMapper {

    private final Map<String, String> logoMap = new ConcurrentHashMap<>();

    public TeamLogoMapper() {
        // ==================== BUNDESLIGA ====================
        logoMap.put("Bayern Munich", "/logos/bundesliga/Bayern Munich.png");
        logoMap.put("Borussia Dortmund", "/logos/bundesliga/Borussia Dortmund.png");
        logoMap.put("Borussia Mönchengladbach", "/logos/bundesliga/Borussia Mönchengladbach.png");
        logoMap.put("Eintracht Frankfurt", "/logos/bundesliga/Eintracht Frankfurt.png");
        logoMap.put("FC Augsburg", "/logos/bundesliga/FC Augsburg.png");
        logoMap.put("FC Heidenheim", "/logos/bundesliga/1.FC Heidenheim 1846.png");
        logoMap.put("FC Köln", "/logos/bundesliga/1.FC Köln.png");
        logoMap.put("Freiburg", "/logos/bundesliga/SC Freiburg.png");
        logoMap.put("Hamburg", "/logos/bundesliga/Hamburger SV.png");
        logoMap.put("Hoffenheim", "/logos/bundesliga/TSG 1899 Hoffenheim.png");
        logoMap.put("Mainz", "/logos/bundesliga/1.FSV Mainz 05.png");
        logoMap.put("RB Leipzig", "/logos/bundesliga/RB Leipzig.png");
        logoMap.put("St Pauli", "/logos/bundesliga/FC St. Pauli.png");
        logoMap.put("Stuttgart", "/logos/bundesliga/VfB Stuttgart.png");
        logoMap.put("Union Berlin", "/logos/bundesliga/1.FC Union Berlin.png");
        logoMap.put("Werder Bremen", "/logos/bundesliga/SV Werder Bremen.png");
        logoMap.put("Wolfsburg", "/logos/bundesliga/VfL Wolfsburg.png");
        logoMap.put("Bayer Leverkusen", "/logos/bundesliga/Bayer 04 Leverkusen.png");

        // ==================== PREMIER LEAGUE ====================
        logoMap.put("Arsenal", "/logos/premier-league/Arsenal FC.png");
        logoMap.put("Aston Villa", "/logos/premier-league/Aston Villa.png");
        logoMap.put("Bournemouth", "/logos/premier-league/AFC Bournemouth.png");
        logoMap.put("Brentford", "/logos/premier-league/Brentford FC.png");
        logoMap.put("Brighton", "/logos/premier-league/Brighton & Hove Albion.png");
        logoMap.put("Burnley", "/logos/premier-league/Burnley FC.png");
        logoMap.put("Chelsea", "/logos/premier-league/Chelsea FC.png");
        logoMap.put("Crystal Palace", "/logos/premier-league/Crystal Palace.png");
        logoMap.put("Everton", "/logos/premier-league/Everton FC.png");
        logoMap.put("Fulham", "/logos/premier-league/Fulham FC.png");
        logoMap.put("Leeds", "/logos/premier-league/Leeds United.png");
        logoMap.put("Liverpool", "/logos/premier-league/Liverpool FC.png");
        logoMap.put("Manchester City", "/logos/premier-league/Manchester City.png");
        logoMap.put("Manchester United", "/logos/premier-league/Manchester United.png");
        logoMap.put("Newcastle", "/logos/premier-league/Newcastle United.png");
        logoMap.put("Nottingham", "/logos/premier-league/Nottingham Forest.png");
        logoMap.put("Sunderland", "/logos/premier-league/Sunderland AFC.png");
        logoMap.put("Tottenham", "/logos/premier-league/Tottenham Hotspur.png");
        logoMap.put("West Ham", "/logos/premier-league/West Ham United.png");
        logoMap.put("Wolverhampton", "/logos/premier-league/Wolverhampton Wanderers.png");

        // ==================== LA LIGA ====================
        logoMap.put("Athletic Bilbao", "/logos/la-liga/Athletic Bilbao.png");
        logoMap.put("Atlético Madrid", "/logos/la-liga/Atlético de Madrid.png");
        logoMap.put("Barcelona", "/logos/la-liga/FC Barcelona.png");
        logoMap.put("Celta", "/logos/la-liga/Celta de Vigo.png");
        logoMap.put("Alaves", "/logos/la-liga/Deportivo Alavés.png");
        logoMap.put("Elche", "/logos/la-liga/Elche CF.png");
        logoMap.put("Espanyol", "/logos/la-liga/RCD Espanyol Barcelona.png");
        logoMap.put("Getafe", "/logos/la-liga/Getafe CF.png");
        logoMap.put("Girona", "/logos/la-liga/Girona FC.png");
        logoMap.put("Levante", "/logos/la-liga/Levante UD.png");
        logoMap.put("Mallorca", "/logos/la-liga/RCD Mallorca.png");
        logoMap.put("Osasuna", "/logos/la-liga/CA Osasuna.png");
        logoMap.put("Rayo Vallecano", "/logos/la-liga/Rayo Vallecano.png");
        logoMap.put("Betis", "/logos/la-liga/Real Betis Balompié.png");
        logoMap.put("Real Madrid", "/logos/la-liga/Real Madrid.png");
        logoMap.put("Real Oviedo", "/logos/la-liga/Real Oviedo.png");
        logoMap.put("Real Sociedad", "/logos/la-liga/Real Sociedad.png");
        logoMap.put("Sevilla", "/logos/la-liga/Sevilla FC.png");
        logoMap.put("Valencia", "/logos/la-liga/Valencia CF.png");
        logoMap.put("Villarreal", "/logos/la-liga/Villarreal CF.png");

        // ==================== SERIE A ====================
        logoMap.put("AC Milan", "/logos/serie-a/AC Milan.png");
        logoMap.put("Atalanta", "/logos/serie-a/Atalanta BC.png");
        logoMap.put("Bologna", "/logos/serie-a/Bologna FC 1909.png");
        logoMap.put("Cagliari", "/logos/serie-a/Cagliari Calcio.png");
        logoMap.put("Como", "/logos/serie-a/Como 1907.png");
        logoMap.put("Cremonese", "/logos/serie-a/US Cremonese.png");
        logoMap.put("Fiorentina", "/logos/serie-a/ACF Fiorentina.png");
        logoMap.put("Genoa", "/logos/serie-a/Genoa CFC.png");
        logoMap.put("Hellas Verona", "/logos/serie-a/Hellas Verona.png");
        logoMap.put("Inter Milan", "/logos/serie-a/Inter Milan.png");
        logoMap.put("Juventus", "/logos/serie-a/Juventus FC.png");
        logoMap.put("Lazio", "/logos/serie-a/SS Lazio.png");
        logoMap.put("Lecce", "/logos/serie-a/US Lecce.png");
        logoMap.put("Napoli", "/logos/serie-a/SSC Napoli.png");
        logoMap.put("Parma", "/logos/serie-a/Parma Calcio 1913.png");
        logoMap.put("Pisa", "/logos/serie-a/Pisa Sporting Club.png");
        logoMap.put("Roma", "/logos/serie-a/AS Roma.png");
        logoMap.put("Sassuolo", "/logos/serie-a/US Sassuolo.png");
        logoMap.put("Torino", "/logos/serie-a/Torino FC.png");
        logoMap.put("Udinese", "/logos/serie-a/Udinese Calcio.png");
    }

    public String getLogoUrl(String teamName) {
        return logoMap.get(teamName);
    }
}