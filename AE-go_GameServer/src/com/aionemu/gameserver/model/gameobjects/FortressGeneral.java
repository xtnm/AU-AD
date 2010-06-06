/**
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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.ai.npcai.AggressiveAi;
import com.aionemu.gameserver.ai.npcai.MonsterAi;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.FortressGeneralController;
import com.aionemu.gameserver.controllers.MonsterController;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;

public class FortressGeneral extends Npc
{	
	/**
	 * 
	 * @param objId
	 * @param controller
	 * @param spawn
	 * @param objectTemplate
	 */
	
	private int linkedFortressId;
	
	public FortressGeneral(int objId, FortressGeneralController controller, SpawnTemplate spawn, VisibleObjectTemplate objectTemplate, int fortressId)
	{
		super(objId, controller, spawn, objectTemplate);
		this.linkedFortressId = fortressId;
	}
	
	public int getFortressId()
	{
		return this.linkedFortressId;
	}

	@Override
	public FortressGeneralController getController()
	{
		return (FortressGeneralController) super.getController();
	}

	@Override
	public void initializeAi()
	{
		if(isAggressive() && !CustomConfig.DISABLE_MOB_AGGRO)
			this.ai = new AggressiveAi();
		else
			this.ai = new MonsterAi();
		
		ai.setOwner(this);	
	}
}
