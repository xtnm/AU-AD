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
package com.aionemu.gameserver.unishell.commands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author xitanium
 *
 */
public class Kick implements UnishellCommand
{

	@Inject
	private World			world;
	
	@Override
	public String execute(String[] params)
	{
		// TODO Auto-generated method stub
		int targetPlayerId = Integer.parseInt(params[0]);
		
		Player target = world.findPlayer(targetPlayerId);
		
		if(target == null)
		{
			return "NO_SUCH_PLAYER";
		}
		
		target.getClientConnection().close(new SM_QUIT_RESPONSE(), true);
		
		return "KICKED " + targetPlayerId;
		
	}

}
