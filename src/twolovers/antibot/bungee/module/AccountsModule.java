package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IPunishModule;

import java.util.*;

public class AccountsModule implements IPunishModule {
	private static final String NAME = "accounts";
	private final ModuleManager moduleManager;
	private Collection<String> punishCommands = new HashSet<>();
	private Conditions conditions;
	private boolean enabled = true;
	private int limit = 2;

	public AccountsModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(NAME + ".conditions.pps", 0);
		final int cps = configYml.getInt(NAME + ".conditions.cps", 0);
		final int jps = configYml.getInt(NAME + ".conditions.jps", 0);

		enabled = configYml.getBoolean(NAME + ".enabled", enabled);
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(NAME + ".commands"));
		conditions = new Conditions(pps, cps, jps, false);
		limit = configYml.getInt(NAME + ".limit", limit);
	}

	public boolean meet(final int pps, final int cps, final int jps) {
		return this.enabled && conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS());
	}

	public boolean check(final Connection connection) {
		if (connection instanceof PendingConnection) {
			final PlayerModule playerModule = moduleManager.getPlayerModule();
			final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());

			return botPlayer.getTotalAccounts() >= limit;
		}

		return false;
	}

	public boolean meetCheck(int pps, int cps, int jps, Connection connection) {
		return meet(pps, cps, jps) && check(connection);
	}

	@Override
	public Collection<String> getPunishCommands() {
		return punishCommands;
	}
}
