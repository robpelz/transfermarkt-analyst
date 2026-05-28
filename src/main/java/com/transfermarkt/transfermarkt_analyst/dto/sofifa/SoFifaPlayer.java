package com.transfermarkt.transfermarkt_analyst.dto.sofifa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoFifaPlayer {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("club")
    private String club;

    @JsonProperty("positions")
    private List<String> positions;

    @JsonProperty("value")
    private String value;

    @JsonProperty("wage")
    private String wage;

    @JsonProperty("overall")
    private Map<String, Object> overall;

    @JsonProperty("potential")
    private Map<String, Object> potential;

    @JsonProperty("total_stats")
    private int totalStats;

    @JsonProperty("clubId")
    private int clubId;

    @JsonProperty("leagueId")
    private int leagueId;

    @JsonProperty("preferredFoot")
    private String preferredFoot;

    @JsonProperty("weakFootAbility")
    private int weakFootAbility;

    @JsonProperty("skillMoves")
    private int skillMoves;

    @JsonProperty("internationalReputation")
    private int internationalReputation;

    @JsonProperty("pace")
    private int pace;

    @JsonProperty("shooting")
    private int shooting;

    @JsonProperty("passing")
    private int passing;

    @JsonProperty("dribbling")
    private int dribbling;

    @JsonProperty("defending")
    private int defending;

    @JsonProperty("physicality")
    private int physicality;

    @JsonProperty("imageUrl")
    private String imageUrl;

    /**
     * Gibt den vollen Namen zurück
     */
    public String getFullName() {
        return name;
    }

    /**
     * Konvertiert Wert-String (z.B. "€101M") in Millionen Euro
     * @return Marktwert in Millionen Euro, oder 0 wenn nicht parsbar
     */
    public double getValueInMillion() {
        if (value == null || value.isEmpty() || value.equals("?")) {
            return 0;
        }

        try {
            String clean = value.replace("€", "").replace(",", ".").trim();

            if (clean.contains("M")) {
                clean = clean.replace("M", "").trim();
                return Double.parseDouble(clean);
            } else if (clean.contains("K")) {
                clean = clean.replace("K", "").trim();
                return Double.parseDouble(clean) / 1000.0;
            } else {
                return Double.parseDouble(clean) / 1_000_000.0;
            }
        } catch (NumberFormatException e) {
            // Logging optional, aber nicht notwendig
            return 0;
        }
    }

    /**
     * Konvertiert Gehalt-String (z.B. "€170K") in Tausend Euro
     * @return Gehalt in Tausend Euro
     */
    public double getWageInThousands() {
        if (wage == null || wage.isEmpty()) return 0;

        String clean = wage.replace("€", "").replace(",", ".").trim();

        if (clean.contains("K")) {
            clean = clean.replace("K", "");
            return Double.parseDouble(clean);
        } else if (clean.contains("M")) {
            clean = clean.replace("M", "");
            return Double.parseDouble(clean) * 1000;
        } else {
            return Double.parseDouble(clean);
        }
    }

    /**
     * Gibt die primäre Position zurück
     * @return erste Position aus der Liste
     */
    public String getPrimaryPosition() {
        if (positions == null || positions.isEmpty()) {
            return "N/A";
        }
        return positions.get(0);
    }

    /**
     * Gibt Gesamtbewertung als Zahl zurück
     */
    public int getOverallRating() {
        if (overall == null) return 0;
        Object base = overall.get("base");
        return base instanceof Number ? ((Number) base).intValue() : 0;
    }

    /**
     * Gibt Potenzial als Zahl zurück
     */
    public int getPotentialRating() {
        if (potential == null) return 0;
        Object base = potential.get("base");
        return base instanceof Number ? ((Number) base).intValue() : 0;
    }
}