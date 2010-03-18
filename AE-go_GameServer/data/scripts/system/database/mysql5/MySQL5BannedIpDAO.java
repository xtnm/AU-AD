/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.gameserver.dao.BannedIpDAO;
import com.aionemu.gameserver.model.ban.BannedIP;

/**
 * BannedIP DAO implementation for MySQL5
 * 
 * @author SoulKeeper
 * @author Divinity - for the //ban command
 * 
 */
public class MySQL5BannedIpDAO extends BannedIpDAO
{
   private String playerIP;
   private boolean checkExist;

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean insert(final BannedIP bannedIP)
   {
      checkExist = false;
      if (checkExist(bannedIP.getMask()))
         return update(bannedIP);
         
      DB.insertUpdate("INSERT INTO banned_ip(mask, time_end) VALUES (?, ?)", new IUStH() {

         @Override
         public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
         {
            preparedStatement.setString(1, bannedIP.getMask());
            if (bannedIP.getTimeEnd() == null)
               preparedStatement.setNull(2, Types.TIMESTAMP);
            else
               preparedStatement.setTimestamp(2, bannedIP.getTimeEnd());
            preparedStatement.execute();
         }
      });

      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean update(final BannedIP bannedIP)
   {
      return DB.insertUpdate("UPDATE banned_ip SET time_end = ? WHERE mask = ?", new IUStH() {
         @Override
         public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
         {
            if (bannedIP.getTimeEnd() == null)
               preparedStatement.setNull(1, Types.TIMESTAMP);
            else
               preparedStatement.setTimestamp(1, bannedIP.getTimeEnd());
            preparedStatement.setString(2, bannedIP.getMask());
            preparedStatement.execute();
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean unban(final String name)
   {
      return DB.insertUpdate("DELETE FROM banned_ip WHERE mask = (SELECT last_ip FROM account_data AD, players P WHERE AD.name = P.account_name AND P.name = ?)", new IUStH() {
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
   public boolean checkExist(final String mask)
   {
      DB.select("SELECT * FROM banned_ip WHERE mask = ?", new ParamReadStH()
      {
         @Override
         public void setParams(PreparedStatement preparedStatement) throws SQLException
         {
            preparedStatement.setString(1, mask);
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
   public String getIP(final String player)
   {
      DB.select("SELECT last_ip FROM players P, account_data AD WHERE P.account_name = AD.name AND P.name = ?", new ParamReadStH()
      {
         @Override
         public void setParams(PreparedStatement preparedStatement) throws SQLException
         {
            preparedStatement.setString(1, player);
         }
         
         @Override
         public void handleRead(ResultSet resultSet) throws SQLException
         {
            resultSet.next();
            playerIP = resultSet.getString("last_ip");
         }
      });
      return playerIP;
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