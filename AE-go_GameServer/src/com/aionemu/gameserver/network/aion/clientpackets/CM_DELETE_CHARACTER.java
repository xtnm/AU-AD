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
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_CHARACTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.PlayerService;
import com.google.inject.Inject;

/**
 * In this packets aion client is requesting deletion of character.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_DELETE_CHARACTER extends AionClientPacket
{
	/**
	 * PlayOk2 - we dont care...
	 */
	@SuppressWarnings("unused")
	private int	playOk2;
	/**
	 * ObjectId of character that should be deleted.
	 */
	private int	chaOid;
	@Inject
	private PlayerService playerService;
	
	private static final Logger log = Logger.getLogger(CM_DELETE_CHARACTER.class);
	
	/**
	 * Constructs new instance of <tt>CM_DELETE_CHARACTER </tt> packet
	 * @param opcode
	 */
	public CM_DELETE_CHARACTER(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		playOk2 = readD();
		chaOid = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		AionConnection client = getConnection();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(chaOid);
		if(playerAccData != null && !playerAccData.isLegionMember())
		{
			playerService.deletePlayer(playerAccData);
			client.sendPacket(new SM_DELETE_CHARACTER(chaOid, playerAccData.getDeletionTimeInSeconds()));
		}
		else
		{
			if(playerAccData != null)
			{
				log.error("Cannot delete character " + playerAccData.getPlayerCommonData().getName() + " : Player is in a legion");
			}
			client.sendPacket(SM_SYSTEM_MESSAGE.STR_DELETE_CHARACTER_IN_LEGION());
		}		
	}
}
