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
        
        // Ajout de l'article
        String utilisateur = "max";
        String titre = "Titre très original";
        String lien = "website.com/titre_original.aspx";
        String id = Article.addArticle(jedis, utilisateur, titre, lien);
        assertEquals(id, "article:1");
        
        // Existence du Hash et vérification du contenu de ce Hash
        assertTrue(jedis.exists(id));
        assertEquals(jedis.hget(id, "titre"), titre);
        assertEquals(jedis.hget(id, "utilisateur"), utilisateur);
        assertEquals(jedis.hget(id, "lien"), lien);
        
    }
}
