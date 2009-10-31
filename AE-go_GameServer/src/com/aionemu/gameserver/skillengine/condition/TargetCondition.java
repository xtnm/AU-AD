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
package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Env;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetCondition")
public class TargetCondition
    extends Condition
{

    @XmlAttribute(required = true)
    protected TargetAttribute value;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link TargetAttribute }
     *     
     */
    public TargetAttribute getValue() {
        return value;
    }

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.condition.Condition#verify(com.aionemu.gameserver.skillengine.model.Env)
	 */
	@Override
	public boolean verify(Env env)
	{
		if(value != TargetAttribute.NONE && env.getEffected() == null)
		{
			return false;
		}
		switch(value)
		{
			//TODO multiple target condition refactoring
			case CREATURE:
				return env.getEffected() instanceof Creature;
			case MONSTER:
				return env.getEffected() instanceof Monster;
			case NPC:
				return env.getEffected() instanceof Npc;
			case PLAYER:
				return env.getEffected() instanceof Player;
			default:
				return false;
		}
	}
}
