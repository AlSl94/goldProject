package org.goldgame.utilities;


import org.goldgame.httpServer.HttpGameServer;

import java.io.IOException;

public class GameManager {

    private GameManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void defaultManager() throws IOException {
        SqlSetup.loadDriver();
        HttpGameServer httpGameServer = new HttpGameServer();
        httpGameServer.load();
    }
}