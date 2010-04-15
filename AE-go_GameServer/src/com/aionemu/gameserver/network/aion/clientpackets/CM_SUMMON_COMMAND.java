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
import com.aionemu.gameserver.controllers.SummonController.UnsummonType;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.Summon.SummonMode;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.utils.ThreadPoolManager;
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
		mode = readC();
		readD();
		readD();
		targetObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();
		final Summon summon = activePlayer.getSummon();
		if(summon != null)
		{
			switch(mode)
			{
				case 0:
					final AionObject target = world.findAionObject(targetObjId);
					log.debug("Summon attacking");
					summon.getController().attackMode();
					if(target != null && target instanceof Creature &&  summon.getAttackTask() == null)
					{
						log.debug("Starting Summon attack thread");
						summon.setAttackTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Creature targetCreature = (Creature)target;
								if(!targetCreature.getLifeStats().isAlreadyDead() && !summon.getLifeStats().isAlreadyDead() && summon.getMode() == SummonMode.ATTACK)
								{
									log.debug("Summon attacking target");
									summon.getController().attackTarget((Creature)target);
								}
								else
								{
									log.debug("Exiting summon attack thread - enemy dead = " + targetCreature.getLifeStats().isAlreadyDead() + " // summon mode = " + summon.getMode().toString());
									summon.getAttackTask().cancel(true);
									summon.setAttackTask(null);
								}
							}
						}, 0, 2000));
						
					}
					break;
				case 1:
					summon.getController().guardMode();
					break;
				case 2:
					summon.getController().restMode();
					break;
				case 3:
					summon.getController().release(UnsummonType.COMMAND);
					break;
					
			}
		}
	}

}
