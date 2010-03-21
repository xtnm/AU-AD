/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.unishell;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * @author xitanium
 *
 */
public class CryptoHelper
{
	
	private static final Logger log = Logger.getLogger(CryptoHelper.class);
	
	/*
	 * Hashes a string using SHA-1 algorithm
	 */
	
	public static String encodeSHA1(String toEncode)
	{
		byte[] bytesToEncode = toEncode.getBytes(); 
		byte[] hash = null;  
	    try 
		{ 
	    	hash = MessageDigest.getInstance("MD5").digest(bytesToEncode);
	    } 
	    catch (Exception e) 
	    {
	       e.printStackTrace();
	       return null;
	    }
	    
	    StringBuffer hashString = new StringBuffer(); 
	    for ( int i = 0; i < hash.length; ++i ) 
	    { 
	       String hex = Integer.toHexString(hash[i]); 
	       if ( hex.length() == 1 ) 
	       { 
	    	   hashString.append('0'); 
	    	   hashString.append(hex.charAt(hex.length()-1)); 
	       } else 
	       { 
	    	   hashString.append(hex.substring(hex.length()-2)); 
	       } 
	    } 
	     return hashString.toString();	
	}

}
