package com.example.backend.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session , @PathParam("sid")  String sid) {
        SESSIONS.put(sid, session);
        log.info("WS 连接建立 sid={} 在线数={}", sid, SESSIONS.size());
    }

    @OnClose
    public void onClose(Session session , @PathParam("sid") String sid) {
        SESSIONS.remove(sid);
        log.info("WS 断开 sid={} 在线数={}", sid, SESSIONS.size());
    }

    @OnMessage
    public void onMessage(String message, Session session , @PathParam("sid") String sid) {
        log.info("WS 收到消息 sid={} msg={}", sid, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WS 错误 sid={} ", session.getId(), error);
    }

    public static synchronized void broadcast(String message) {
        if ( SESSIONS.isEmpty()) {
            log.info("信息为空");
            return;
        }
        for (Map.Entry<String, Session> entry : SESSIONS.entrySet()) {
            try {
                entry.getValue().getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.warn("WS 发送失败，移除连接 sid={}", entry.getKey());
                SESSIONS.remove(entry.getKey());
            }
        }
        log.info("WS 广播完成 在线数={} 消息={}", SESSIONS.size() , message);
    }
}
