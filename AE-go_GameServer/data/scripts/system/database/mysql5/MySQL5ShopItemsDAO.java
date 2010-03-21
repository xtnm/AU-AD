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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.ShopItemsDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;

/**
 * @author kosyachok
 *
 */
public class MySQL5ShopItemsDAO extends ShopItemsDAO
{
	private static final Logger log = Logger.getLogger(MySQL5ShopItemsDAO.class);
	
	public List<Integer> loadAddedItems(final int playerId)
	{
		final List<Integer> items = new ArrayList<Integer>();
		
		DB.select("SELECT id,itemid, count FROM shop_additem WHERE playerid = ?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					int itemid = rset.getInt("itemid");
					int count = rset.getInt("count");
					for(int i=0; i < count; i++)
					{
						items.add(itemid);
					}
					log.info("added shop items : " + count + " x " + itemid);
					removeAddedItem(rset.getInt("id"));
					log.info("removed shop item field from DB");
				}
			}
		});
		
		return items;
	}
	
	public void removeAddedItem(final int id)
	{
		DB.insertUpdate("DELETE FROM shop_additem WHERE id = ?", new IUStH(){
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException
			{
				// TODO Auto-generated method stub
				arg0.setInt(1, id);
				arg0.execute();
			}
		});
	}
	
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
