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
package com.aionemu.gameserver.dao;

import java.sql.Timestamp;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.account.AccountTime;

/**
 * DAO that manages Banned Accounts
 * 
 * @author Divinity
 */
public abstract class AccountTimeDAO implements DAO
{
   /**
    * Updates BannedAccount object.<br>
    * In case of success returns true.<br>
    * In case of error returns false.<br>
    * 
    * @param account
    *            record to update
    * @return true in case of success or false in other case
    */
	public abstract boolean update(AccountTime account);
   
   /**
   * Return the player account id (in account_id column of player table in the database).
   * 
   * @return int - Player account name
   */
   public abstract int getAccountId(String player);
   
   /**
   * Unban the player
   *
   * @param player name
   * @return was it unban or not
   */
   public abstract boolean unban(String name);

   /**
    * Returns class name that will be uses as unique identifier for all DAO classes
    * 
    * @return class name
    */
   @Override
   public final String getClassName()
   {
      return AccountTimeDAO.class.getName();
   }
}