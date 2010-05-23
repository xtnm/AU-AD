/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.utils.chathandlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * This chat handler is responsible for handling admin commands, starting with //
 * 
 * @author Luno
 * @author Divinity - updated for GM Audit
 * 
 */
public class AdminCommandChatHandler implements ChatHandler
{
	private static final Logger			log			= Logger.getLogger(AdminCommandChatHandler.class);

	private Map<String, AdminCommand>	commands	= new HashMap<String, AdminCommand>();
	
	@Inject
	private TeleportService teleportService;

	AdminCommandChatHandler()
	{

	}

	void registerAdminCommand(AdminCommand command)
	{
		if(command == null)
			throw new NullPointerException("Command instance cannot be null");

		String commandName = command.getCommandName();

		AdminCommand old = commands.put(commandName, command);
		if(old != null)
		{
			log.warn("Overriding handler for command " + commandName + " from " + old.getClass().getName() + " to "
				+ command.getClass().getName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChatHandlerResponse handleChatMessage(ChatType chatType, String message, Player sender)
	{
		if(!message.startsWith("//") && !message.startsWith("."))
		{
			if(sender.CHAT_FIX_WORLD_CHANNEL)
			{
				Iterator<Player> onlinePlayers = sender.getActiveRegion().getWorld().getPlayersIterator();
				while(onlinePlayers.hasNext())
				{
					Player p = onlinePlayers.next();
					if(sender.getCommonData().getRace() == Race.ASMODIANS && (p.getCommonData().getRace() == Race.ASMODIANS || p.getAccessLevel() > 0))
					{
						String toSend = "[Asmodiens]";
						if(sender.getAccessLevel() > 0)
						{
							toSend += " <GM>";
						}
						toSend += " " + sender.getName();
						toSend += " : " + message;
						PacketSendUtility.sendMessage(p, toSend);
					}
					else if(sender.getCommonData().getRace() == Race.ELYOS && (p.getCommonData().getRace() == Race.ELYOS || p.getAccessLevel() > 0))
					{
						String toSend = "[Elyseens]";
						if(sender.getAccessLevel() > 0)
						{
							toSend += " <GM>";
						}
						toSend += " " + sender.getName();
						toSend += " : " + message;
						PacketSendUtility.sendMessage(p, toSend);
					}
					else { }
				}
				return ChatHandlerResponse.BLOCKED_MESSAGE;
			}
			else
			{
				return new ChatHandlerResponse(false, message);
			}
		}
		else
		{
			// If player command
			if(message.startsWith("."))
			{
				String[] commandAndParams = message.split(" ", 2);
				String command = commandAndParams[0].substring(1);
				if(command.equals("world"))
				{
					String worldMessage = commandAndParams[1].trim();
					if(worldMessage.equals("fix"))
					{
						sender.CHAT_FIX_WORLD_CHANNEL = true;
						if(sender.getCommonData().getRace() == Race.ASMODIANS)
						{
							PacketSendUtility.sendMessage(sender, "Votre chat est maintenant fixe sur le canal 'Asmodiens'");
							PacketSendUtility.sendMessage(sender, "Tapez .world unfix pour liberer votre chat du canal.");
						}
						else
						{
							PacketSendUtility.sendMessage(sender, "Votre chat est maintenant fixe sur le canal 'Elyseens'");
							PacketSendUtility.sendMessage(sender, "Tapez .world unfix pour liberer votre chat du canal.");
						}
					}
					else if(worldMessage.equals("unfix"))
					{
						sender.CHAT_FIX_WORLD_CHANNEL = false;
						if(sender.getCommonData().getRace() == Race.ASMODIANS)
						{
							PacketSendUtility.sendMessage(sender, "Votre chat est maintenant libere du canal 'Asmodiens'.");
						}
						else
						{
							PacketSendUtility.sendMessage(sender, "Votre chat est maintenant libere du canal 'Elyseens'.");
						}
					}
					else
					{
						Iterator<Player> onlinePlayers = sender.getActiveRegion().getWorld().getPlayersIterator();
						while(onlinePlayers.hasNext())
						{
							Player p = onlinePlayers.next();
							if(sender.getCommonData().getRace() == Race.ASMODIANS && (p.getCommonData().getRace() == Race.ASMODIANS || p.getAccessLevel() > 0))
							{
								String toSend = "[Asmodiens]";
								if(sender.getAccessLevel() > 0)
								{
									toSend += " <GM>";
								}
								toSend += " " + sender.getName();
								toSend += " : " + worldMessage;
								PacketSendUtility.sendMessage(p, toSend);
							}
							else if(sender.getCommonData().getRace() == Race.ELYOS && (p.getCommonData().getRace() == Race.ELYOS || p.getAccessLevel() > 0))
							{
								String toSend = "[Elyseens]";
								if(sender.getAccessLevel() > 0)
								{
									toSend += " <GM>";
								}
								toSend += " " + sender.getName();
								toSend += " : " + worldMessage;
								PacketSendUtility.sendMessage(p, toSend);
							}
							else { }
						}
					}
				}
				else if(command.equals("ely") && sender.getAccessLevel() > 0)
				{
					Iterator<Player> onlinePlayers = sender.getActiveRegion().getWorld().getPlayersIterator();
					while(onlinePlayers.hasNext())
					{
						Player p = onlinePlayers.next();
						if(p.getCommonData().getRace() == Race.ELYOS && p.getObjectId() != sender.getObjectId())
						{
							PacketSendUtility.sendMessage(p, "[Elyseens] <GM> " + sender.getName() + " : " + commandAndParams[1].trim());
						}
					}
					PacketSendUtility.sendMessage(sender, "[Elyseens] <GM> " + sender.getName() + " : " + commandAndParams[1]);
				}
				else if(command.equals("asmo") && sender.getAccessLevel() > 0)
				{
					Iterator<Player> onlinePlayers = sender.getActiveRegion().getWorld().getPlayersIterator();
					while(onlinePlayers.hasNext())
					{
						Player p = onlinePlayers.next();
						if(p.getCommonData().getRace() == Race.ASMODIANS && p.getObjectId() != sender.getObjectId())
						{
							PacketSendUtility.sendMessage(p, "[Asmodiens] <GM> " + sender.getName() + " : " + commandAndParams[1].trim());
						}
					}
					PacketSendUtility.sendMessage(sender, "[Asmodiens] <GM> " + sender.getName() + " : " + commandAndParams[1]);
				}
				else
				{
					PacketSendUtility.sendMessage(sender, command + ": cette commande joueur n'existe pas.");
				}
				return ChatHandlerResponse.BLOCKED_MESSAGE;
			}
			// If admin command
			else
			{

				if(!(sender.getAccessLevel() > 0))
				{
					PacketSendUtility.sendMessage(sender, "<You do not have permission to use admin commands>");
					return ChatHandlerResponse.BLOCKED_MESSAGE;
				}

				String[] commandAndParams = message.split(" ", 2);

				String command = commandAndParams[0].substring(2);
				AdminCommand admc = commands.get(command);
				if(admc == null)
				{
					PacketSendUtility.sendMessage(sender, "<There is no such admin command: " + command + ">");
					return ChatHandlerResponse.BLOCKED_MESSAGE;
				}

				String[] params = new String[] {};
				if(commandAndParams.length > 1)
					params = commandAndParams[1].split(" ", admc.getSplitSize());

				admc.executeCommand(sender, params);
				return ChatHandlerResponse.BLOCKED_MESSAGE;
			}
		}
	}

	/**
	 * Clear all registered handlers (before reload). 
	 */
	void clearHandlers()
	{
		this.commands.clear();
	}

	/**
	 * Returns count of available admin command handlers.
	 * @return count of available admin command handlers.
	 */
	public int getSize()
	{
		return this.commands.size();
	}
}
