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

import java.util.Set;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.ban.BannedChat;

/**
 * DAO that manages Banned chat
 * 
 * @author Divinity
 */
public abstract class BannedChatDAO implements DAO
{
   /**
   * Load the banned players
   */
   public abstract Set<BannedChat> getAllBans();
   
   /**
    * Updates or Inserts BannedChat object.<br>
    * In case of success returns true.<br>
    * In case of error returns false.<br>
    * 
    * @param bannedChat
    * @return true in case of success or false in other case
    */
   public abstract boolean addOrUpdate(BannedChat bannedChat);
   
   /**
    * Updates BannedChat object.<br>
    * In case of success returns true.<br>
    * In case of error returns false.<br>
    * 
    * @param bannedChat
    * @return true in case of success or false in other case
    */
   public abstract boolean update(BannedChat bannedChat);
   
   /**
   * Check if player is already banned
   * @param name - player name
   * @return true if exist else false
   */
   public abstract boolean checkExist(String name);
   
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
      return BannedChatDAO.class.getName();
   }
}