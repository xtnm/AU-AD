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
package com.aionemu.gameserver.controllers;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BannedChatDAO;
import com.aionemu.gameserver.model.ban.BannedChat;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

/**
 * Class that controlls all chat banning activity
 * 
 * @author Divinity
 */
public class BannedChatController
{
   /**
    * Logger for this class.
    */
   private static final Logger      log   = Logger.getLogger(BannedChatController.class);
   
   /**
   * List of banned chat
   */
   private static Set<BannedChat>   banList;
   
   /**
    * Loads list of banned chat
    */
   public static void load()
   {
      reload();
   }

   /**
    * Loads list of banned chat
    */
   public static void reload()
   {
      // we are not going to make ip ban every minute, so it's ok to simplify a concurrent code a bit
      banList = new CopyOnWriteArraySet<BannedChat>(getDAO().getAllBans());
      log.info("BannedChatController loaded " + banList.size() + " chat bans.");
   }
   
   /**
    * Checks if player is banned
    * 
    * @param name
    * @return is it banned or not
    */
   public static boolean isBanned(String name)
   {
      for (BannedChat chatBan : banList)
      {
         if (chatBan.getName().equals(name) && chatBan.isActive())
            return true;
      }
      return false;
   }
   
   /**
   * Update the PenaltyEnd
   * @param BannedChat
   */
   public static boolean addOrUpdate(BannedChat bannedChat)
   {
      return getDAO().addOrUpdate(bannedChat);
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
    * Retuns {@link com.aionemu.loginserver.dao.BannedChatDAO} , just a shortcut
    * 
    * @return {@link com.aionemu.loginserver.dao.BannedChatDAO}
    */
   private static BannedChatDAO getDAO()
   {
      return DAOManager.getDAO(BannedChatDAO.class);
   }
}