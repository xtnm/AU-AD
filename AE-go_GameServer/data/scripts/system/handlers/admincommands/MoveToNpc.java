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
package admincommands;

import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author MrPoke and lord_rex
 *
 */
public class MoveToNpc extends AdminCommand
{
	@Inject
	TeleportService teleportService;
	@Inject
	NpcData npcData;
	
	public MoveToNpc()
	{
		super("movetonpc");
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.AdminCommand#executeCommand(com.aionemu.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.isGM())
		{
			int npcId = 0;
			try
			{
				npcId = Integer.valueOf(params[0]);
				teleportService.teleportToNpc(admin, npcId);	
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				PacketSendUtility.sendMessage(admin, "Need more parameter!");
			}
			catch(NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "You don't have enough privileges to use that command!");
		}
	}
}
