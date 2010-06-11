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
package com.aionemu.gameserver.ai.desires.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillList;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;
import com.aionemu.gameserver.model.templates.npcskill.NpcUniqueSkillTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 * 
 */
public class SkillUseDesire extends AbstractDesire
{

	protected Creature		owner;
	private NpcSkillList	skillList;

	private static final Logger log = Logger.getLogger(SkillUseDesire.class);
	
	/**
	 * @param owner
	 * @param desirePower
	 */
	public SkillUseDesire(Creature owner, int desirePower)
	{
		super(desirePower);
		this.owner = owner;
		this.skillList = ((Npc) owner).getNpcSkillList();
	}

	@Override
	public boolean handleDesire(AI<?> ai)
	{
		if(owner.isCasting())
			return true;
		
		List<NpcSkillTemplate> skills = skillList.getNpcSkills();
		List<NpcUniqueSkillTemplate> uniqueSkills = skillList.getNpcUniqueSkills();
		
		// 1 : If npc has unique skill, process it
		if(uniqueSkills != null && uniqueSkills.size() > 0)
		{
			for(NpcUniqueSkillTemplate uSkill : uniqueSkills)
			{
				if(!owner.hasCastedUniqueSkill(uSkill.getSkillid()))
				{
					NpcLifeStats nls = (NpcLifeStats)owner.getLifeStats();
					if(nls.getHpPercentage() <= uSkill.getHPPercent())
					{
						Skill skill = SkillEngine.getInstance().getSkill(owner, uSkill.getSkillid(), uSkill.getSkillLevel(),owner.getTarget());
						if(skill != null)
						{
							owner.setCastedUniqueSkill(uSkill.getSkillid());
							// MEGA KILL !!!!!
							skill.useSkill();
							PacketSendUtility.broadcastPacket(owner, new SM_MESSAGE(owner.getObjectId(), owner.getName(), "Boooooooooooooooooommmm !!", ChatType.SHOUT));
							// let's sleep a second ...
							return true;
						}
						else
						{
							log.error("npc #" + owner.getName() + " cannot cast unique skill #" + uSkill.getSkillid() + " : no such skill template");
						}
					}
				}
			}
		}
		
		// 2 : No unique skill to cast, well let's try classic skills
		
		if(skills != null && skills.size() > 0)
		{
			for(NpcSkillTemplate template : skills)
			{
				if(template.isAboutHp())
				{
					NpcLifeStats nls = (NpcLifeStats)owner.getLifeStats();
					int minHpPercent = template.getMinHp();
					int maxHpPercent = template.getMaxHp();
					if(nls.getHpPercentage() >= minHpPercent && nls.getHpPercentage() <= maxHpPercent)
					{
						if(Rnd.get(0, 100) < template.getProbability())
						{
							Skill skill = SkillEngine.getInstance().getSkill(owner, template.getSkillid(), template.getSkillLevel(),
								owner.getTarget());
							skill.useSkill();
							return true;
						}
					}
				}
				else
				{
					if(Rnd.get(0, 100) < template.getProbability())
					{
							Skill skill = SkillEngine.getInstance().getSkill(owner, template.getSkillid(), template.getSkillLevel(),
								owner.getTarget());
							skill.useSkill();
							return true;
					}
				}
			}
		}
		
		return true;
	}

	@Override
	public void onClear()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public int getExecutionInterval()
	{
		return 1;
	}

}
