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
package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.FortressDAO;
import com.aionemu.gameserver.dao.NpcSpawnDAO;
import com.aionemu.gameserver.dao.TicketDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawn.NpcSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.restrictions.DisabledRestriction;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.world.WorldPosition;


/**
 * @author xitanium
 *
 */
public class MySQL5FortressDAO extends FortressDAO
{
	
	@Override
	public ArrayList<NpcSpawnTemplate> getAllTemplates(final int fortressId, final Race ownerFaction)
	{
		final ArrayList<NpcSpawnTemplate> templates = new ArrayList<NpcSpawnTemplate>();
		DB.select("SELECT npc_template_id, map, pos_x, pos_y, pos_z, heading FROM fortress_spawn_template WHERE fortress_id = ? AND race = ?", new ParamReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				while(arg0.next())
				{
					int npcTemplateId = arg0.getInt("npc_template_id");
					int map = arg0.getInt("map");
					float x = arg0.getFloat("pos_x");
					float y = arg0.getFloat("pos_y");
					float z = arg0.getFloat("pos_z");
					byte heading = arg0.getByte("heading");
					NpcSpawnTemplate template = new NpcSpawnTemplate(fortressId, npcTemplateId, map, x, y, z, heading);
					templates.add(template);
				}
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, fortressId);
				arg0.setString(2, ownerFaction.name());
			}
		});
		return templates;
	}
	
	@Override
	public void insertCache(final int fortressId, final int uniqueObjectId)
	{
		DB.insertUpdate("INSERT INTO fortress_spawn_cache(fortress_id, unique_objectid) VALUES (?,?)", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, fortressId);
				arg0.setInt(2, uniqueObjectId);
				arg0.execute();
			}
		});
	}
	
	@Override
	public void clearCache(final int fortressId)
	{
		DB.insertUpdate("DELETE FROM fortress_spawn_cache WHERE fortress_id = ?", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, fortressId);
				arg0.execute();
			}
		});
	}
	
	@Override
	public ArrayList<Integer> loadCache(final int fortressId)
	{
		final ArrayList<Integer> entries = new ArrayList<Integer>();
		DB.select("SELECT unique_objectid FROM fortress_spawn_cache WHERE fortress_id = ?", new ParamReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				while(arg0.next())
				{
					entries.add(arg0.getInt("unique_objectid"));
				}
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, fortressId);
			}
		});
		return entries;
	}
	
	@Override
	public NpcSpawnTemplate getGeneralSpawnTemplate(final int fortressId, final Race ownerFaction)
	{
		final NpcSpawnTemplate[] templates = new NpcSpawnTemplate[1];
		DB.select("SELECT general_template_id, map, pos_x, pos_y, pos_z, heading FROM fortress_generals WHERE fortress_id = ? AND race = ?", new ParamReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.next();
				int npcTemplateId = arg0.getInt("general_template_id");
				int map = arg0.getInt("map");
				float x = arg0.getFloat("pos_x");
				float y = arg0.getFloat("pos_y");
				float z = arg0.getFloat("pos_z");
				byte heading = arg0.getByte("heading");
				NpcSpawnTemplate template = new NpcSpawnTemplate(fortressId, npcTemplateId, map, x, y, z, heading);
				templates[0] = template;
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, fortressId);
				arg0.setString(2, ownerFaction.name());
			}
		});
		return templates[0];
	}
	
	@Override
	public void setFortressOwner(final int fortressId, final Race newOwner)
	{
		DB.insertUpdate("UPDATE fortress_owners SET owner_faction = ? WHERE fortress_id = ?", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setString(1, newOwner.name());
				arg0.setInt(2, fortressId);
				arg0.execute();
			}
		});
	}
	
	@Override
	public Race getCurrentFortressOwnerFaction(final int fortressId)
	{
		final Race[] ownerFaction = new Race[1];
		DB.select("SELECT owner_faction FROM fortress_owners WHERE fortress_id = ?", new ParamReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.next();
				String factionName = arg0.getString("owner_faction");
				if(factionName == "ASMODIANS")
				{
					ownerFaction[0] = Race.ASMODIANS;
				}
				else
				{
					ownerFaction[0] = Race.ELYOS;
				}
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, fortressId);
			}
		});
		return ownerFaction[0];
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
