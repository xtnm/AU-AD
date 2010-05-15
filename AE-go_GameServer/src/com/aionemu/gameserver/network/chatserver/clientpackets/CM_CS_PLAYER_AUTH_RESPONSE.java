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
package com.aionemu.gameserver.network.chatserver.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.chatserver.CsClientPacket;
import com.aionemu.gameserver.services.ChatService;
import com.google.inject.Inject;

/**
 * @author ATracer
 */
public class CM_CS_PLAYER_AUTH_RESPONSE extends CsClientPacket
{
	protected static final Logger	log	= Logger.getLogger(CM_CS_PLAYER_AUTH_RESPONSE.class);

	/**
	 * Response: 0=Authed,<br>
	 * 1=NotAuthed,<br>
	 * 2=AlreadyRegistered
	 */
	private int						response;
	/**
	 * Player for which authentication was performed
	 */
	private int						playerId;
	
	@Inject
	private ChatService				chatService;

	/**
	 * @param opcode
	 */
	public CM_CS_PLAYER_AUTH_RESPONSE(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		response = readC();
		playerId = readD();
	}

	@Override
	protected void runImpl()
	{
		switch(response)
		{
			case 0: // Authed
				chatService.playerAuthed(playerId);
				break;
			case 1: // NotAuthed
				log.warn("Player was not authed in chat server " + playerId);
				break;
			case 2: // AlreadyRegistered
				log.warn("Player was already registered in chat server " + playerId);
				break;
		}
	}
}
