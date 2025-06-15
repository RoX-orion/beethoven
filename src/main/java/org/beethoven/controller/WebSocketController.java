package org.beethoven.controller;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-06-14
 */

@Slf4j
@Component
@ServerEndpoint("/apiws")
public class WebSocketController {

    private Session session;

    @OnMessage
    public void onMessage(String message) {
        log.info("[websocket] 收到消息：id={}，message={}", this.session.getId(), message);

        this.session.getAsyncRemote().sendText("["+ Instant.now().toEpochMilli() +"] Hello " + message);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig){
        this.session = session;
        log.info("[websocket] 新的连接：id={}", this.session.getId());
    }

    @OnClose
    public void onClose(CloseReason closeReason){
        log.info("[websocket] 连接断开：id={}，reason={}", this.session.getId(),closeReason);
    }

    @OnError
    public void onError(Throwable throwable) throws IOException {
        log.info("[websocket] 连接异常：id={}，throwable={}", this.session.getId(), throwable.getMessage());

        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }
}
