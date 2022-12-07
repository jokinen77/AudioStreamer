/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.audiostreamer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jokin
 */
@WebSocket
public class SoundWebSocketHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(SoundWebSocketHandler.class);

    private static Map<Long, Session> sessions = new ConcurrentHashMap<>();
    private static ConcurrentLinkedDeque<byte[]> queue = new ConcurrentLinkedDeque<>();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        sessions.put(System.currentTimeMillis(), session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {

    }

    public static void broadcastSoundData(byte[] data) throws IOException {
        queue.addLast(data);
    }

    public static void startBroadcastThread() throws IOException {
        new Thread(() -> {
            while (true) {
                if (queue.isEmpty()) {
                    continue;
                }
                byte[] data = queue.pollFirst();
                for (Session session : sessions.values()) {
                    if (session.isOpen()) {
                        try {
                            session.getRemote().sendBytes(ByteBuffer.wrap(data));
                        } catch (IOException e) {
                            LOG.error("Cannot broadcast audio!", e);
                        }
                    }
                }
            }
        }).start();
    }

}
