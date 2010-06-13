/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.List;

import com.aionemu.gameserver.controllers.ReviveType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author orz
 * @author Sarynth
 * 
 */
public class SM_DIE extends AionServerPacket
{
	private ReviveType	reviveType;

	public SM_DIE(ReviveType reviveType)
	{
		this.reviveType = reviveType;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		Player player = con.getActivePlayer();
		// 6660 was sniffed from another free server.
		// If anyone has retail SM_DIE data from retail please feel free to update the last dword.
		int kiskReviveDelay = (this.reviveType == ReviveType.KISK_REVIVE ? 6660 : 0); 
				
		/*
		 * Skill revive (Cleric : Rebirth)
		 */
		
		boolean hasEffect = false;
		
		List<Effect> playersEffects = player.getEffectController().getAbnormalEffects();
		for(Effect e : playersEffects)
		{
			if(e.getSkillId() == 1169)
			{
				player.getReviveController().setUsedSkillTime(e.getElapsedTime());
				writeC(buf, 1169);
				hasEffect = true;
				break;
			}
		}
		if(!hasEffect)
		{
			writeC(buf, 0);
		}	
		
		/*
		 * Item revive
		 */
		
		if(player.getInventory().getItemCountByItemId(161000001) >= 1 && !player.isItemUseDisabled(161000001))
		{
			player.getReviveController().setUsedItemId(161000001);
			writeD(buf, 161000001);
		}
		else if(player.getInventory().getItemCountByItemId(161000003) >= 1 && !player.isItemUseDisabled(161000003))
		{
			player.getReviveController().setUsedItemId(161000003);
			writeD(buf, 161000003);
		}
		else if(player.getInventory().getItemCountByItemId(161000004) >= 1 && !player.isItemUseDisabled(161000004))
		{
			player.getReviveController().setUsedItemId(161000004);
			writeD(buf, 161000004);
		}
		else if(player.getInventory().getItemCountByItemId(161001001) >= 1 && !player.isItemUseDisabled(161001001))
		{
			player.getReviveController().setUsedItemId(161001001);
			writeD(buf, 161001001);
		}
		else
		{
			writeD(buf, 0);
		}
		
		/*
		 * Kisk revive
		 */
		
		writeD(buf, kiskReviveDelay); // kiskReviveDelay
	}
}
