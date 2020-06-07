/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject2;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public static Object lock = new Object();
    private static PushTimeService pst;
    private static List<HandlerSocket> listSocket = Collections.synchronizedList(new ArrayList<HandlerSocket>());
    static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    public void XyluPacket(HandlerSocket handlerSocket, ByteBuffer buffer) throws IOException {

        OutputStream wr = handlerSocket.sk.getOutputStream();
        byte[] arr = new byte[buffer.remaining()];
        buffer.get(arr);
        wr.write(arr);
        wr.flush();
        System.out.println("MyWebSocket c>s");
    }

    public ByteBuffer XyluPacketLogin(HandlerSocket handlerSocket, ByteBuffer buffer) throws IOException {

        ByteBuffer bufferout = null;
        if (handlerSocket.sk.isConnected()) {
            InputStream instr = handlerSocket.sk.getInputStream();
            int buffSize = 8192;
            byte[] buff = null;
            byte[] out = null;
            if (buffSize > 0) {
                buff = new byte[buffSize];
                int ret_read = instr.read(buff);
                if (ret_read != -1) {
                    out = new byte[ret_read];
                    for (int i = 0; i < ret_read; i++) {
                        out[i] = buff[i];
                    }
                }
            }
            bufferout = ByteBuffer.wrap(out);

        }
        return bufferout;
    }

    @OnMessage
    public void onMessage(ByteBuffer buffer, Session session) throws IOException, EncodeException {

        try {
            for (HandlerSocket handlerSocket : listSocket) {
                if (session.getId().equals(handlerSocket.idsession)) {
                    System.out.println("MyWebSocket chay onmsg loop");
                    XyluPacket(handlerSocket, buffer);
                    return;
                }
            }
            System.out.println("MyWebSocket chay onmsg login 0");
            HandlerSocket handlerSocket = new HandlerSocket(session.getId(), session);

            listSocket.add(handlerSocket);
            System.out.println("MyWebSocket chay onmsg login 1");
            ByteBuffer bufferout = XyluPacketLogin(handlerSocket, buffer);
            session.getBasicRemote().sendBinary(bufferout);
            handlerSocket.start();
        } catch (Exception e) {
            System.out.println("loi"+e.getMessage());
        }

//        for (Session peer : peers) {
//            if (session.getId().equals(peer.getId())) { // do not resend the message to its sender
//                System.out.println("chay onmsg ser");
//                byte[] b = new byte[20];
////                int x = in.read(b);
//                ByteBuffer buffer1 = ByteBuffer.wrap(b);
//                session.getBasicRemote().sendBinary(buffer1);
//            }
//
//        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
    }

    @OnOpen
    public void onOpen(Session session) {
        peers.add(session);
        System.out.println("onOpen::=" + session.getId());
    }

//    @OnClose
//    public void onClose(Session session) {
//        System.out.println("onClose::" + session.getId());
//    }
    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {
        System.out.println("onMessage::From=" + session.getId() + " Message=" + message);
        try {
            session.getBasicRemote().sendText("1Hello Client " + session.getId() + "!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("onError::" + t.getMessage());
    }
}
