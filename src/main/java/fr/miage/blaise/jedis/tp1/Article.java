package fr.miage.blaise.jedis.tp1;

import java.util.HashMap;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/**
 *
 * @author Maxime BLAISE
 */
public class Article {

    /**
     * A chaque vote, le score d'un article augmente de 457.
     */
    private static final int VOTE_INCREMENT = 457;

    /**
     * Délai d'expiration d'un article
     */
    private static final int ARTICLE_EXPIRE = Delay.SEMAINE.getSeconds();

    /**
     * Ajout d'un article dans la base Redis
     *
     * @param conn Connexion à la base
     * @param utilisateur Créateur de l'article
     * @param titre Titre de l'article
     * @param url Lien de l'article
     * @return .
     */
    public static String addArticle(Jedis conn, String utilisateur, String titre, String url) {
        String articleId = "";
        try {
            articleId = String.valueOf(conn.incr("article:"));
        } catch (Exception e) {
            // C'est le premier article
            conn.set("article:", "1");
            articleId = "1";
        }

        // Gestion des votes pour cet article
        String articleSelectionne = "selectionne:" + articleId;
        conn.sadd(articleSelectionne, utilisateur);
        // Expire au bout d'une semaine
        conn.expire(articleSelectionne, ARTICLE_EXPIRE);

        String article = "article:" + articleId;
        long timestamp = System.currentTimeMillis() / 1000;
        // Ajout des informations dans une map
        HashMap<String, String> donnees = new HashMap<>();
        donnees.put("titre", titre);
        donnees.put("lien", url);
        donnees.put("utilisateur", utilisateur);
        donnees.put("timestamp", String.valueOf(timestamp));
        donnees.put("nbvotes", "1");
        conn.hmset(article, donnees);

        // Pour la liste des articles selon le temps 
        conn.zadd("time:", timestamp, article);

        // Pour la liste des articles selon le score
        conn.zadd("score:", timestamp + VOTE_INCREMENT, article);

        // Pour classer les articles selon le nombre de votes
        conn.zadd("nbvotes:", 1, article);

        return article;
    }

    /**
     * Ajout d'un vote pour un article
     *
     * @param conn Connexion à la base Redis
     * @param idArticle Numéro de l'article
     * @param utilisateur Utilisateur votant
     */
    public static void addVote(Jedis conn, int idArticle, String utilisateur) {
        // On sauvegarde ce vote
        String articleSelectionne = "selectionne:" + idArticle;
        conn.sadd(articleSelectionne, utilisateur);

        // Incrémentation du nombre de votes pour cet article
        incVotesArticle(conn, idArticle, 1);

        conn.zincrby("nbvotes:", 1, "article:" + idArticle);
    }

    /**
     * Méthode qui permet de récupérer tous les articles.
     *
     * @param conn Connexion à la base Redis
     * @return Set des articles
     */
    public static Set<String> getAllArticles(Jedis conn) {
        Set<String> articles = conn.zrange("time:", 0, -1);
        return articles;
    }

    /**
     * Permet de récupérer les N articles les plus votés.
     *
     * @param conn Connexion à la base Redis
     * @param n Nombre d'article souhaités
     * @return Set des articles
     */
    public static Set<String> getNMostVoted(Jedis conn, int n) {
        Set<String> articles = conn.zrevrange("nbvotes:", 0, n);
        return articles;
    }

    private static void incVotesArticle(Jedis conn, int idArticle, int nbVotes) {
        conn.hincrBy("article:" + idArticle, "nbVotes", nbVotes);
    }

    /**
     * Ajout d'un article dans une catégorie.
     *
     * @param conn Connexion à la base Redis
     * @param category Nom de la catégorie
     * @param idArticle Numéro de l'article
     * @return
     */
    public static boolean addInCategory(Jedis conn, String category, int idArticle) {
        String categoryKey = "category:" + category;
        String articleKey = "article:" + idArticle;

        if (conn.sismember(categoryKey, articleKey)) {
            // Ajout
            conn.sadd(categoryKey, articleKey);
            return true;
        }

        return false;
    }

    /**
     * Permet de récupérer les scores d'un article d'une catégorie donnée.
     *
     * @param conn Connexion à la base Redis
     * @param category Catégorie donnée
     * @return Liste des articles de la catégorie, avec leurs scores
     */
    public static Set<Tuple> scoreForOneCategory(Jedis conn, String category) {
        String newCategoryKey = "score:" + category;
        // Commande dans redis-cli : zinterstore score:prog 2 cat:prog score aggregate max
        conn.zinterstore(newCategoryKey, "2", "category:" + category, "score:", "aggregate", "max");

        Set<Tuple> articlesWithScore = conn.zrevrangeWithScores(newCategoryKey, 0, -1);
        return articlesWithScore;
    }
}
