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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Jego
 */
public class ReviveController
{
	TeleportService	teleportService;

	private Player	player;

	public ReviveController(Player player)
	{
		this.player = player;
		this.teleportService = player.getController().sp.getTeleportService();
	}

	/**
	 * 
	 */
	public void skillRevive()
	{
		revive(10, 10);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 14), true);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.REVIVE);
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}

	/**
	 * 
	 */
	public void bindRevive()
	{
		revive(25, 25);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.REVIVE);
		// TODO: It is not always necessary.
		// sendPacket(new SM_QUEST_LIST(activePlayer));
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));

		teleportService.moveToBindLocation(player, true);
	}

	private void revive(int hpPercent, int mpPercent)
	{
		player.getLifeStats().setCurrentHpPercent(hpPercent);
		player.getLifeStats().setCurrentMpPercent(mpPercent);
		player.getCommonData().setDp(0);
		player.getLifeStats().triggerRestoreOnRevive();

		player.unsetState(CreatureState.DEAD);
		player.getController().startProtectionActiveTask();
	}

}
