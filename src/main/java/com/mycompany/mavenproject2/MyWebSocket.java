/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject2;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/endpoint")
public class MyWebSocket {

    public static List<HandlerSocket> listSocket = Collections.synchronizedList(new ArrayList<HandlerSocket>());
    static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    public void XyluPacketx(HandlerSocket handlerSocket, ByteBuffer buffer) throws IOException, Exception {
        // mục đích là đẩy packet của mu client vào muserver
        OutputStream wr = handlerSocket.sk.getOutputStream();
        byte[] arr = new byte[buffer.remaining()];
        buffer.get(arr);
        wr.write(arr);
        wr.flush();
    }

    @OnMessage
    public void onMessage(ByteBuffer buffer, Session session) throws IOException, EncodeException {

        try {
            for (HandlerSocket handlerSocket : listSocket) {
                if (session.getId().equals(handlerSocket.idsession)) {
                    System.out.println("MyWebSocket chay onmsg loop");
                    XyluPacketx(handlerSocket, buffer);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("loi" + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        peers.add(session);
        HandlerSocket handlerSocket = new HandlerSocket(session.getId(), session);
        listSocket.add(handlerSocket);
        System.out.println("onOpen::=" + session.getId());
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // tắt socket tương ứng với id session , trường hợp này xảy ra khi client tắt kết nối
        System.out.println("onClose::" + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {
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
