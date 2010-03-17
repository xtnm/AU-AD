/**
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.controllers;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AccountTimeDAO;
import com.aionemu.gameserver.model.account.AccountTime;

/**
 * Class that controlls all account banning activity
 * 
 * @author Divinity
 */
public class AccountTimeController
{
   /**
    * Logger for this class.
    */
   @SuppressWarnings("unused")
   private static final Logger      log   = Logger.getLogger(AccountTimeController.class);
   
   /**
   * Update the PenaltyEnd
   * @param AccountTime
   */
   public static boolean update(AccountTime account)
   {
      return getDAO().update(account);
   }
   
   /**
   * Returns the player account id
   * 
   * @param player - String : player name
   * @return player account id
   */
   public static int getAccountId(String player)
   {
      return getDAO().getAccountId(player);
   }
   
   /**
   * Unban the player
   *
   * @param name : player name
   * @return was it unbanned or not
   */
   public static boolean unban(String name)
   {
      return getDAO().unban(name);
   }

   /**
    * Retuns {@link com.aionemu.loginserver.dao.AccountTimeDAO} , just a shortcut
    * 
    * @return {@link com.aionemu.loginserver.dao.AccountTimeDAO}
    */
   private static AccountTimeDAO getDAO()
   {
      return DAOManager.getDAO(AccountTimeDAO.class);
   }
}