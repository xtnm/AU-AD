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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.BrokerService;

/**
 * @author kosyachok
 *
 */
public class SM_BROKER_SETTLED_LIST extends AionServerPacket
{
	List<BrokerItem> settledItems;
	private int totalKinah;
	private int haveItemsIcon;
	private int unk;
	
	/**
	 * Sending settled items list
	 * @param settledItems
	 * @param totalKinah
	 */
	public SM_BROKER_SETTLED_LIST(List<BrokerItem> settledItems, int totalKinah)
	{
		this.settledItems = settledItems;
		this.totalKinah = totalKinah;
		this.haveItemsIcon = 0;
		this.unk = 0;
	}
	
	public SM_BROKER_SETTLED_LIST(boolean haveItems)
	{
		this.settledItems = new ArrayList<BrokerItem>();
		this.totalKinah = 0;
		if(haveItems)
			this.haveItemsIcon = 1;
		else
			this.haveItemsIcon = 0;
		this.unk = 1;
	}
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, totalKinah);
		writeD(buf, haveItemsIcon);
		writeH(buf, 1);
		writeD(buf, 0);//TODO: Pages
		writeC(buf, unk);//
		writeH(buf, settledItems.size());
		if(settledItems.size() > 0)
		{
			writeD(buf, settledItems.get(0).getItemId());
			if(settledItems.get(0).isSold())
				writeD(buf, settledItems.get(0).getPrice());
			else
				writeD(buf, 0);
			writeD(buf, 0);
			writeH(buf, settledItems.get(0).getItemCount());
			writeD(buf, 0);
			writeH(buf, 0);
			writeH(buf, settledItems.get(0).getItemCount());
			writeD(buf, 0);
			writeH(buf, 0);
			writeD(buf, (int)(settledItems.get(0).getSettleTime().getTime() / 60000));
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			for(int i = 1; i < settledItems.size(); i++)
			{
				writeD(buf, settledItems.get(i).getItemId());
				if(settledItems.get(i).isSold())
					writeD(buf, settledItems.get(i).getPrice());
				else
					writeD(buf, 0);
				writeD(buf, 0);
				writeH(buf, settledItems.get(i).getItemCount());
				writeD(buf, 0);
				writeH(buf, 0);
				writeH(buf, settledItems.get(i).getItemCount());
				writeD(buf, 0);
				writeH(buf, 0);
				writeD(buf, (int)settledItems.get(i).getSettleTime().getTime() / 60000);
				writeH(buf, 0);
				writeD(buf, settledItems.get(i).getItemId());
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 0);
				writeH(buf, 0);
			}
		}
	}
}
