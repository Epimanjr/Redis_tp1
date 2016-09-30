/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.miage.blaise.jedis.tp1;

import redis.clients.jedis.Jedis;

/**
 *
 * @author Maxime
 */
public class TestJedis {
    public static void main(String[] args) {
        Jedis conn = new Jedis("localhost");
        conn.set("cle", "valeur");
        String val = conn.get("cle");
        System.out.println(val);
    }
}
