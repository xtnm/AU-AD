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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.gameserver.dao.AccountTimeDAO;
import com.aionemu.gameserver.model.account.AccountTime;

/**
 * AccountTime DAO implementation for MySQL5
 * 
 * @author Divinity
 * 
 */
public class MySQL5AccountTimeDAO extends AccountTimeDAO
{
   private int   playerAccountId;

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean update(AccountTime account)
   {
	   return false;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int getAccountId(final String player)
   {
      DB.select("SELECT account_id FROM players WHERE name = ?", new ParamReadStH()
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
            playerAccountId = resultSet.getInt("account_id");
         }
      });
      return playerAccountId;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean unban(final String name)
   {
     return DB.insertUpdate("UPDATE account_time SET penalty_end = NULL WHERE account_id = (SELECT account_id FROM players WHERE name = ?)", new IUStH() {
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