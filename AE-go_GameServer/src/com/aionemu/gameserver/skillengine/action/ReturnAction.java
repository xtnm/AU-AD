/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import org.apache.log4j.Logger;
import java.util.Collections;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.controllers.attack.SkillAttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;

/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnAction")
public class ReturnAction
extends Action
{
	private static final Logger	log	= Logger.getLogger(ReturnAction.class);

	@Override
	public void act(Skill skill)
	{
		float x,y,z;
		int worldId;
		BindPointTemplate bplist;

		Player player = skill.getEffector();
		SkillTemplate template = skill.getSkillTemplate();

		//TODO investigate unk
		int unk = 0;

		PacketSendUtility.broadcastPacket(player,
			new SM_CASTSPELL_END(player.getObjectId(), template.getSkillId(), skill.getSkillLevel(), unk, 0, Collections.singletonList(new SkillAttackResult(skill.getFirstTarget(), 0, AttackStatus.NORMALHIT)), template.getCooldown()), true);

		LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());

		int bindPointId = player.getCommonData().getBindPoint();

		if (bindPointId != 0) 
		{
			bplist = DataManager.BIND_POINT_DATA.getBindPointTemplate2(bindPointId);
			worldId = bplist.getZoneId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		}
		else
		{
			locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}
		
		player.getController().teleportTo(worldId, x, y, z, 0);
	}
}
