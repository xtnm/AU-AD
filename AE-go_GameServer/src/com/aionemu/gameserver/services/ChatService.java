/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.chatserver.ChatServer;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 * 
 */
public class ChatService
{
	private static final Logger	log	= Logger.getLogger(ChatService.class);

	@Inject
	private ChatServer			chatServer;
	@Inject
	private World 				world;
	
	private Map<Integer, Player>		players = new HashMap<Integer, Player>();

	/**
	 * Send token to chat server
	 * 
	 * @param player
	 */
	public void onPlayerLogin(Player player)
	{
		if(!isPlayerConnected(player))
		{
			byte[] token = generateToken(player);
			chatServer.sendPlayerLoginRequst(player, token);
		}
		else
		{
			log.warn("Player already registered with chat server " + player.getName());
			//TODO do force relog in chat server?
		}
	}

	/**
	 * Disonnect from chat server
	 * 
	 * @param player
	 */
	public void onPlayerLogout(Player player)
	{
		//TODO
	}
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerConnected(Player player)
	{
		return players.containsKey(player.getObjectId());
	}

	/**
	 * @param playerId
	 */
	public void playerAuthed(int playerId)
	{	
		Player player = world.findPlayer(playerId);
		if(player != null)
		{
			players.put(playerId, player);
			// TODO send init packet with token	
		}	
	}
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	private byte[] generateToken(Player player)
	{
		return new byte[0];
	}
}
