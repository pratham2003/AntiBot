package somebody.is.madbro.handlers;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import somebody.is.madbro.AntiBotCore;
import somebody.is.madbro.datatrack.DataTrackCore;
import somebody.is.madbro.settings.Settings;

public class BotHandler {

	private AntiBotCore botclass = null;
	private DataTrackCore data = null;

	public BotHandler(AntiBotCore instance, DataTrackCore instance2) {
		// transfer instance for compatibility
		botclass = instance;
		data = instance2;
		// TODO Auto-generated constructor stub
	}

	public void onPlayerKick(PlayerKickEvent event) {
		if (!Settings.enabled) {
			return;
		}

		if (data.getBotTracker().autokick.contains(event.getPlayer().getName())) {
			event.setLeaveMessage(null);
			return;
		}

		if (data.getBotTracker().autoipkick.contains(event.getPlayer()
				.getAddress().toString().split(":")[0])) {
			event.setLeaveMessage(null);
			return;
		}

	}

	// falsified antibot trigger bug fix, or brolos bug fix.

	public void onPlayerQuit(PlayerQuitEvent event) {
		if (botclass.getHandler().getPermissions().hasPerms(event.getPlayer())) {
			return;
		} else {
			if (data.getBotTracker().botcts < 1) {
				return;
			} else {
				data.getBotTracker().botcts -= 1;
			}
		}
	}

	public void onPlayerMove(PlayerMoveEvent event) {
		if (!Settings.enabled) {
			return;
		}
		try {
			String pl = event.getPlayer().getName();
			if (data.getBotTracker().trackplayers.containsKey(pl)) {
				if (!(data.getBotTracker().trackplayers.get(pl)).hasMoved) {
					(data.getBotTracker().trackplayers.get(pl)).moved();
				}
			}
		} catch (Exception e) {
			botclass.getUtility()
					.getDebugUtility()
					.debug("Player " + event.getPlayer().getName()
							+ " did not get updated successfully for move.");
		}
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!Settings.enabled) {
			return;
		}
		try {
			botclass.getUtility().getDebugUtility()
					.debug("User is trying to connect..");
			data.getBotTracker().time = System.currentTimeMillis();

			if (botclass.getHandler().getPermissions()
					.hasPerms(event.getPlayer())) {
				botclass.getUtility().getDebugUtility().debug("Whitelisted.");
				if (data.getBotTracker().reanibo
						&& botclass
								.getHandler()
								.getPermissions()
								.ownPermission("AntiBot.Settings.notify",
										event.getPlayer(), 1)) {
					event.getPlayer().sendMessage(
							Settings.prefix + "\247c"
									+ Settings.connectInvasion);
				}
				if (data.getBotTracker().reanibo
						&& botclass
								.getHandler()
								.getPermissions()
								.ownPermission("AntiBot.admin.Settings.notify",
										event.getPlayer(), 2)
						&& Settings.interval > 100000) {
					event.getPlayer()
							.sendMessage(
									Settings.prefix
											+ "\247cThe system needs a flush. Please type /antibot flush. Thanks.");
				}
				return;
			}

			if (data.getBotTracker().autokick.contains(event.getPlayer()
					.getName())) {
				event.getPlayer().kickPlayer(Settings.kickMsg);
				event.setJoinMessage("");
				return;
			}

			if (!data.getBotTracker().reanibo) {
				// IP tracking usernames system.
				data.getBotTracker()
						.trackPlayer(
								event.getPlayer(),
								event.getPlayer().getAddress().toString()
										.split(":")[0]);
				botclass.getUtility().getDebugUtility()
						.debug("Added user to tracking");
				data.getBotTracker().addConnected(event.getPlayer().getName());
				botclass.getUtility().getDebugUtility()
						.debug("Added user to connected");
				data.getBotTracker().trackplayers.put(
						event.getPlayer().getName(),
						botclass.getDataTrack().getPlayer(
								event.getPlayer().getName(), this));
				botclass.getUtility().getDebugUtility()
						.debug("Added user to trackplayer");
			}

			if (data.getBotTracker().botcts > Settings.accounts + 2
					&& data.getBotTracker().reanibo) {
				Settings.accounts = Settings.accounts + 2;
				Settings.interval = Settings.interval + 5000;
			}

			// bug workaround
			if (Settings.interval < 1) {
				botclass.getUtility().getDebugUtility()
						.debug("Bug detected! Fixing bug.");
				// lets try setting this back to default Settings.intervals, if
				// not,
				// reload
				// the configuration.
				Settings.interval = botclass.getDefaultinterval();
				if (botclass.getDefaultinterval() < 1) {
					// have to fix.
					botclass.getSettings().loadSettings(
							botclass.getDataFolder());
				}
			}

			long math = data.getBotTracker().time
					- data.getBotTracker().lasttime;
			int cb = Settings.interval
					+ botclass.getUtility().getBotUtility()
							.getRandomIntInvasion();
			botclass.getUtility().getDebugUtility().debug("Checking....0");
			botclass.getUtility().getDebugUtility().debug("Math: " + math);
			botclass.getUtility().getDebugUtility()
					.debug("Time: " + data.getBotTracker().time);
			botclass.getUtility().getDebugUtility()
					.debug("Current Interval: " + Settings.interval);
			botclass.getUtility().getDebugUtility()
					.debug("Random Interval: " + cb);
			botclass.getUtility().getDebugUtility()
					.debug("Lasttime: " + data.getBotTracker().lasttime);
			botclass.getUtility()
					.getDebugUtility()
					.debug("BotCts: " + data.getBotTracker().botcts + " Accs: "
							+ Settings.accounts);

			if (data.getBotTracker().botcts > Settings.accounts && math < cb) {
				botclass.getUtility().getDebugUtility().debug("Hit #1!");
				// Incoming invasion.
				if (!data.getBotTracker().reanibo) {
					if (Settings.whiteList) {
						if (Settings.notify && Settings.whiteList) {
							botclass.getServer()
									.broadcastMessage(
											Settings.prefix
													+ "\247cOh no! A minecraft bot invasion has began. Connection Throttling: \247aEnabled");
						}
						data.getBotTracker().reanibo = true;
					} else {
						if (Settings.notify) {
							botclass.getServer()
									.broadcastMessage(
											Settings.prefix
													+ "\247chas detected minecraft spam!");
						}
					}
					botclass.getUtility().getDebugUtility()
							.debug("Tripswitched!");
					botclass.getUtility().getBotUtility().kickConnected();
					botclass.getUtility().getBotUtility().flush();
				}
				data.getBotTracker().botattempt = System.currentTimeMillis();
				data.getBotTracker().botcts += 1;
				event.getPlayer().kickPlayer(Settings.connectMsg);
				event.setJoinMessage("");
			} else if (data.getBotTracker().botattempt < Settings.interval
					&& data.getBotTracker().reanibo) {
				botclass.getUtility().getDebugUtility().debug("Hit #2");
				// Attempting to connect.
				data.getBotTracker().botattempt = System.currentTimeMillis();
				data.getBotTracker().botcts += 1;
				event.getPlayer().kickPlayer(Settings.connectMsg);
				event.setJoinMessage("");
			} else {
				botclass.getUtility().getDebugUtility().debug("Hit #3");

				if (data.getBotTracker().reanibo) {
					botclass.getUtility().getBotUtility().flush();
				}
				// No invasion.
				data.getBotTracker().lasttime = System.currentTimeMillis();
				data.getBotTracker().botcts += 1;
			}

			if (!botclass.getServer()
					.getOfflinePlayer(event.getPlayer().getName()).isOnline()) {
				event.setJoinMessage("");
			}

		} catch (Exception e) {
			botclass.getServer()
					.broadcastMessage(
							Settings.prefix
									+ "\247cAn error had occured! Please check console.");
			e.printStackTrace();
		}
	}

}
