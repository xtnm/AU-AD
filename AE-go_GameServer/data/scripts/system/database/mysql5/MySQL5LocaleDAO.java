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
import com.aionemu.commons.database.ReadStH;
import com.aionemu.gameserver.dao.FortressDAO;
import com.aionemu.gameserver.dao.LocaleDAO;
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
public class MySQL5LocaleDAO extends LocaleDAO
{
	
	@Override
	public Map<Integer, String> loadLocalesByLang(final String locale)
	{
		final Map<Integer, String> result = new HashMap<Integer, String>();
		DB.select("SELECT locale_id, string_" + locale.toLowerCase() + " FROM locales", new ReadStH() {
			
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				// TODO Auto-generated method stub
				while(arg0.next())
				{
					result.put(arg0.getInt("locale_id"), arg0.getString("locale_"+locale));
				}
			}
		});
		return result;
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
