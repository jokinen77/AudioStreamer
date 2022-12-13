/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.audiostreamer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author jokin
 */
@WebSocket
public class SoundWebSocketHandler {

    private static final List<Session> sessions = new ArrayList<>();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        synchronized (sessions) {
            sessions.add(session);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        synchronized (sessions) {
            sessions.removeIf((sess) -> !sess.isOpen());
        }
    }

    public static void broadcastSoundData(byte[] data) throws IOException {
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getRemote().sendBytesByFuture(ByteBuffer.wrap(data));
            }
        }
    }

}
