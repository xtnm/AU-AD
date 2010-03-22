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

import java.util.Iterator;

import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author xitanium
 *
 */
public class Player implements UnishellCommand
{

	@Inject
	private World			world;
	
	@Override
	public String execute(String[] params)
	{
		String result = "";
		
		if(params.length == 0)
		{
			return "550 Invalid syntax.";
		}
		
		if(params[0].equals("LIST"))
		{
			Iterator<com.aionemu.gameserver.model.gameobjects.player.Player> players = world.getPlayersIterator();
			while(players.hasNext())
			{
				com.aionemu.gameserver.model.gameobjects.player.Player current = players.next();
				result += current.getObjectId() + " " + current.getName() + "\n\r";
			}
		}
		else
		{
			return "555 No such sub-command";
		}
		
		return result;
		
	}

}
