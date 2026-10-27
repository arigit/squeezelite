/*
 *  Squeezelite Android
 *
 *  (c) Craig Drummond 2025-2026 <craig.p.drummond@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.lyrion.squeezelite;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Utils {
    public static final String LOG_TAG = "SqueezeliteUI";
    // Debug builds also write the log to a file (and <name>.1), as the phone's log only keeps a few hours. One file
    // per process: the player service runs in its own.
    private static final long MAX_LOG_FILE_SIZE = 4 * 1024 * 1024;
    private static File logFile;
    private static ExecutorService logExecutor;

    public static synchronized void startFileLog(Context context, String name) {
        if (BuildConfig.DEBUG && null==logFile) {
            logFile = new File(context.getFilesDir(), name);
            logExecutor = Executors.newSingleThreadExecutor();
        }
    }

    private static void logToFile(String level, String message, Throwable t) {
        if (null==logExecutor) {
            return;
        }
        long time = System.currentTimeMillis();
        String thrown = null==t ? "" : "\n" + Log.getStackTraceString(t);
        logExecutor.execute(() -> {
            if (logFile.length() > MAX_LOG_FILE_SIZE) {
                logFile.renameTo(new File(logFile.getPath() + ".1"));
            }
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US).format(new Date(time)) +
                        " " + level + " " + message + thrown + "\n");
            } catch (IOException ignored) {
            }
        });
    }

    static public boolean notificationAllowed(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            debug("Check if notif permission granted");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                debug("No notif permission");
                return false;
            }
        }
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            debug("Notifs are disabled");
            return false;
        }
        if (channelId != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = mgr.getNotificationChannel(channelId);
            if (null!=channel) {
                debug("Channel " + channelId + " importance " + channel.getImportance());
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
        }
        debug("Notifs are allowed");
        return true;
    }

    public static boolean isPlayerRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // Android Auto may hold the service bound without a player, so only a started one counts
            if (service.started && service.service.getClassName().equals(PlayerService.class.getCanonicalName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        return null==str || str.isEmpty();
    }

    public static int toInt(String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public static class BtDevice implements Comparable {
        public BtDevice(String name, String mac) {
            this.name = name;
            this.mac = mac;
        }
        public String name;
        public String mac;

        @Override
        public int compareTo(Object o) {
            if (null==name) { // Should never happen, but...
                if (null==mac) {
                    return -1;
                }
                return mac.compareTo(((BtDevice)o).mac);
            }
            return name.compareTo(((BtDevice)o).name);
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static String getName(BluetoothDevice dev) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String alias = dev.getAlias();
            if (!isEmpty(alias)) {
                return alias;
            }
        }

        String name = dev.getName();
        if (!isEmpty(name)) {
            return name;
        }

        String mac = dev.getAddress();
        if (!isEmpty(mac)) {
            return mac;
        }
        return "?";
    }

    public static BtDevice getConnectedDevice(Context context) {
        return getConnectedDevice(context, null, null);
    }

    // First connected device whose MAC is in macs (any, if null), other than exceptMac
    public static BtDevice getConnectedDevice(Context context, Set<String> macs, String exceptMac) {
        BluetoothManager btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            Method isConnectedMethod;
            try {
                isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                isConnectedMethod.setAccessible(true);
            } catch (Exception e) {
                Utils.error("Failed to get BluetoothDevice.isConnected method", e);
                return null;
            }
            Set<BluetoothDevice> bonded = btManager.getAdapter().getBondedDevices();
            for (BluetoothDevice dev: bonded) {
                String mac = dev.getAddress();
                if ((null!=macs && !macs.contains(mac)) || (null!=exceptMac && exceptMac.equals(mac))) {
                    continue;
                }
                try {
                    if ((boolean) isConnectedMethod.invoke(dev, (Object[]) null)) {
                        return new BtDevice(getName(dev), dev.getAddress());
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private static String logPrefix() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        if (st.length>4) {
            // Remove org.lyrion.squeezelite.
            return "["+st[4].getClassName().substring(23)+"."+st[4].getMethodName()+"] ";
        }
        return "";
    }

    public static void verbose(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(LOG_TAG, logPrefix() + message);
        }
    }

    public static void debug(String message) {
        if (BuildConfig.DEBUG) {
            String line = logPrefix() + message;
            Log.d(LOG_TAG, line);
            logToFile("D", line, null);
        }
    }

    public static void info(String message) {
        if (BuildConfig.DEBUG) {
            String line = logPrefix() + message;
            Log.i(LOG_TAG, line);
            logToFile("I", line, null);
        }
    }

    public static void warn(String message) {
        if (BuildConfig.DEBUG) {
            String line = logPrefix() + message;
            Log.w(LOG_TAG, line);
            logToFile("W", line, null);
        }
    }

    public static void warn(String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            String line = logPrefix() + message;
            Log.w(LOG_TAG, line, t);
            logToFile("W", line, t);
        }
    }

    public static void error(String message) {
        if (BuildConfig.DEBUG) {
            String line = logPrefix() + message;
            Log.e(LOG_TAG, line);
            logToFile("E", line, null);
        }
    }

    public static void error(String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            String line = logPrefix() + message;
            Log.e(LOG_TAG, line, t);
            logToFile("E", line, t);
        }
    }
}
