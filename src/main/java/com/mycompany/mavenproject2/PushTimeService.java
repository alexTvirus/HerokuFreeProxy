/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject2;

import java.util.*;
import javax.websocket.Session;
public class PushTimeService implements Runnable {
    
    private static PushTimeService instance;
    private static Map<String, Session> sMap = new HashMap<String, Session>();
    private PushTimeService() {}
    
    public static void add(Session s) {
        sMap.put(s.getId(), s);
    }
    
    public static void initialize() {
        if (instance == null) {
            instance = new PushTimeService();
            new Thread(instance).start();
        }
    }
    @Override
    public void run() {
        
        while (true) {
            try {
                Thread.sleep(10*1000);
                for (String key : sMap.keySet()) {
                    
                    Session s = sMap.get(key); 
                    
                    if (s.isOpen()) {
                        Date d = new Date(System.currentTimeMillis());
                        s.getBasicRemote().sendText(d.toString());
                    } else {
                        sMap.remove(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
