package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.BlacklistModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.RateLimitModule;

public class PlayerHandshakeListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;

	public PlayerHandshakeListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onPlayerHandshake(final PlayerHandshakeEvent event) {
		try {
			final PendingConnection connection = event.getConnection();

			if (connection.isConnected()) {
				final int requestedProtocol = event.getHandshake().getRequestedProtocol();
				final BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
				final RateLimitModule rateLimitModule = moduleManager.getRateLimitModule();
				final String ip = connection.getAddress().getHostString();
				int currentPPS = moduleManager.getCurrentPPS();
				int currentCPS = moduleManager.getCurrentCPS();
				final int currentJPS = moduleManager.getCurrentJPS();

				if (requestedProtocol == 1) {
					currentPPS++;
				} else {
					currentCPS++;
				}

				if (rateLimitModule.meetCheck(connection)) {
					new Punish(plugin, moduleManager, "en", rateLimitModule, connection, event);
					blacklistModule.setBlacklisted(ip, true);
				} else if (blacklistModule.meetCheck(currentPPS, currentCPS, currentJPS, connection)) {
					new Punish(plugin, moduleManager, "en", blacklistModule, connection, event);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
