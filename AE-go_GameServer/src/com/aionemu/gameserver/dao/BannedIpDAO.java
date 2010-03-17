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
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.ban.BannedIP;

/**
 * DAO that manages Banned IPs
 * 
 * @author SoulKeeper
 * @author Divinity - for //ban command
 */
public abstract class BannedIpDAO implements DAO
{

   /**
    * Inserts BannedIP object to database.<br>
    * ID of object must be NULL.<br>
    * If insert was successfull - sets the assigned id to BannedIP object and returns true.<br>
    * In case of error returns false without modification of bannedIP object.<br>
    * 
    * @param bannedIP
    *            record to add to db
    * @return true in case of success or false
    */
   public abstract boolean insert(BannedIP bannedIP);
   /**
    * Updates BannedIP object.<br>
    * ID of object must NOT be null.<br>
    * In case of success returns true.<br>
    * In case of error returns false.<br>
    * 
    * @param bannedIP
    *            record to update
    * @return true in case of success or false in other case
    */
   public abstract boolean update(BannedIP bannedIP);
   
   /**
   * Return the player IP (in last_ip column of account_data table in the database).
   * 
   * @return String - Player IP
   */
   public abstract String getIP(String player);
   
   /**
   * Check if the player already exist in banned_ip table in the database.
   * 
   * @param mask - String : player ip
   * @return boolean - True : Already exist in the database | False : Not exist.
   */
   public abstract boolean checkExist(String mask);
   
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
      return BannedIpDAO.class.getName();
   }
}