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
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class CM_SUMMON_COMMAND extends AionClientPacket
{

	private int mode;
	private int targetObjId;
	
	private int unk1 = 0;
	private int unk2 = 0;
	
	@Inject
	private World world;
	
	private static final Logger log = Logger.getLogger(CM_SUMMON_COMMAND.class);
	
	public CM_SUMMON_COMMAND(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		mode = readH();
		unk1 = readD();
		unk2 = readD();
		targetObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();
		Summon summon = activePlayer.getSummon();
		if(summon != null)
		{
			switch(mode)
			{
				case 0:
					AionObject target = world.findAionObject(targetObjId);
					if(target != null && target instanceof Creature)
					{
						summon.getController().attackTarget((Creature)target);
					}
					else
					{
						log.error("Summon target Aion object cannot be loaded, data are unk1=" + unk1 + " and unk2=" + unk2);
					}
					break;
				case 1:
					summon.getController().guardMode();
					break;
				case 2:
					summon.getController().restMode();
					break;
				case 3:
					summon.getController().release();
					break;
					
			}
		}
	}

}
