package com.example.test;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.util.Log;

public class Main {
    private static void log(String value) {
        System.out.println(value);
        Log.d("Test", value);
    }

    public static void main(String[] args) {
        try {
            log("Start");

            var mode = 0; // OFF
            if (args.length == 1) {
                mode = Integer.parseInt(args[0]);
            }
            log("Mode: " + mode);

            System.loadLibrary("android_servers");
            log("Loaded libandroiod_servers.so");

            @SuppressLint("PrivateApi") var displayControlClass = Class.forName("com.android.server.display.DisplayControl");
            log("displayControlClass: " + displayControlClass);

            var getPhysicalDisplayIdsMethod = displayControlClass.getDeclaredMethod("getPhysicalDisplayIds");
            log("getPhysicalDisplayIdsMethod: " + getPhysicalDisplayIdsMethod);

            var getPhysicalDisplayTokenMethod = displayControlClass.getDeclaredMethod("getPhysicalDisplayToken", long.class);
            log("getPhysicalDisplayTokenMethod: " + getPhysicalDisplayTokenMethod);

            var surfaceControlClass = Class.forName("android.view.SurfaceControl");
            log("surfaceControlClass: " + surfaceControlClass);

            @SuppressLint("SoonBlockedPrivateApi") var setDisplayPowerModeMethod = surfaceControlClass.getDeclaredMethod("setDisplayPowerMode", IBinder.class, int.class);
            log("setDisplayPowerModeMethod: " + setDisplayPowerModeMethod);

            var displayIds = (long[]) getPhysicalDisplayIdsMethod.invoke(null);
            log("displayIds.length: " + displayIds.length);

            for (long displayId : displayIds) {
                log("displayId: " + displayId);

                var token = (IBinder) getPhysicalDisplayTokenMethod.invoke(null, displayId);
                log("token: " + token);

                setDisplayPowerModeMethod.invoke(null, token, mode);
                log("setDisplayPowerMode success");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
