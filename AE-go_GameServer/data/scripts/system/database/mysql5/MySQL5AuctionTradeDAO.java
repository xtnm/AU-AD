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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AuctionTradeDAO;
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
 * @author xitanium
 *
 */
public class MySQL5AuctionTradeDAO extends AuctionTradeDAO
{
	private static final Logger log = Logger.getLogger(MySQL5AuctionTradeDAO.class);
	
	public void insertTrade(final Player player, final Item item, final int price)
	{
		Date now = new Date();
		Timestamp currentTimestamp = new Timestamp(now.getTime());
		final long timestamp = currentTimestamp.getTime();
		
		DB.insertUpdate("INSERT INTO auction_trades(seller, item, count, price, start) VALUES(?, ?, ?, ?, ?)", new IUStH(){
			
			@Override
			public void handleInsertUpdate(PreparedStatement arg0) throws SQLException
			{
				// TODO Auto-generated method stub
				arg0.setInt(1, player.getObjectId());
				arg0.setInt(2, item.getItemTemplate().getTemplateId());
				arg0.setInt(3, item.getItemCount());
				arg0.setInt(4, price);
				arg0.setLong(5, timestamp);
				
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
