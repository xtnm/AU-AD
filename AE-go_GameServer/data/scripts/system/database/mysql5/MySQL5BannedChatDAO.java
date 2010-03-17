/**
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.gameserver.dao.BannedChatDAO;
import com.aionemu.gameserver.model.ban.BannedChat;

/**
 * BannedChat DAO implementation for MySQL5
 * 
 * @author Divinity
 * 
 */
public class MySQL5BannedChatDAO extends BannedChatDAO
{
   private boolean checkExist;
   
   /**
    * {@inheritDoc}
    */
   @Override
   public Set<BannedChat> getAllBans()
   {
      final Set<BannedChat> result = new HashSet<BannedChat>();
      DB.select("SELECT * FROM banned_chat", new ReadStH() {
         @Override
         public void handleRead(ResultSet resultSet) throws SQLException
         {
            while (resultSet.next())
            {
               BannedChat chat = new BannedChat();
               chat.setName(resultSet.getString("player_name"));
               chat.setTimeEnd(resultSet.getTimestamp("time_end"));
               result.add(chat);
            }
         }
      });
      return result;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean addOrUpdate(final BannedChat bannedChat)
   {
      checkExist = false;
      if (checkExist(bannedChat.getName()))
         return update(bannedChat);
         
      DB.insertUpdate("INSERT INTO banned_chat (player_name, time_end) VALUES (?, ?)", new IUStH()
      {
         @Override
         public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
         {
            preparedStatement.setString(1, bannedChat.getName());
            if (bannedChat.getTimeEnd() == null)
               preparedStatement.setNull(2, Types.TIMESTAMP);
            else
               preparedStatement.setTimestamp(2, bannedChat.getTimeEnd());
            preparedStatement.execute();
         }
      });

      return true;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean update(final BannedChat bannedChat)
   {
      return DB.insertUpdate("UPDATE banned_chat SET time_end = ? WHERE player_name = ?", new IUStH() {
         @Override
         public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
         {
            if (bannedChat.getTimeEnd() == null)
               preparedStatement.setNull(1, Types.TIMESTAMP);
            else
               preparedStatement.setTimestamp(1, bannedChat.getTimeEnd());
            preparedStatement.setString(2, bannedChat.getName());
            preparedStatement.execute();
         }
      });
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean checkExist(final String name)
   {
      DB.select("SELECT * FROM banned_chat WHERE player_name = ?", new ParamReadStH()
      {
         @Override
         public void setParams(PreparedStatement preparedStatement) throws SQLException
         {
            preparedStatement.setString(1, name);
         }
         
         @Override
         public void handleRead(ResultSet resultSet) throws SQLException
         {
            if (resultSet.next())
               checkExist = true;
         }
      });
      return checkExist;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean unban(final String name)
   {
      return DB.insertUpdate("DELETE FROM banned_chat WHERE player_name = ?", new IUStH() {
         @Override
         public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
         {
            preparedStatement.setString(1, name);
            preparedStatement.execute();
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean supports(String s, int i, int i1)
   {
      return MySQL5DAOUtils.supports(s, i, i1);
   }
}