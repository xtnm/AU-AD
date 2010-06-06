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
package com.aionemu.gameserver.dao;

import java.util.ArrayList;
import java.util.Map;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawn.NpcSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author xitanium
 *
 */
public abstract class FortressDAO implements DAO
{

	@Override
	public final String getClassName()
	{
		 return FortressDAO.class.getName();
	}
	
	public abstract void insertCache(int fortressId, int uniqueObjectId);
	
	public abstract ArrayList<NpcSpawnTemplate> getAllTemplates(final int fortressId, final Race ownerFaction);
	
	public abstract void clearCache(final int fortressId);
	
	public abstract ArrayList<Integer> loadCache(final int fortressId);
	
	public abstract NpcSpawnTemplate getGeneralSpawnTemplate(final int fortressId, final Race ownerFaction);
	
	public abstract Race getCurrentFortressOwnerFaction(final int fortressId);
	
	public abstract void setFortressOwner(final int fortressId, final Race newOwner);
	
}
