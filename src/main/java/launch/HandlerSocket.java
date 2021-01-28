/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package launch;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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

//    private Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();
//    private Thread rateThread; //rate publisher thread
    public HandlerSocket(String idsession, final Session session) throws UnknownHostException, IOException {
        this.idsession = idsession;
        this.session = session;
    }

    public void startconnect() {
        try {
            // tạo socket kết nối đến muserver thông quang port 55901 , muserver đã chạy và open cổng 55901

            this.sk = new Socket("127.0.0.1", 55901);
//        sk.setTcpNoDelay(true);
            sk.setKeepAlive(true);
            sk.setSoTimeout(45000);
        } catch (Exception e) {
            Logger.getLogger(HandlerSocket.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void run() {

        ByteBuffer bufferout = null;
        byte[] buff = null;
        byte[] out = null;
        byte[] out2 = new byte[1];
        out2[0] = -1;
        InputStream instr = null;
        int buffSize = 0;
        try {

            while (true) {
                if (sk.isConnected()) {
                    instr = sk.getInputStream();
                    buffSize = sk.getReceiveBufferSize();
//                    int buffSize = 131072;
                    if (buffSize > 0) {
                        buff = new byte[buffSize];
//                        Thread.sleep(1000);
                        int ret_read = instr.read(buff);
//                        Thread.sleep(1000);
                        if (ret_read != -1) {
                            out = Arrays.copyOfRange(buff, 0, ret_read);
                        }

                        if (ret_read == -1) {
//                            System.out.println("close socket on server");
//                            if (sk.isConnected()) {
//                                sk.close();
//                            }
//                            if (session.isOpen()) {
//                                session.close();
//                            }

                            bufferout = ByteBuffer.wrap(out2);
                            //System.out.println("send in s>c");
                            session.getBasicRemote().sendBinary(bufferout);
                            return;
                        }
                        bufferout = ByteBuffer.wrap(out);
                        //System.out.println("send in s>c");
                        session.getBasicRemote().sendBinary(bufferout);
                    } else {
                        break;
                    }
                } else {
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(out2));
                    break;
                }
            }
            try {
                session.getBasicRemote().sendBinary(ByteBuffer.wrap(out2));
            } catch (IOException ex) {
                Logger.getLogger(HandlerSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(out2));
                }
            } catch (IOException ex) {
                Logger.getLogger(HandlerSocket.class.getName()).log(Level.SEVERE, null, ex);
            }

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
