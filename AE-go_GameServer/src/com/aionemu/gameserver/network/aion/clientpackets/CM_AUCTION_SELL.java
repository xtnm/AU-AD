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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PONG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * This packet is sent when a player clicks on an instance gate to open it
 * 
 * @author xitanium
 * 
 */
public class CM_AUCTION_SELL extends AionClientPacket
{
	
	@Inject
	private World world;
	@Inject
	private ItemService itemService;
	
	private int uniqueObjectId;
	private int price;
	private int count;
	
	private int unk1;
	private int unk2;
	
	/**
	 * Constructs new instance of <tt>CM_AUCTION_SELL</tt> packet
	 * @param opcode
	 */
	public CM_AUCTION_SELL(int opcode)
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
		unk1 = readD();
		uniqueObjectId = readD();
		price = readD();
		unk2 = readD();
		count = readH();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		Item item = player.getInventory().getFirstItemByItemId(uniqueObjectId);
		
		if(player.getInventory().getKinahItem().getItemCount() < price)
		{
			//TODO: send correct SM_MESSAGE packet instead of custom string
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.NOT_ENOUGH_KINAH(price));
			return;
		}
		
		// remove item from inventory
		player.getInventory().removeFromBagByObjectId(uniqueObjectId, count);
		PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(uniqueObjectId));
		
		// remove kinah
		player.getInventory().decreaseKinah(price);
		PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(player.getInventory().getKinahItem()));		
		
		PacketSendUtility.sendMessage(player, "Received Auction Sell request : " + item.getItemTemplate().getName() + " x " + count + " for " + price + "kinah. Feature under dev, ignore this message.");
	}
}
