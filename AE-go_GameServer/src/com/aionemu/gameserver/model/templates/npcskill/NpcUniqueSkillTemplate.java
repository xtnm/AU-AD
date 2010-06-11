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
package com.aionemu.gameserver.model.templates.npcskill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Xitanium
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcuniqueskill")
public class NpcUniqueSkillTemplate
{
	@XmlAttribute(name = "skillid")
	protected int		skillid;
	@XmlAttribute(name = "skilllevel")
	protected int		skilllevel;
	@XmlAttribute(name = "hp_percent")
	protected int		hpPercent;

	/**
	 * @return the skillid
	 */
	public int getSkillid()
	{
		return skillid;
	}

	/**
	 * @return the skilllevel
	 */
	public int getSkillLevel()
	{
		return skilllevel;
	}

	/**
	 * @return the percentage of hp
	 */
	public int getHPPercent()
	{
		return hpPercent;
	}
}