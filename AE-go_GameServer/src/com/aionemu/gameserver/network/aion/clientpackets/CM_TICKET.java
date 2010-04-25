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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.TicketDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PONG;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * This packet is sent when a player opens a ticket from ingame
 * 
 * @author xitanium
 * 
 */
public class CM_TICKET extends AionClientPacket
{
	
	@Inject
	private World world;
	
	private int ticketType;
	
	private String ticketTitle;
	private String ticketMessage;
	
	private String ticketAddData;
	
	private String data;
	
	/**
	 * Constructs new instance of <tt>CM_TICKET</tt> packet
	 * @param opcode
	 */
	public CM_TICKET(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		// empty
		ticketType = readH();
		data = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		String[] tckData = data.split("/", 3);
		ticketTitle = tckData[0];
		ticketMessage = tckData[1];
		ticketAddData = tckData[2];
		PacketSendUtility.sendMessage(player, "Votre requete a bien ete enregistree.");
		DAOManager.getDAO(TicketDAO.class).insertTicket(ticketType, player, ticketTitle, ticketMessage, ticketAddData);
		PacketSendUtility.sendMessage(player, "Type : " + ticketType);
		PacketSendUtility.sendMessage(player, "Titre : " + ticketTitle);
		PacketSendUtility.sendMessage(player, "Message : " + ticketMessage);
		PacketSendUtility.sendMessage(player, "Info. supp. : " + ticketAddData);
		
	}
}
