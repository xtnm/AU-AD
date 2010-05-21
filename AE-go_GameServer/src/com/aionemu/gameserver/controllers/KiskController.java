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
package com.aionemu.gameserver.controllers;

import java.util.List;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Sarynth
 *
 */
public class KiskController extends NpcController
{
	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage)
	{
		Kisk kisk = (Kisk)this.getOwner();

		if (kisk.getLifeStats().isFullyRestoredHp())
		{
			List<Player> members = kisk.getCurrentMemberList();
			for(Player member : members)
			{
				PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.KISK_UNDER_ATTACK);
			}
		}
		
		super.onAttack(creature, skillId, type, damage);
		
	}
	
	@Override
	public void onDespawn(boolean forced)
	{
		
		final Kisk kisk = (Kisk)this.getOwner();
		
		List<Player> members = kisk.getCurrentMemberList();
		for(Player member : members)
		{
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.KISK_DISMANTLED);
			member.setKisk(null);
			sp.getTeleportService().sendSetBindPoint(member);
		}
		
		removeKisk(kisk);
	}
	
	@Override
	public void onDie(Creature lastAttacker)
	{
		final Kisk kisk = (Kisk)this.getOwner();
		
		List<Player> members = kisk.getCurrentMemberList();
		for(Player member : members)
		{
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.KISK_DESTROYED);
		}
		
		removeKisk(kisk);
	}
	
	private void removeKisk(final Kisk kisk)
	{
		PacketSendUtility.broadcastPacket(kisk, new SM_EMOTION(kisk, 13, 0, 0));
		
		// Remove players from kisk bind point and send updates.
		List<Player> members = kisk.getCurrentMemberList();
		for(Player member : members)
		{
			member.setKisk(null);
			sp.getTeleportService().sendSetBindPoint(member);
			if (member.getLifeStats().isAlreadyDead())
				PacketSendUtility.sendPacket(member, new SM_DIE(ReviveType.BIND_REVIVE));
		}
		
		// Schedule World Removal
		addTask(TaskId.DECAY, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				sp.getWorld().despawn(kisk);
			}
		}, 3 * 1000));
	}
	
	@Override
	public void onDialogRequest(Player player)
	{
		final Kisk kisk = (Kisk)this.getOwner();
		
		if (player.getKisk() == kisk)
		{
			PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "You are already bound to this kisk.", ChatType.ANNOUNCEMENTS));
			return;
		}
		
		if (kisk.canBind(player))
		{
			RequestResponseHandler responseHandler = new RequestResponseHandler(kisk) {
				
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					Kisk kisk = (Kisk)requester;
					
					// Check again if it's full (If they waited to press OK)
					if (!kisk.canBind(responder))
					{
						PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.CANT_USE_KISK);
						return;
					}
					
					// Adds responder to kisk, and sends SM_KISK_UPDATE
					kisk.addPlayer(responder);
					
					// Send Bind Point Data (Adds the Kisk Info)
					sp.getTeleportService().sendSetBindPoint(responder);
					
					// Send System Message
					PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.KISK_REGISTERED_BIND_POINT);
					
					// Send Animated Bind Flash
					PacketSendUtility.broadcastPacket(responder, new SM_LEVEL_UPDATE(responder.getObjectId(), 2, responder.getCommonData().getLevel()), true);
				}
	
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// Nothing Happens
				}
			};
			
			boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_BIND_TO_KISK, responseHandler);
			if (requested)
			{
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_BIND_TO_KISK, player.getObjectId()));
			}
		}
		else if (kisk.getCurrentMemberCount() >= kisk.getMaxMembers())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.KISK_FULL);
		}
		else
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.CANT_USE_KISK);
		}
	}
	
}
