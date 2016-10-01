package fr.miage.blaise.jedis.tp1;

/**
 *
 * @author Maxime
 */
public enum Delay {
    // Objets directement construits
    HEURE(3600),
    JOUR(3600 * 24),
    SEMAINE(3600 * 24 * 7),
    MOIS(3600 * 24 * 30);

    private final int seconds;

    Delay(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }
}
