package somebody.is.mad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Player;
//import org.bukkit.event.player.PlayerChatEvent;
//import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class BotListener implements Listener {

	private final AntiBot botclass;
	public long time;
	public long lasttime;
	public long botattempt;
	public int interval = 5000;
	public int accounts = 4;
	public int spamam = 4;
	public int spamtime = 1500;
	public int connectFor = 30000;
	public boolean notify = true;
	public boolean useWhiteListPerms = true;
	public boolean useOpPerms = false;
	public boolean enabled = true;
	public boolean debugmode = false;
	public boolean whiteList = false;
	public boolean silentChatKick = true;
	public boolean banUsers = false;
	public ArrayList<String> autokick = new ArrayList<String>();
	public ArrayList<String> autoipkick = new ArrayList<String>();
	public ArrayList<String> spammyPlayers = new ArrayList<String>();
	public ArrayList<String> connected = new ArrayList<String>();
	public HashMap<String, PlayerTrack> trackplayers = new HashMap<String, PlayerTrack>();
	public String kickMsg = "The Ban Hammer has spoken!";
	public String connectMsg = "You are not on the whitelist!";
	public String connectInvasion = "The server is currently under attack.";
	public String lastMessage = "";
	public String prefix = "\247f[\247bAntiBot\247f] ";
	public int spamcts = 0;
	public int botcts;
	public boolean reanibo = false;

	public BotListener(AntiBot instance) {
		this.botclass = instance;
	}

	public void debug(String msg) {
		if (debugmode) {
			botclass.getServer().broadcastMessage(
					prefix + "" + msg);
		}
	}

	public void addConnected(String playerName) {
		try {
			if (!connected.contains(playerName)) {
				connected.add(playerName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			debug("Adding new player failed: " + e.getMessage());
		}
	}

	public boolean checkConnection(String usr) {
		if(trackplayers.containsKey(usr)) {
			PlayerTrack mp = (PlayerTrack) trackplayers.get(usr);
			if(mp.connectedForLonger()) {
				return true;
			}
		}
		return false;
	}

	public void kickConnected() {
		// int kicked = 0;
		debug("Kicking players with method #1 Size: " + connected.size());
		for (String pl : connected) {
			try {
				debug("Kicking player..." + pl);
				Player p2 = botclass.getServer().getPlayerExact(pl);
				if (!checkConnection(pl)) {
					botclass.getServer().getPlayerExact(pl).kickPlayer(kickMsg);
					if(banUsers) {
						autoipkick.add(p2.getAddress().toString().split(":")[0]);
						autokick.add(pl);
					}
					// kicked += 1;
					debug("Kicked player with method #1");
					debug("We now have autokick: " + autokick.size() + " ip: "
							+ autoipkick.size());
				} else {
					connected.remove(pl);
				}
			} catch (Exception e) {
				// if it fails. go down here.
				debug("Failed to kick: " + pl);
			}
		}
		connected.clear();

		// kick players if the above method doesn't work :|
		/*
		 * debug("Checking if " + kicked + " is less than 1"); if (kicked < 1) {
		 * debug("Kicking player with method #2"); Player[] players =
		 * botclass.getServer().getOnlinePlayers(); for (Player pl : players) {
		 * if (!hasPerms(pl)) { pl.kickPlayer(connectMsg); autokick.add(pl);
		 * debug("Kicked player with method #2"); } } }
		 */

	}

	public boolean useWhitelist(Player pl) {
		if (useWhiteListPerms) {
			return pl.isWhitelisted();
		} else {
			return false;
		}
	}

	public boolean useOp(Player pl) {
		if (useOpPerms) {
			return pl.isOp();
		} else {
			return false;
		}
	}

	public boolean hasPerms(Player pl) {
		if (useOp(pl) || useWhitelist(pl)
				|| botclass.ownPermission("AntiBot.join", pl, 1)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean flush() {
		try {
			if (reanibo) {
				debug("Disabled Reanibios.");
				reanibo = false;
				interval = botclass.defaultinterval;
				connected.clear();
				accounts = botclass.defaultaccounts;
				lasttime = 0;
				botattempt = 0;
				if (notify && whiteList) {
					botclass.getServer()
							.broadcastMessage(
									prefix + "\247aThe minecraft bot invasion has ended. Connection Throttling: \247cDisabled");
				}
			}
			botcts = 0;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean flush2() {
		try {
			reanibo = false;
			interval = botclass.defaultinterval;
			connected.clear();
			autokick.clear();
			autoipkick.clear();
			accounts = botclass.defaultaccounts;
			lasttime = 0;
			botattempt = 0;
			if (notify && whiteList) {
				botclass.getServer()
						.broadcastMessage(
								prefix + "\247aThe minecraft bot invasion has ended. Connection Throttling: \247cDisabled");
			}
			botcts = 0;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean toggle(Boolean e) {
		try {
			enabled = e;
			return true;
		} catch (Exception fe) {
			return false;
		}
	}

	public int getRandomInt() {
		if (!reanibo) {
			return 0;
		}
		Random rdm = new Random();
		if (interval > 35000) {
			return rdm.nextInt(7000);
		} else if (interval > 45000) {
			return rdm.nextInt(25000);
		}
		return rdm.nextInt(5000);
	}

	public void trackPlayer(Player ev, String IP) {
		if (!botclass.iplist.isEmpty()) {
			// check ipmap list.
			boolean kicked = false;
			try {
				if (botclass.iplist.containsKey(IP)) {
					if (!botclass.iplist.get(IP).contains(ev.getName())) {
						ev.kickPlayer(kickMsg);
						kicked = true;
						if(banUsers) {
							autokick.add(ev.getName());
							autoipkick.add(IP);
						}
						botclass.iplist.remove(IP);
					}
				}
			} catch (Exception e) {
				// didn't find player.
			}

			// still here? add us.
			if (!kicked)
				botclass.iplist.put(IP, ev.getName());
		} else {
			botclass.iplist.put(IP, ev.getName());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		if (!enabled) {
			return;
		}

		// spammy chat kick.
		if (silentChatKick) {
			if (event.getReason().contains("C: ")) {
				event.setReason(event.getReason().replace("C: ", ""));
				event.setLeaveMessage(null);
				return;
			}
		}

		if (autokick.contains(event.getPlayer().getName())) {
			event.setLeaveMessage(null);
			return;
		}

		if (autoipkick.contains(event.getPlayer().getAddress().toString()
				.split(":")[0])) {
			event.setLeaveMessage(null);
			return;
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		if (autoipkick.contains(event.getAddress().toString().split(":")[0])) {
			event.disallow(Result.KICK_BANNED, kickMsg);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		try {
			if (!enabled) {
				return;
			}
			String pN = event.getPlayer().getName();
			if (autokick.contains(event.getPlayer().getName())) {
				event.getPlayer().kickPlayer(kickMsg);
				event.setCancelled(true);
				return;
			}

			if (autoipkick.contains(event.getPlayer().getAddress().toString()
					.split(":")[0])) {
				event.getPlayer().kickPlayer(kickMsg);
				event.setCancelled(true);
				return;
			}

			if (hasPerms(event.getPlayer())) {
				return;
			}

			if (!trackplayers.containsKey(pN)) {
				trackplayers.put(pN, new PlayerTrack(pN, this));
			} else {
				try {
					PlayerTrack pc = trackplayers.get(pN);
					long math = System.currentTimeMillis() - pc.lastChatMsg;
					if (pc.amoumt > spamam && math < spamtime) {
						if (notify) {
							botclass.getServer()
									.broadcastMessage(
											prefix + "\247chas detected chat spam!");
						}
						trackplayers.remove(pN);
						event.getPlayer().kickPlayer("C: " + kickMsg);
						event.setCancelled(true);
					} else {
						pc.trig();
					}
				} catch (Exception e) {

				}
			}

		} catch (Exception e) {
			// alright, it failed. Don't worry about it.
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent event) {
		try {
			if (!enabled) {
				return;
			}
			String pN = event.getPlayer().getName();
			if (autokick.contains(event.getPlayer().getName())) {
				event.getPlayer().kickPlayer(kickMsg);
				event.setCancelled(true);
				return;
			}

			if (autoipkick.contains(event.getPlayer().getAddress().toString()
					.split(":")[0])) {
				event.getPlayer().kickPlayer(kickMsg);
				event.setCancelled(true);
				return;
			}

			if (hasPerms(event.getPlayer())) {
				return;
			}

			if (!trackplayers.containsKey(pN)) {
				trackplayers.put(pN, new PlayerTrack(pN, this));
			} else {
				try {
					PlayerTrack pc = trackplayers.get(pN);
					long math = System.currentTimeMillis() - pc.lastChatMsg;
					if (pc.amoumt > spamam && math < spamtime) {
						if (notify) {
							botclass.getServer()
									.broadcastMessage(
											prefix + "\247chas detected chat spam!");
						}
						trackplayers.remove(pN);
						event.getPlayer().kickPlayer(kickMsg);
						event.setCancelled(true);
					} else {
						pc.trig();
						trackplayers.remove(pN);
						trackplayers.put(pN, pc);
					}
				} catch (Exception e) {

				}
			}

		} catch (Exception e) {
			// alright, it failed. Don't worry about it.
		}
	}
	
	
	//falsified antibot trigger bug fix, or brolos bug fix.
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(hasPerms(event.getPlayer())) {
			return;
		} else {
			botcts -= 1;
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!enabled) {
			return;
		}
		try {
			debug("User is trying to connect..");
			time = System.currentTimeMillis();

			if (hasPerms(event.getPlayer())) {
				debug("Whitelisted.");
				if (reanibo
						&& botclass.ownPermission("AntiBot.notify",
								event.getPlayer(), 1)) {
					event.getPlayer().sendMessage(
							prefix + "\247c" + connectInvasion);
				}
				if (reanibo
						&& botclass.ownPermission("AntiBot.admin.notify",
								event.getPlayer(), 2) && interval > 100000) {
					event.getPlayer()
							.sendMessage(
									prefix + "\247cThe system needs a flush. Please type /antibot flush. Thanks.");
				}
				return;
			}

			if (autokick.contains(event.getPlayer().getName())) {
				event.getPlayer().kickPlayer(kickMsg);
				event.setJoinMessage("");
				return;
			}

			if (!reanibo) {
				// IP tracking usernames system.
				trackPlayer(event.getPlayer(), event.getPlayer().getAddress()
						.toString().split(":")[0]);
				debug("Added user to tracking");
				addConnected(event.getPlayer().getName());
				debug("Added user to connected");
				trackplayers.put(event.getPlayer().getName(), new PlayerTrack(event.getPlayer().getName(), this));
				debug("Added user to trackplayer");
			}

			if (botcts > accounts + 2 && reanibo) { // Increase violation
													// levels.
				accounts = accounts + 2;
				interval = interval + 5000;
			}

			// bug workaround
			if (interval < 1) {
				debug("Bug detected! Fixing bug.");
				// lets try setting this back to default intervals, if not,
				// reload
				// the configuration.
				interval = botclass.defaultinterval;
				if (botclass.defaultinterval < 1) {
					// have to fix.
					botclass.loadSekritTools();
				}
			}

			long math = time - lasttime;
			int cb = interval + getRandomInt();
			debug("Checking....0");
			debug("Math: " + math);
			debug("Time: " + time);
			debug("Current Interval: " + interval);
			debug("Random Interval: " + cb);
			debug("Lasttime: " + lasttime);
			debug("BotCts: " + botcts + " Accs: " + accounts);

			if (botcts > accounts && math < cb) {
				debug("Hit #1!");
				// Incoming invasion.
				if (!reanibo) {
					if (whiteList) {
						if (notify && whiteList) {
							botclass.getServer()
									.broadcastMessage(
											prefix + "\247cOh no! A minecraft bot invasion has began. Connection Throttling: \247aEnabled");
						}
						reanibo = true;
					} else {
						if (notify) {
							botclass.getServer()
									.broadcastMessage(
											prefix + "\247chas detected minecraft spam!");
						}
					}
					debug("Tripswitched!");
					kickConnected();
					flush();
				}
				botattempt = System.currentTimeMillis();
				botcts += 1;
				event.getPlayer().kickPlayer(connectMsg);
				event.setJoinMessage("");
			} else if (botattempt < interval && reanibo) {
				debug("Hit #2");
				// Attempting to connect.
				botattempt = System.currentTimeMillis();
				botcts += 1;
				event.getPlayer().kickPlayer(connectMsg);
				event.setJoinMessage("");
			} else {
				debug("Hit #3");

				if (reanibo) {
					flush();
				}
				// No invasion.
				lasttime = System.currentTimeMillis();
				botcts += 1;
			}

			if (!botclass.getServer()
					.getOfflinePlayer(event.getPlayer().getName()).isOnline()) {
				event.setJoinMessage("");
			}

		} catch (Exception e) {
			botclass.getServer()
					.broadcastMessage(
							prefix + "\247cAn error had occured! Please check console.");
			e.printStackTrace();
		}
	}
}
