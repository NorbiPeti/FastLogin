package com.github.games647.fastlogin.bungee.hooks;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.vik1395.BungeeAuth.ListenerClass;
import me.vik1395.BungeeAuth.Main;
import me.vik1395.BungeeAuth.Password.PasswordHandler;
import me.vik1395.BungeeAuth.Tables;
import net.md_5.bungee.api.ProxyServer;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Github: https://github.com/MatteCarra/BungeeAuth
 *
 * Project page:
 *
 * Spigot: https://www.spigotmc.org/resources/bungeeauth.493/
 */
public class BungeeAuthHook implements BungeeAuthPlugin {

    //https://github.com/MatteCarra/BungeeAuth/blob/master/src/me/vik1395/BungeeAuth/Login.java#L32
    private final Tables databaseConnection = new Tables();

    @Override
    public void forceLogin(final ProxiedPlayer player) {
//https://github.com/MatteCarra/BungeeAuth/blob/master/src/me/vik1395/BungeeAuth/Login.java#L92-95
        Main.plonline.add(player.getName());

        //renamed from ct to databaseConnection
//            databaseConnection.setStatus(player.getName(), "online");
        final Class<?>[] parameterTypes = new Class<?>[]{String.class, String.class};
        final Object[] arguments = new Object[]{player.getName(), "online"};

        ProxyServer.getInstance().getScheduler().runAsync(Main.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    callProtected("setStatus", parameterTypes, arguments);
                    ListenerClass.movePlayer(player, false);

                    ProxyServer.getInstance().getScheduler().schedule(Main.plugin, new Runnable() {
                        @Override
                        public void run() {
                            //not thread-safe
                            ListenerClass.prelogin.get(player.getName()).cancel();
                        }
                    }, 0, TimeUnit.SECONDS);
                } catch (Exception ex) {
                    Main.plugin.getLogger().severe("[BungeeAuth] Error force loging in player");
                }
            }
        });
    }

    @Override
    public boolean isRegistered(String playerName) {
        //https://github.com/MatteCarra/BungeeAuth/blob/master/src/me/vik1395/BungeeAuth/Register.java#L46
        //renamed t to databaseConnection
        return databaseConnection.checkPlayerEntry(playerName);
    }

    @Override
    public void forceRegister(final ProxiedPlayer player, String password) {
        //https://github.com/MatteCarra/BungeeAuth/blob/master/src/me/vik1395/BungeeAuth/Register.java#L102
        PasswordHandler ph = new PasswordHandler();
        Random rand = new Random();
        int maxp = 7; //Total Password Hashing methods.
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        String Pw = password;
        String pType = "" + rand.nextInt(maxp + 1);
        String regdate = ft.format(dNow);
        //https://github.com/MatteCarra/BungeeAuth/blob/master/src/me/vik1395/BungeeAuth/Register.java#L60
        String lastip = player.getAddress().getAddress().getHostAddress();
        String lastseen = regdate;
        String hash = ph.newHash(Pw, pType);

        //creates a new SQL entry with the player's details.

        //renamed t to databaseConnection
//            databaseConnection.newPlayerEntry(player.getName(), hash, pType, "", lastip, regdate, lastip, lastseen);

        final Class<?>[] parameterTypes = new Class<?>[] {String.class, String.class, String.class, String.class
                , String.class, String.class, String.class, String.class};
        final Object[] arguments = new Object[] {player.getName(), hash, pType, "", lastip, regdate, lastip, lastseen};

        ProxyServer.getInstance().getScheduler().runAsync(Main.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    callProtected("newPlayerEntry", parameterTypes, arguments);

                    ProxyServer.getInstance().getScheduler().schedule(Main.plugin, new Runnable() {
                        @Override
                        public void run() {
                            //proparly not thread-safe
                            forceLogin(player);
                        }
                    }, 0, TimeUnit.SECONDS);
                } catch (Exception ex) {
                    Main.plugin.getLogger().severe("[BungeeAuth] Error when creating a new player in the Database");
                }
            }
        });
    }

    //pail ;(
    private void callProtected(String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Exception {
        Class<? extends Tables> tableClass = databaseConnection.getClass();

        Method method = tableClass.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        method.invoke(databaseConnection, arguments);
    }
}
