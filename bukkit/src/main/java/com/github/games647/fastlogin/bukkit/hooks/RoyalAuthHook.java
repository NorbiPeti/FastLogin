package com.github.games647.fastlogin.bukkit.hooks;

import org.bukkit.entity.Player;
import org.royaldev.royalauth.AuthPlayer;
import org.royaldev.royalauth.Config;

/**
 * Github: https://github.com/RoyalDev/RoyalAuth
 *
 * Project page:
 *
 * Bukkit: http://dev.bukkit.org/bukkit-plugins/royalauth/
 */
public class RoyalAuthHook implements BukkitAuthPlugin {

    @Override
    public void forceLogin(Player player) {
        AuthPlayer authPlayer = AuthPlayer.getAuthPlayer(player);

//https://github.com/RoyalDev/RoyalAuth/blob/master/src/main/java/org/royaldev/royalauth/commands/CmdLogin.java#L62
        //not thread-safe
        authPlayer.login();
    }

    @Override
    public boolean isRegistered(String playerName) {
        AuthPlayer authPlayer = AuthPlayer.getAuthPlayer(playerName);
        return authPlayer.isRegistered();
    }

    @Override
    public void forceRegister(Player player, String password) {
//https://github.com/RoyalDev/RoyalAuth/blob/master/src/main/java/org/royaldev/royalauth/commands/CmdRegister.java#L50
        AuthPlayer authPlayer = AuthPlayer.getAuthPlayer(player);
        authPlayer.setPassword(password, Config.passwordHashType);

        //login in the player after registration
        forceLogin(player);
    }
}
