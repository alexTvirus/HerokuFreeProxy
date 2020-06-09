/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject2;

import static com.mycompany.mavenproject2.MyWebSocket.lock;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;

/**
 *
 * @author Alex
 */
public class HandlerSocket extends Thread {

    public Socket sk;
    public String idsession;
    public Session session;

    public HandlerSocket(String idsession, Session session) throws UnknownHostException, IOException {
        this.idsession = idsession;
        this.session = session;
        // tạo socket kết nối đến muserver thông quang port 55901 , muserver đã chạy và open cổng 55901

        System.out.println("tao ket noi 1");
        this.sk = new Socket("127.0.0.1", 55901);
        System.out.println("tao ket noi 2");
    }

    @Override
    public void run() {
    }

    public Socket getSk() {
        return sk;
    }

    public void setSk(Socket sk) {
        this.sk = sk;
    }

    public String getIdsession() {
        return idsession;
    }

    public void setIdsession(String idsession) {
        this.idsession = idsession;
    }

}
