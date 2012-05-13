package somebody.is.madbro.handlers;

import somebody.is.madbro.AntiBot;
import somebody.is.madbro.datatrack.DataTrackCore;
import somebody.is.madbro.handlers.chat.ChatFlowHandler;
import somebody.is.madbro.handlers.chat.ChatSpamHandler;

public class HandlerCore {

	protected AntiBot antibot = null;
	protected DataTrackCore datatrack = null;

	// handlers
	private CommandHandler commandhandler = null;
	private BotHandler bothandler = null;
	private ChatSpamHandler chatspamhandler = null;
	private CountryBanHandler countrybanhandler = null;
	private ChatFlowHandler chatflowhandler = null;

	public HandlerCore(AntiBot instance, DataTrackCore instance2) {
		antibot = instance;
		commandhandler = new CommandHandler(instance);
		bothandler = new BotHandler(instance, instance2);
		chatspamhandler = new ChatSpamHandler(instance);
		countrybanhandler = new CountryBanHandler(instance);
		chatflowhandler = new ChatFlowHandler(instance);
	}
	
	public CommandHandler getCommands() {
		return commandhandler;
	}

	public BotHandler getBotHandler() {
		return bothandler;
	}

	public ChatSpamHandler getChatSpamHandler() {
		return chatspamhandler;
	}
	
	public CountryBanHandler getCountryBanHandler() {
		return countrybanhandler;
	}
	
	public ChatFlowHandler getChatFlowHandler() {
		return chatflowhandler;
	}

}
