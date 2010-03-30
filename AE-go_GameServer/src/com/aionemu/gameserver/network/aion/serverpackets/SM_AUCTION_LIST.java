/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.controllers.ReviveType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author xitanium
 * 
 */
public class SM_AUCTION_LIST extends AionServerPacket
{

	public SM_AUCTION_LIST()
	{
		
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeH(buf, 25);
		writeC(buf, 0);
		writeD(buf, 0);
		
		for(int i=0; i<25; i++)
		{
			//item 1
			writeD(buf, 0);
			writeH(buf, 0);
			writeD(buf, 100000450);
			writeH(buf, 45000);
			writeD(buf, 0);
			writeC(buf, 0);
			writeH(buf, 1);
			writeD(buf, 0);
			writeD(buf, 0);
			writeC(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeS(buf, "Xitanium" + i);
		}
		
		// end
		writeH(buf, 0);
		

		// [trisphere] Structure is actually:
		// uint8 skillRevive
		// uint8 itemRevive
		// uint32 kiskReviveDelay (wait time before choice can be made)
	}
}
