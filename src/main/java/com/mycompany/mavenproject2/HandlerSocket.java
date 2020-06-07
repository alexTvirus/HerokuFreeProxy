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
        this.sk = new Socket("0.0.0.0", 55901);
        sk.setKeepAlive(true);
        sk.setTcpNoDelay(true);
    }

    @Override
    public void run() {
        try {
            ByteBuffer bufferout = null;
            while (true) {
                if (sk.isConnected()) {
                    InputStream instr = sk.getInputStream();
                    int buffSize = 8192;
                    byte[] buff = null;
                    byte[] out = null;
                    if (buffSize > 0) {
                        buff = new byte[buffSize];
                        System.out.println("HandlerSocket s>c0");
                        int ret_read = instr.read(buff);
                        System.out.println("HandlerSocket s>c1");
                        if (ret_read != -1) {
                            out = new byte[ret_read];
                            for (int i = 0; i < ret_read; i++) {
                                out[i] = buff[i];
                            }
                            bufferout = ByteBuffer.wrap(out);
                            this.session.getBasicRemote().sendBinary(bufferout);
                            System.out.println("HandlerSocket s>c2");
                        }
                        if (ret_read == -1) {
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

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
