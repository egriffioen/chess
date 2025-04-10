package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String visitorName;
    public Session session;
    public Integer gameID;

    public Connection(String visitorName, Session session, Integer gameID) {
        this.visitorName = visitorName;
        this.session = session;
        this.gameID = gameID;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}