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
package com.aionemu.chatserver.service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.aionemu.chatserver.model.ChatClient;
import com.aionemu.chatserver.model.channel.Channel;
import com.aionemu.chatserver.model.channel.Channels;
import com.aionemu.chatserver.network.aion.serverpackets.SM_PLAYER_AUTH_RESPONSE;
import com.aionemu.chatserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.chatserver.network.netty.handler.ClientChannelHandler.State;
import com.google.inject.Inject;

/**
 * @author ATracer
 */
public class ChatService
{
	private static final Logger	log = Logger.getLogger(ChatService.class);
	
	private Map<Integer, ChatClient>	players	= new ConcurrentHashMap<Integer, ChatClient>();
	
	@Inject
	private BroadcastService broadcastService;
	
	private static final String stoken = "qwertyasdfzxcvtgbdsfrvsdgvxdfgtcvbwtcsfawbsc";
	
	/**
	 * Player registered from server side
	 * 
	 * @param playerId
	 * @param token
	 * @param identifier 
	 * @return
	 */
	public ChatClient registerPlayer(int playerId)
	{
		byte[] token = generateToken(playerId);
		ChatClient chatClient = new ChatClient(playerId, token);
		players.put(playerId, chatClient);
		return chatClient;
	}

	/**
	 * 
	 * @param playerId
	 * @return
	 */
	private byte[] generateToken(int playerId)
	{
		return (playerId + stoken).getBytes();
	}

	/**
	 * Player registered from client request
	 * 
	 * @param playerId
	 * @param token
	 * @param identifier
	 * @param clientChannelHandler
	 */
	public void registerPlayerConnection(int playerId, byte[] token, byte[] identifier, ClientChannelHandler channelHandler)
	{
		ChatClient chatClient = players.get(playerId);
		if(chatClient != null)
		{
			byte[] regToken = chatClient.getToken();
			if(Arrays.equals(regToken, token))
			{
				chatClient.setIdentifier(identifier);
				chatClient.setChannelHandler(channelHandler);
				channelHandler.sendPacket(new SM_PLAYER_AUTH_RESPONSE());
				channelHandler.setState(State.AUTHED);
				channelHandler.setChatClient(chatClient);
				broadcastService.addClient(chatClient);
			}
		}
	}

	/**
	 * 
	 * @param chatClient 
	 * @param channelIndex
	 * @param channelIdentifier
	 * @return
	 */
	public Channel registerPlayerWithChannel(ChatClient chatClient, int channelIndex, byte[] channelIdentifier)
	{
		Channel channel = Channels.getChannelByIdentifier(channelIdentifier);	
		if(channel != null)
			chatClient.addChannel(channel);
		return channel;
	}
	
	/**
	 * 
	 * @param playerId
	 */
	public void playerLogout(int playerId)
	{
		ChatClient chatClient = players.get(playerId);
		if(chatClient != null)
		{
			players.remove(playerId);
			broadcastService.removeClient(chatClient);
			if(chatClient.getChannelHandler() != null)
				chatClient.getChannelHandler().close();
			else
				log.warn("Received logout event without client authentication for player " + playerId);
		}		
	}

}
