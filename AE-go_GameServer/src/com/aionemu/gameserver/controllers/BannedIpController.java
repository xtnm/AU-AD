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
package com.aionemu.gameserver.controllers;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BannedIpDAO;
import com.aionemu.gameserver.model.ban.BannedIP;

/**
 * Class that controlls all ip banning activity
 * 
 * @author SoulKeeper
 * @author Divinity - for //ban command
 */
public class BannedIpController
{
   /**
    * Logger for this class.
    */
	@SuppressWarnings("unused")
   private static final Logger      log   = Logger.getLogger(BannedIpController.class);

   /**
    * Adds or updates ip ban. Changes are reflected in DB
    * 
    * @param ipBan
    *            banned ip to add or change
    * @return was it updated or not
    */
   public static boolean addOrUpdateBan(BannedIP ipBan)
   {
      if (getDAO().insert(ipBan))
         return true;
      else
         return false;
   }
   
   /**
   * Returns the player ip
   * 
   * @param player - String : player name
   * @return player ip
   */
   public static String getIP(String player)
   {
      return getDAO().getIP(player);
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
    * Retuns {@link com.aionemu.loginserver.dao.BannedIpDAO} , just a shortcut
    * 
    * @return {@link com.aionemu.loginserver.dao.BannedIpDAO}
    */
   private static BannedIpDAO getDAO()
   {
     return DAOManager.getDAO(BannedIpDAO.class);
   }
}