package com.afms.cahgame.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TaskService extends Service {

    private static String lobbyId;

    public static void setLobbyId(String lobbyId) {
        TaskService.lobbyId = lobbyId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("Close", String.format("ID: %d Name: %s", lobbyId, Util.playerName));
        Database.removePlayerFromLobby(lobbyId, Util.playerName);
        stopSelf();
    }
}
