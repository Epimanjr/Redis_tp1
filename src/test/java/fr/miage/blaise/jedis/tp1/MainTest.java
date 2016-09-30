/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.miage.blaise.jedis.tp1;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import redis.clients.jedis.Jedis;

/**
 *
 * @author maxim
 */
public class MainTest {
    
    private static final String host = "localhost";
    private static final int port = 6379;
    private static final int db = 2;

    
    public MainTest() {
    }
    
     @BeforeClass
    public static void cleanUp(){
        Jedis jedis = new Jedis(host,port);
        jedis.select(db);
        if(db != 0)
            jedis.flushDB();
    }

    @Test
    public void testConnexion(){
        Jedis jedis = new Jedis(host,port);
        jedis.select(db);
        assertEquals(jedis.ping(), "PONG");
    }
}
