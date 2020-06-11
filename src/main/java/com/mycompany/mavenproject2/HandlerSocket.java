/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject2;

import static com.mycompany.mavenproject2.MyWebSocket.lock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import org.apache.commons.io.IOUtils;

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
        sk.setTcpNoDelay(true);
        sk.setKeepAlive(true);
        System.out.println("tao ket noi 2");
        start();
    }

    @Override
    public void run() {

        try {
            ByteBuffer bufferout = null;
            byte[] buff = null;
            byte[] out = null;
            while (true) {
                if (sk.isConnected()) {
                    InputStream instr = sk.getInputStream();
                    int buffSize = sk.getReceiveBufferSize();
//                    int buffSize = 8192;
                    if (buffSize > 0) {
                        buff = new byte[buffSize];
                        System.out.println("read in server");
//                        Thread.sleep(1000);
                        int ret_read = instr.read(buff);
//                        Thread.sleep(1000);
                        if (ret_read != -1) {
                            out = Arrays.copyOfRange(buff, 0, ret_read);
                        }
                        if (ret_read == -1) {
                            System.out.println("close socket on server");
                            if (session.isOpen()) {
                                session.close();
                            }
                            if (sk.isConnected()) {
                                sk.close();
                            }
                            break;
                        }
                    }
                    bufferout = ByteBuffer.wrap(out);
                    System.out.println("send in s>c");
                    session.getBasicRemote().sendBinary(bufferout);
                }

            }
        } catch (Exception e) {
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
