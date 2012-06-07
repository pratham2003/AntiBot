package me.freebuild.superspytx.antibot.handlers;

import me.freebuild.superspytx.antibot.Core;
import me.freebuild.superspytx.antibot.datatrack.DataTrackCore;
import me.freebuild.superspytx.antibot.handlers.chat.ChatFlowHandler;
import me.freebuild.superspytx.antibot.handlers.chat.ChatSpamHandler;

public class HandlerCore {

	protected Core antibot = null;
	protected DataTrackCore datatrack = null;

	// handlers
	private CommandHandler commandhandler = null;
	private BotHandler bothandler = null;
	private ChatSpamHandler chatspamhandler = null;
	private CountryBanHandler countrybanhandler = null;
	private ChatFlowHandler chatflowhandler = null;
	private CaptchaHandler captchahandler = null;

	public HandlerCore(Core instance, DataTrackCore instance2) {
		antibot = instance;
		commandhandler = new CommandHandler(instance);
		bothandler = new BotHandler(instance, instance2);
		chatspamhandler = new ChatSpamHandler(instance);
		countrybanhandler = new CountryBanHandler(instance);
		chatflowhandler = new ChatFlowHandler(instance);
		captchahandler = new CaptchaHandler(instance);
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
	
	public CaptchaHandler getCaptchaHandler() {
		return captchahandler;
	}

}