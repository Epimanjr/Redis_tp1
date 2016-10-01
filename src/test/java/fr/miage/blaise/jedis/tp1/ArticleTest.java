package fr.miage.blaise.jedis.tp1;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import redis.clients.jedis.Jedis;

/**
 *
 * @author Maxime
 */
public class ArticleTest {

    private static final String host = "localhost";
    private static final int port = 6379;
    private static final int db = 2;

    private String utilisateur = "max";
    private String titre = "Titre très original";
    private String lien = "website.com/titre_original.aspx";

    public ArticleTest() {
    }

    @BeforeClass
    public static void cleanUp() {
        Jedis jedis = new Jedis(host, port);
        jedis.select(db);
        if (db != 0) {
            jedis.flushDB();
        }
    }

    @Test
    public void testConnexion() {
        Jedis jedis = new Jedis(host, port);
        jedis.select(db);
        assertEquals(jedis.ping(), "PONG");
    }

    @Test
    public void testAddArticle() {
        Jedis jedis = new Jedis(host, port);
        jedis.select(db);
        if(jedis.exists("article:")) {
            jedis.del("article:");
        }

        // Ajout de l'article
        String id = Article.addArticle(jedis, utilisateur, titre, lien);
        assertEquals(id, "article:1");

        // Existence du Hash et vérification du contenu de ce Hash
        assertTrue(jedis.exists(id));
        assertEquals(jedis.hget(id, "titre"), titre);
        assertEquals(jedis.hget(id, "utilisateur"), utilisateur);
        assertEquals(jedis.hget(id, "lien"), lien);

        // Existences des autres structures
        assertTrue(jedis.exists("time:"));
        assertTrue(jedis.exists("score:"));
        assertTrue(jedis.exists("nbvotes:"));
        // Vérification des tailles de ces structures
        //

        String id2 = Article.addArticle(jedis, utilisateur, titre, lien);
        assertEquals(id2, "article:2");

    }

    @Test
    public void testAddVote() {
        Jedis jedis = new Jedis(host, port);
        jedis.select(db);
        if(jedis.exists("article:")) {
            jedis.del("article:");
        }

        String id = Article.addArticle(jedis, utilisateur, titre, lien);
        assertEquals(id, "article:1");
        
        // Ajout d'un vote
        Article.addVote(jedis, 1, "blaise");
        
        assertTrue(jedis.exists("selectionne:1"));
        assertTrue(jedis.sismember("selectionne:1", "blaise"));
        assertEquals(jedis.hget("article:1", "nbvotes"), "2");
        
        assertTrue(jedis.exists("nbvotes:"));
        
    }
}
