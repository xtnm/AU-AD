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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.NpcSpawnDAO;
import com.aionemu.gameserver.dao.TicketDAO;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawn.NpcSpawnTemplate;
import com.aionemu.gameserver.restrictions.DisabledRestriction;
import com.aionemu.gameserver.world.WorldPosition;


/**
 * @author xitanium
 *
 */
public class MySQL5NpcSpawnDAO extends NpcSpawnDAO
{
	
	@Override
	public ArrayList<NpcSpawnTemplate> getAllTemplates()
	{
		final ArrayList<NpcSpawnTemplate> templates = new ArrayList<NpcSpawnTemplate>();
		DB.select("SELECT template_id, npc_template_id, map, pos_x, pos_y, pos_z, heading FROM npc_spawn_template", new ParamReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				while(arg0.next())
				{
					int templateId = arg0.getInt("template_id");
					int npcTemplateId = arg0.getInt("npc_template_id");
					int map = arg0.getInt("map");
					float x = arg0.getFloat("pos_x");
					float y = arg0.getFloat("pos_y");
					float z = arg0.getFloat("pos_z");
					byte heading = arg0.getByte("heading");
					NpcSpawnTemplate tpl = new NpcSpawnTemplate(templateId, npcTemplateId, map, x, y, z, heading);
					templates.add(tpl);
				}
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				
			}
		});
		return templates;
	}
	
	@Override
	public synchronized int insertTemplate(final int npcId, final WorldPosition position)
	{
		final ArrayList<Integer> template = new ArrayList<Integer>();
		DB.insertUpdate("INSERT INTO npc_spawn_template(npc_template_id, map, pos_x, pos_y, pos_z, heading) VALUES (?,?,?,?,?,?)", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, npcId);
				arg0.setInt(2, position.getMapId());
				arg0.setFloat(3, position.getX());
				arg0.setFloat(4, position.getY());
				arg0.setFloat(5, position.getZ());
				arg0.setByte(6, position.getHeading());
				arg0.execute();
			}
		});
		DB.select("SELECT MAX(template_id) as template_value FROM npc_spawn_template", new ParamReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.first();
				template.add(arg0.getInt("template_value"));
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				
			}
		});
		return template.get(0);
	}
	
	@Override
	public void insertCache(final int templateId, final int uniqueObjectId)
	{
		DB.insertUpdate("INSERT INTO npc_spawn_cache(template_id, unique_objectid) VALUES (?,?)", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, templateId);
				arg0.setInt(2, uniqueObjectId);
				arg0.execute();
			}
		});
	}
	
	@Override
	public void clearCache()
	{
		DB.insertUpdate("DELETE FROM npc_spawn_cache", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.execute();
			}
		});
	}
	
	@Override
	public void deleteFromCache(final int templateId)
	{
		DB.insertUpdate("DELETE FROM npc_spawn_cache WHERE template_id = ?", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, templateId);
				arg0.execute();
			}
		});
	}
	
	@Override
	public void deleteTemplate(final int templateId)
	{
		DB.insertUpdate("DELETE FROM npc_spawn_template WHERE template_id = ?", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, templateId);
				arg0.execute();
			}
		});
	}
	
	@Override
	public int getCacheEntryRelatedTemplate(final int uniqueObjectId)
	{
		final ArrayList<Integer> resultTemplate = new ArrayList<Integer>();
		DB.select("SELECT template_id FROM npc_spawn_cache WHERE unique_objectid = ?", new ParamReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				if(arg0.first())
				{
					resultTemplate.add(arg0.getInt("template_id"));
				}
				else
				{
					resultTemplate.add(0);
				}
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				// TODO Auto-generated method stub
				arg0.setInt(1, uniqueObjectId);
			}
		});
		return resultTemplate.get(0);
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
