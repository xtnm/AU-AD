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

import com.aionemu.commons.utils.ExitCode;
import com.aionemu.gameserver.network.chatserver.CsClientPacket;
import com.aionemu.gameserver.network.chatserver.ChatServerConnection.State;
import com.aionemu.gameserver.network.chatserver.serverpackets.SM_CS_AUTH;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * 
 * @author ATracer
 * 
 */
public class CM_CS_AUTH_RESPONSE extends CsClientPacket
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log	= Logger.getLogger(CM_CS_AUTH_RESPONSE.class);

	/**
	 * Response: 0=Authed,<br>
	 * 1=NotAuthed,<br>
	 * 2=AlreadyRegistered
	 */
	private int						response;

	/**
	 * @param opcode
	 */
	public CM_CS_AUTH_RESPONSE(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		response = readC();
	}

	@Override
	protected void runImpl()
	{
		switch(response)
		{
			case 0: // Authed
				log.info("GameServer authed successfully");
				getConnection().setState(State.AUTHED);
				break;
			case 1: // NotAuthed
				log.fatal("GameServer is not authenticated at ChatServer side");
				System.exit(ExitCode.CODE_ERROR);
				break;
			case 2: // AlreadyRegistered
				log.info("GameServer is already registered at ChatServer side! trying again...");
				ThreadPoolManager.getInstance().schedule(new Runnable(){
					@Override
					public void run()
					{
						CM_CS_AUTH_RESPONSE.this.getConnection().sendPacket(new SM_CS_AUTH());
					}

				}, 10000);
				break;
		}
	}
}
