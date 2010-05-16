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

package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_ID;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author xitanium
 *
 */

public class Rename extends AdminCommand
{
	@Inject
	private World	world;
	
	@Inject
	private PlayerService playerService;

	/**
	 * Constructor of Rename
	 */
	public Rename()
	{
		super("rename");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		
		if(params.length < 2)
		{
			PacketSendUtility.sendMessage(admin, "syntax //rename <character_name> <new_name>");
			return;
		}
		
		Player player = world.findPlayer(Util.convertName(params[0]));
		
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player cannot be found.");
			return;
		}
		
		if(!playerService.isValidName(params[1]))
		{
			PacketSendUtility.sendMessage(admin, "The specified new name has an invalid format.");
			return;
		}
		
		if(!playerService.isFreeName(params[1]))
		{
			PacketSendUtility.sendMessage(admin, "The specified new name is already in use.");
			return;
		}
		
		world.removeObject(player);
		
		player.getCommonData().setName(params[1]);
		playerService.storePlayer(player);
		
		world.storeObject(player);
		
		PacketSendUtility.broadcastPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendMessage(player, "You have been renamed to: " + params[1]);
		PacketSendUtility.sendMessage(admin, "Player " + params[0] + " renamed to " + params[1]);
		
	}
}
