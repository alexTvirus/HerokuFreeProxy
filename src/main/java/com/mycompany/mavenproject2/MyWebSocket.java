/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/endpoint")
public class MyWebSocket {

    private static PushTimeService pst;

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @OnMessage
    public void onMessage(InputStream in) throws IOException {
        System.out.println("com.mycompany.mavenproject2.MyWebSocket.onMessage()");
        byte[] b = new byte[20];
        int x = in.read(b);
//        buffer.write(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println(
                buffer.toByteArray().length
        );
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen::" + session.getId());
    }

//    @OnClose
//    public void onClose(Session session) {
//        System.out.println("onClose::" + session.getId());
//    }
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage::From=" + session.getId() + " Message=" + message);

        try {
            session.getBasicRemote().sendText("Hello Client " + session.getId() + "!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("onError::" + t.getMessage());
    }
}
