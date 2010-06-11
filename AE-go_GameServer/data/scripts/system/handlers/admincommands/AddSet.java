/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.itemset.ItemPart;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.LocaleManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author Antivirus
 *
 */

public class AddSet extends AdminCommand
{
	@Inject
	private ItemService itemService;
	@Inject
	private World	world;
	
	public AddSet()
	{
		super("addset");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_ADDSET)
		{
			PacketSendUtility.sendMessage(admin, LocaleManager.getString(1));
			return;
		}

		if(params.length == 0 || params.length > 2)
		{
			PacketSendUtility.sendMessage(admin, LocaleManager.getString(18));
			return;
		}

		int itemSetId = 0;
		Player receiver = null;

		try
		{
			itemSetId = Integer.parseInt(params[0]);
			
			receiver = admin;
		}
		catch (NumberFormatException e)
		{
			receiver = world.findPlayer(Util.convertName(params[0]));
			try
			{
				itemSetId = Integer.parseInt(params[1]);				
			}
			catch (NumberFormatException ex)
			{
			
				PacketSendUtility.sendMessage(admin, LocaleManager.getString(19));
				return;
			}
			catch (Exception ex2)
			{
				PacketSendUtility.sendMessage(admin, LocaleManager.getString(7));
				return;
			}
		}

		ItemSetTemplate itemSet = DataManager.ITEM_SET_DATA.getItemSetTemplate(itemSetId);
		if( itemSet == null )
		{
			PacketSendUtility.sendMessage(admin, LocaleManager.getString(20) + " " + itemSetId);
			return;
		}
		
		if( receiver.getInventory().getNumberOfFreeSlots() < itemSet.getItempart().size() )
		{
			PacketSendUtility.sendMessage(admin, LocaleManager.getString(21));
			return;			
		}
		
		for( ItemPart setPart : itemSet.getItempart())
		{
			int count = itemService.addItem(receiver, setPart.getItemid(), 1);
	
			if(count != 0)
			{
				PacketSendUtility.sendMessage(admin, "Item " + setPart.getItemid() +  " couldn't be added");
				return;
			}
		}
		
		PacketSendUtility.sendMessage(admin, LocaleManager.getString(23));
		PacketSendUtility.sendMessage(receiver, LocaleManager.getString(22));
	}
}
