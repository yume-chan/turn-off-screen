package com.example.test;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.util.Log;

import dalvik.system.PathClassLoader;

public class Main {
    private static void log(String value) {
        System.out.println(value);
        Log.d("Test", value);
    }

    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi", "DiscouragedPrivateApi", "BlockedPrivateApi"})
    public static void main(String[] args) {
        try {
            log("Start");

            var mode = 0; // OFF
            if (args.length == 1) {
                mode = Integer.parseInt(args[0]);
            }
            log("Mode: " + mode);

            var classLoaderFactoryClass = Class.forName("com.android.internal.os.ClassLoaderFactory");
            var createClassLoaderMethod = classLoaderFactoryClass.getDeclaredMethod("createClassLoader", String.class, String.class, String.class, ClassLoader.class, int.class, boolean.class, String.class);
            var classLoader = (PathClassLoader) createClassLoaderMethod.invoke(null, "/system/framework/services.jar", "", "", null, 34, true, null);

            var displayControlClass = classLoader.loadClass("com.android.server.display.DisplayControl");
            log("displayControlClass: " + displayControlClass);

            var loadLibraryMethod = Runtime.class.getDeclaredMethod("loadLibrary0", Class.class, String.class);
            loadLibraryMethod.setAccessible(true);
            loadLibraryMethod.invoke(Runtime.getRuntime(), displayControlClass, "android_servers");

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
