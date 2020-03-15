package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.PunishModule;

import java.util.Collection;
import java.util.HashSet;

public class SettingsModule implements PunishModule {
	private final ModuleManager moduleManager;
	private final Collection<String> punishCommands = new HashSet<>();
	private final Collection<BotPlayer> pending = new HashSet<>();
	private Conditions conditions;
	private int delay;
	private boolean enabled = true;

	public SettingsModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		if (configYml != null) {
			final String name = getName();
			final int pps = configYml.getInt(name + ".conditions.pps", 0);
			final int cps = configYml.getInt(name + ".conditions.cps", 0);
			final int jps = configYml.getInt(name + ".conditions.jps", 0);

			this.enabled = configYml.getBoolean(name + ".enabled");
			this.punishCommands.clear();
			this.punishCommands.addAll(configYml.getStringList(name + ".commands"));
			this.conditions = new Conditions(pps, cps, jps, false);
			this.delay = configYml.getInt(name + ".delay", 5000);
		}
	}

	@Override
	public String getName() {
		return "settings";
	}

	@Override
	public final boolean meet(final int pps, final int cps, final int jps) {
		return this.enabled && conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS());
	}

	@Override
	public final boolean check(final Connection connection) {
		return true;
	}

	@Override
	public final Collection<String> getPunishCommands() {
		return punishCommands;
	}

	public Collection<BotPlayer> getPending() {
		return this.pending;
	}

	public void addPending(final BotPlayer botPlayer) {
		this.pending.add(botPlayer);
	}

	public void removePending(final BotPlayer botPlayer) {
		this.pending.remove(botPlayer);
	}

	public long getDelay() {
		return this.delay;
	}
}