package org.beethoven.lib.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-06-14
 */

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("[websocket] 客户端连接: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("[websocket] 收到消息: {}", message.getPayload());
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(message.getPayload()));
                } catch (Exception e) {
                    log.error("[websocket] 发送消息失败: sessionId={}", s.getId(), e);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("[websocket] 连接关闭: {}", session.getId());
    }
}
