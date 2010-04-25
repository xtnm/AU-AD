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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.gameserver.dao.TicketDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;


/**
 * @author Ark
 *
 */
public class MySQL5TicketDAO extends TicketDAO
{

	@Override
	public void insertTicket(final int type, final Player sender, final String title, final String message, final String addData)
	{
		DB.insertUpdate("INSERT INTO tickets(playerId, type, title, message, addData, time) VALUES (?,?,?,?,?,?)", new IUStH() {
			
			@Override
			public void handleInsertUpdate(PreparedStatement st) throws SQLException {
				// TODO Auto-generated method stub
				st.setInt(1, sender.getObjectId());
				st.setInt(2, type);
				st.setString(3, title);
				st.setString(4, message);
				st.setString(5, addData);
				Date now = new Date();
				st.setTimestamp(6, new Timestamp(now.getTime()));
				st.execute();
			}
		});
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
