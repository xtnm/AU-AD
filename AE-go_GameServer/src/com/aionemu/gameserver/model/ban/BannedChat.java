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
package com.aionemu.gameserver.model.ban;

import java.sql.Timestamp;

/**
 * This class represents banned chat
 * 
 * @author Divinity
 */
public class BannedChat
{
   /**
    * Returns player name
    */
   private String      name;

   /**
    * Returns expiration time
    */
   private Timestamp   timeEnd;

   /**
    * Checks if ban is still active
    * 
    * @return true if ban is still active
    */
   public boolean isActive()
   {
      return timeEnd == null || timeEnd.getTime() > System.currentTimeMillis();
   }

   /**
    * Retuns player name
    * 
    * @return player name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Sets player name
    * 
    * @param name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Returns expiration time of ban
    * 
    * @return expiration time of ban
    */
   public Timestamp getTimeEnd()
   {
      return timeEnd;
   }

   /**
    * Sets expiration time of ban
    * 
    * @param timeEnd
    *            expiration time of ban
    */
   public void setTimeEnd(Timestamp timeEnd)
   {
      this.timeEnd = timeEnd;
   }
}