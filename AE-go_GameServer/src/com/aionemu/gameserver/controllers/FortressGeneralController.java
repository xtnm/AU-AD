/*
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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.FortressGeneral;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.FortressService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.WorldType;
import com.google.inject.Inject;
import com.google.inject.internal.Nullable;

/**
 * @author Xitanium
 *
 */
public class FortressGeneralController extends NpcController
{
	
	@Inject
	private FortressService fortressService;
	
	@Override
	public void doDrop(Player player)
	{
		super.doDrop(player);
		/*sp.getDropService().registerDrop(getOwner() , player);			
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOT_STATUS(this.getOwner().getObjectId(), 0));*/
	}
	
	@Override
	public void doReward(Creature creature)
	{
		super.doReward(creature);

		/*Creature master = creature.getMaster();
		if(master instanceof Player)
		{
			Player player = (Player) master;
			
			if(player.getPlayerGroup() == null) //solo
			{
				// Exp reward
				long expReward = StatFunctions.calculateSoloExperienceReward(player, getOwner());
				player.getCommonData().addExp(expReward);

				// DP reward
				int currentDp = player.getCommonData().getDp();
				int dpReward = StatFunctions.calculateSoloDPReward(player, getOwner());
				player.getCommonData().setDp(dpReward + currentDp);
				
				// AP reward
				WorldType worldType = sp.getWorld().getWorldMap(player.getWorldId()).getWorldType();
				if(worldType == WorldType.ABYSS)
				{
					int apReward = StatFunctions.calculateSoloAPReward(player, getOwner());
					player.getCommonData().addAp(apReward);
				}
				
				sp.getQuestEngine().onKill(new QuestEnv(getOwner(), player, 0 , 0));
			}
			else
			{
				sp.getGroupService().doReward(player, getOwner());
			}
		}*/
	}
	
	@Override
	public void onRespawn()
	{
		super.onRespawn();
	}
	
	@Override
	public void onDie(@Nullable Creature lastAttacker)
	{
		//super.onDie(lastAttacker);
		if(lastAttacker instanceof Player)
		{
			fortressService.triggerGeneralKilled(getOwner().getFortressId(), (Player)lastAttacker);
		}
	}

	@Override
	public FortressGeneral getOwner()
	{
		return (FortressGeneral) super.getOwner();
	}
}