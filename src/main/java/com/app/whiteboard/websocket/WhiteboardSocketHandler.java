package com.app.whiteboard.websocket;

import com.app.whiteboard.cluster.JGroupsClusterManager;
import com.app.whiteboard.crdt.Stroke;
import com.app.whiteboard.crdt.StrokeCRDT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WhiteboardSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final StrokeCRDT crdt;
    private final JGroupsClusterManager clusterManager;
    private final ObjectMapper mapper = new ObjectMapper();


    public WhiteboardSocketHandler(StrokeCRDT crdt, JGroupsClusterManager clusterManager) {
        this.crdt = crdt;
        this.clusterManager = clusterManager;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        String payload = mapper.writeValueAsString(crdt.getAllStrokes());
        session.sendMessage(new TextMessage(payload));
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var node = mapper.readTree(message.getPayload());
        String type = node.get("type").asText();
        if ("add".equals(type)) {
            Stroke s = mapper.treeToValue(node.get("stroke"), Stroke.class);
            crdt.addStroke(s);
            broadcastToLocalClients(message.getPayload());
            clusterManager.broadcastState(crdt);
        } else if ("remove".equals(type)) {
            String id = node.get("strokeId").asText();
            crdt.removeStroke(id);
            broadcastToLocalClients(message.getPayload());
            clusterManager.broadcastState(crdt);
        } else if ("clear".equals(type)) {
            crdt.clearAll(System.currentTimeMillis());
            broadcastToLocalClients(message.getPayload());
            clusterManager.broadcastState(crdt);
        }
    }


    private void broadcastToLocalClients(String payload) {
        sessions.forEach(s -> {
            try { s.sendMessage(new TextMessage(payload)); }
            catch (Exception e) {  }
        });
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
