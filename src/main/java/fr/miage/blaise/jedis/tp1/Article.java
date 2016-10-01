package fr.miage.blaise.jedis.tp1;

import java.util.HashMap;
import redis.clients.jedis.Jedis;

/**
 *
 * @author Maxime BLAISE
 */
public class Article {
    
    /**
     * A chaque vote, le score d'un article augmente de 457.
     */
    private static final int VOTE_INCREMENT = 457;
    

    private String utilisateur;

    private String titre;

    private String url;

    public Article(String utilisateur, String titre, String url) {
        this.utilisateur = utilisateur;
        this.titre = titre;
        this.url = url;
    }

    public String ajouterArticle(Jedis conn) {
        String articleId = String.valueOf(conn.incr("article:"));
        
        long timestamp = System.currentTimeMillis() / 1000;
        // Ajout des informations dans une map
        HashMap<String, String> donnees = new HashMap<>();
        donnees.put("titre", titre);
        donnees.put("lien", url);
        donnees.put("utilisateur", utilisateur);
        donnees.put("timestamp", String.valueOf(timestamp));
        donnees.put("nbvotes", "1");
        conn.hmset(articleId, donnees);
        
        // Pour la liste des articles selon le temps 
        conn.zadd("time:", timestamp, articleId);
        
        // Pour la liste des articles selon le score
        conn.zadd("score:", timestamp + VOTE_INCREMENT, articleId);
        
        return articleId;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
