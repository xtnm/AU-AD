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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_USESKILL;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetOrderUseUltraSkillEffect")
public class PetOrderUseUltraSkillEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect)
	{
		Player effector = (Player) effect.getEffector();
		int effectorId = effector.getSummon().getObjectId();
		// temp hardcoded
		int skillId = 17607;
		int skillLvl = 1;
		int targetId = effect.getEffected().getObjectId();

		PacketSendUtility.sendPacket(effector, new SM_SUMMON_USESKILL(effectorId, skillId,
			skillLvl, targetId));
	}

	@Override
	public void calculate(Effect effect)
	{
		if(effect.getEffector() instanceof Player && effect.getEffected() != null)
			effect.increaseSuccessEffect();
	}
}
