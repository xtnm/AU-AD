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
package com.aionemu.gameserver.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BrokerDAO;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.broker.BrokerItemMask;
import com.aionemu.gameserver.model.templates.broker.BrokerMessages;
import com.aionemu.gameserver.model.templates.broker.BrokerRace;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BROKER_ITEMS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BROKER_REGISTERED_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BROKER_REGISTRATION_SERVICE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BROKER_SETTLED_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author kosyachok
 * 
 */
public class BrokerService
{
	private List<BrokerItem>	elyosBrokerItems		= new ArrayList<BrokerItem>();
	private List<BrokerItem>	elyosSettledItems		= new ArrayList<BrokerItem>();
	private List<BrokerItem>	asmodianBrokerItems		= new ArrayList<BrokerItem>();
	private List<BrokerItem>	asmodianSettledItems	= new ArrayList<BrokerItem>();

	private List<BrokerItem>	itemsToDelete			= new ArrayList<BrokerItem>();

	private static final Logger	log						= Logger.getLogger(BrokerService.class);

	@Inject
	private World				world;

	public void initBrokerService()
	{
		log.info("Loading broker...");
		int loadedBrokerItemsCount = 0;
		int loadedSettledItemsCount = 0;

		List<BrokerItem> brokerItems = DAOManager.getDAO(BrokerDAO.class).loadBroker();

		for(BrokerItem item : brokerItems)
		{
			if(item.getItemBrokerRace() == BrokerRace.ASMODIAN)
			{
				if(item.isSettled())
				{
					asmodianSettledItems.add(item);
					loadedSettledItemsCount++;
				}
				else
				{
					asmodianBrokerItems.add(item);
					loadedBrokerItemsCount++;
				}
			}
			else if(item.getItemBrokerRace() == BrokerRace.ELYOS)
			{
				if(item.isSettled())
				{
					elyosSettledItems.add(item);
					loadedSettledItemsCount++;
				}
				else
				{
					elyosBrokerItems.add(item);
					loadedBrokerItemsCount++;
				}
			}
			else
				continue;
		}

		log.info("Broker loaded with " + loadedBrokerItemsCount + " broker items, " + loadedSettledItemsCount
			+ " settled items.");
	}

	public void storeBroker()
	{
		checkExpiredItems();
		clearPlayersCache();
		brokerGarbageCleaner();

		boolean result = DAOManager.getDAO(BrokerDAO.class).storeBroker(itemsToDelete);

		DAOManager.getDAO(BrokerDAO.class).storeBroker(asmodianBrokerItems);
		DAOManager.getDAO(BrokerDAO.class).storeBroker(asmodianSettledItems);
		DAOManager.getDAO(BrokerDAO.class).storeBroker(elyosBrokerItems);
		DAOManager.getDAO(BrokerDAO.class).storeBroker(elyosSettledItems);

		if(result)
			itemsToDelete.clear();
	}

	private void brokerGarbageCleaner()
	{
		for(int i = 0; i < asmodianBrokerItems.size(); i++)
		{
			if(asmodianBrokerItems.get(i) == null)
				asmodianBrokerItems.remove(i);
		}

		for(int i = 0; i < elyosBrokerItems.size(); i++)
		{
			if(elyosBrokerItems.get(i) == null)
				elyosBrokerItems.remove(i);
		}
	}

	private void clearPlayersCache()
	{
		Iterator<Player> players = world.getPlayersIterator();

		while(players.hasNext())
		{
			Player player = players.next();

			player.getBrokerListCache().clear();
		}
	}

	public void showRequestedItems(Player player, int clientMask, int sortType, int startPage)
	{
		List<BrokerItem> searchItems = new ArrayList<BrokerItem>();

		if(player.getBrokerListCache().size() == 0 || player.getBrokerMaskCache() != clientMask)
			searchItems = getItemsByMask(player, clientMask);
		else
			searchItems = getItemsFromCache(player);

		if(searchItems == null || searchItems.size() < 0)
			return;

		int totalSearchItemsCount = searchItems.size();

		player.setBrokerSortTypeCache(sortType);
		player.setBrokerStartPageCache(startPage);

		sortBrokerItems(searchItems, sortType);
		searchItems = getRequestedPage(searchItems, startPage);

		PacketSendUtility.sendPacket(player, new SM_BROKER_ITEMS(searchItems, totalSearchItemsCount, startPage));
	}

	private List<BrokerItem> getItemsByMask(Player player, int clientMask)
	{
		List<BrokerItem> brokerItems = getRaceBrokerItems(player.getCommonData().getRace());
		if(brokerItems == null)
			return null;

		List<BrokerItem> searchItems = new ArrayList<BrokerItem>();
		FastMap<Integer, Integer> brokerListCache = new FastMap<Integer, Integer>();
		
		int itemIdModifier = 100000;
		int trueMask = clientMask;
		
		if(clientMask > 2000)
		{
			trueMask = BrokerItemMask.getBrokerMaskById(clientMask).getMask();
			
			if(trueMask <=0)
				return null;
			
			itemIdModifier = 1000;
		}

		for(int i = 0; i < brokerItems.size(); i++)
		{
			BrokerItem item = brokerItems.get(i);
			
			if(item == null || item.getItem() == null)
				continue;

			int itemMask = item.getItemId() / itemIdModifier;
			
			if(itemMask == (trueMask | itemMask))
			{
				searchItems.add(item);
				brokerListCache.put(item.getItemUniqueId(), i);
			}
		}

		player.setBrokerListCache(brokerListCache);
		player.setBrokerMaskCache(clientMask);

		return searchItems;
	}

	private List<BrokerItem> getItemsFromCache(Player player)
	{
		List<BrokerItem> brokerItems = new ArrayList<BrokerItem>();

		if(player.getCommonData().getRace() == Race.ASMODIANS)
			brokerItems = asmodianBrokerItems;
		else if(player.getCommonData().getRace() == Race.ELYOS)
			brokerItems = elyosBrokerItems;
		else
			return null;

		List<BrokerItem> cacheItems = new ArrayList<BrokerItem>();

		for(int i : player.getBrokerListCache().values())
		{
			BrokerItem item = brokerItems.get(i);

			if(item == null)
				continue;

			cacheItems.add(item);
		}

		return cacheItems;
	}

	/**
	 * Perform sorting according to sort type
	 * 
	 * @param brokerItems
	 * @param sortType
	 */
	private void sortBrokerItems(List<BrokerItem> brokerItems, int sortType)
	{
		Collections.sort(brokerItems, BrokerItem.getComparatoryByType(sortType));
	}
	
	/**
	 * 
	 * @param brokerItems
	 * @param startPage
	 * @return
	 */
	private List<BrokerItem> getRequestedPage(List<BrokerItem> brokerItems, int startPage)
	{
		List<BrokerItem> page = new ArrayList<BrokerItem>();
		int startingElement = startPage * 9;

		for(int i = startingElement, limit = 0; i < brokerItems.size() && limit < 45; i++, limit++)
		{
			page.add(brokerItems.get(i));
		}

		return page;
	}

	private List<BrokerItem> getRaceBrokerItems(Race race)
	{
		switch(race)
		{
			case ELYOS:
				return elyosBrokerItems;

			case ASMODIANS:
				return asmodianBrokerItems;

			default:
				return null;
		}
	}

	private List<BrokerItem> getRaceBrokerSettledItems(Race race)
	{
		switch(race)
		{
			case ELYOS:
				return elyosSettledItems;

			case ASMODIANS:
				return asmodianSettledItems;

			default:
				return null;
		}
	}

	public void buyBrokerItem(Player player, int itemUniqueId)
	{
		if(player.getInventory().isFull())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.MSG_FULL_INVENTORY);
			return;
		}

		BrokerItem buyingItem = null;
		int listIndex;
		boolean isEmptyCache = player.getBrokerListCache().size() == 0;
		Race playerRace = player.getCommonData().getRace();

		if(!isEmptyCache)
		{
			listIndex = player.getBrokerListCache().get(itemUniqueId);
			buyingItem = getRaceBrokerItems(playerRace).get(listIndex);
		}
		else
		{
			List<BrokerItem> brokerItems = getRaceBrokerItems(playerRace);
			listIndex = 0;
			for(BrokerItem item : brokerItems)
			{
				if(item.getItemUniqueId() == itemUniqueId)
				{
					buyingItem = item;
					break;
				}
				listIndex++;
			}
		}

		if(buyingItem == null)
			return; // TODO: Message "this item has already been bought, refresh page please."

		Item item = buyingItem.getItem();
		int price = buyingItem.getPrice();

		if(player.getInventory().getKinahItem().getItemCount() < price)
			return;

		getRaceBrokerItems(playerRace).set(listIndex, null);
		putToSettled(playerRace, buyingItem, true);

		if(!isEmptyCache)
			player.getBrokerListCache().remove(itemUniqueId);

		player.getInventory().decreaseKinah(price);
		Item boughtItem = player.getInventory().putToBag(item);
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(Collections.singletonList(boughtItem)));

		showRequestedItems(player, player.getBrokerMaskCache(), player.getBrokerSortTypeCache(), player
			.getBrokerStartPageCache());
	}

	private void putToSettled(Race race, BrokerItem brokerItem, boolean isSold)
	{
		if(isSold)
			brokerItem.removeItem();
		else
			brokerItem.setSettled();

		brokerItem.setPersistentState(PersistentState.UPDATE_REQUIRED);

		switch(race)
		{
			case ASMODIANS:
				asmodianSettledItems.add(brokerItem);
				break;

			case ELYOS:
				elyosSettledItems.add(brokerItem);
				break;
		}

		Player seller = world.findPlayer(brokerItem.getSellerId());

		if(seller != null)
		{
			PacketSendUtility.sendPacket(seller, new SM_BROKER_SETTLED_LIST(true));
			// TODO: Retail system message
		}
	}

	public void registerItem(Player player, int itemUniqueId, int price)
	{
		Item itemToRegister = player.getInventory().getItemByObjId(itemUniqueId);
		Race playerRace = player.getCommonData().getRace();

		if(itemToRegister == null)
			return;

		BrokerRace brRace;

		if(playerRace == Race.ASMODIANS)
			brRace = BrokerRace.ASMODIAN;
		else if(playerRace == Race.ELYOS)
			brRace = BrokerRace.ELYOS;
		else
			return;

		int registrationCommition = Math.round(price * 0.02f);

		if(registrationCommition < 10)
			registrationCommition = 10;

		if(player.getInventory().getKinahItem().getItemCount() < registrationCommition)
		{
			PacketSendUtility.sendPacket(player, new SM_BROKER_REGISTRATION_SERVICE(BrokerMessages.NO_ENOUGHT_KINAH
				.getId()));
		}

		player.getInventory().decreaseKinah(registrationCommition);

		player.getInventory().removeFromBag(itemToRegister, false);
		PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(itemToRegister.getObjectId()));

		itemToRegister.setItemLocation(126);
		//update item immediately
		DAOManager.getDAO(InventoryDAO.class).store(itemToRegister, player.getObjectId());

		BrokerItem newBrokerItem = new BrokerItem(itemToRegister, price, player.getName(), player.getObjectId(), brRace);

		switch(brRace)
		{
			case ASMODIAN:
				asmodianBrokerItems.add(newBrokerItem);
				break;

			case ELYOS:
				elyosBrokerItems.add(newBrokerItem);
				break;
		}

		PacketSendUtility.sendPacket(player, new SM_BROKER_REGISTRATION_SERVICE(newBrokerItem));
	}

	public void showRegisteredItems(Player player)
	{
		List<BrokerItem> brokerItems = getRaceBrokerItems(player.getCommonData().getRace());
		List<BrokerItem> registeredItems = new ArrayList<BrokerItem>();
		int playerId = player.getObjectId();

		for(BrokerItem item : brokerItems)
		{
			if(item != null && item.getItem() != null && playerId == item.getSellerId())
				registeredItems.add(item);
		}

		PacketSendUtility.sendPacket(player, new SM_BROKER_REGISTERED_LIST(registeredItems));
	}

	public void cancelRegisteredItem(Player player, int brokerItemId)
	{
		List<BrokerItem> brokerItems = getRaceBrokerItems(player.getCommonData().getRace());
		for(int i = 0; i < brokerItems.size(); i++)
		{
			BrokerItem brokerItem = brokerItems.get(i);
			if(brokerItem != null && brokerItem.getItemUniqueId() == brokerItemId)
			{
				Item item = player.getInventory().putToBag(brokerItem.getItem());
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(Collections.singletonList(item)));
				brokerItem.setPersistentState(PersistentState.DELETED);
				itemsToDelete.add(brokerItem);

				getRaceBrokerItems(player.getCommonData().getRace()).set(i, null);

				break;
			}
		}

		showRegisteredItems(player);
	}

	public void showSettledItems(Player player)
	{
		List<BrokerItem> brokerSettledItems = getRaceBrokerSettledItems(player.getCommonData().getRace());
		List<BrokerItem> settledItems = new ArrayList<BrokerItem>();

		int playerId = player.getObjectId();
		int totalKinah = 0;

		for(BrokerItem item : brokerSettledItems)
		{
			if(item != null && playerId == item.getSellerId())
			{
				settledItems.add(item);

				if(item.isSold())
					totalKinah += item.getPrice();
			}
		}

		PacketSendUtility.sendPacket(player, new SM_BROKER_SETTLED_LIST(settledItems, totalKinah));
	}

	public void settleAccount(Player player)
	{
		Race playerRace = player.getCommonData().getRace();
		List<BrokerItem> brokerSettledItems = getRaceBrokerSettledItems(playerRace);
		List<BrokerItem> collectedItems = new ArrayList<BrokerItem>();
		int playerId = player.getObjectId();
		int kinahCollect = 0;
		boolean itemsLeft = false;

		for(BrokerItem item : brokerSettledItems)
		{
			if(item.getSellerId() == playerId)
				collectedItems.add(item);
		}

		for(BrokerItem item : collectedItems)
		{
			if(item.isSold())
			{
				boolean result = false;
				switch(playerRace)
				{
					case ASMODIANS:
						result = asmodianSettledItems.remove(item);
						break;
					case ELYOS:
						result = elyosSettledItems.remove(item);
						break;
				}

				if(result)
				{
					item.setPersistentState(PersistentState.DELETED);
					itemsToDelete.add(item);
					kinahCollect += item.getPrice();
				}
			}
			else
			{
				if(item.getItem() != null)
				{
					Item resultItem = player.getInventory().putToBag(item.getItem());
					if(resultItem != null)
					{
						switch(playerRace)
						{
							case ASMODIANS:
								asmodianSettledItems.remove(item);
								break;
							case ELYOS:
								elyosSettledItems.remove(item);
								break;
						}

						PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(Collections
							.singletonList(resultItem)));
					}
					else
						itemsLeft = true;

				}
				else
					log.warn("Broker settled item missed. ObjID: " + item.getItemUniqueId());
			}
		}

		player.getInventory().increaseKinah(kinahCollect);

		showSettledItems(player);

		if(!itemsLeft)
			PacketSendUtility.sendPacket(player, new SM_BROKER_SETTLED_LIST(false));

	}

	private void checkExpiredItems()
	{
		List<BrokerItem> asmoBrokerItems = getRaceBrokerItems(Race.ASMODIANS);
		List<BrokerItem> elyosBrokerItems = getRaceBrokerItems(Race.ELYOS);

		Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());

		for(int i = 0; i < asmoBrokerItems.size(); i++)
		{
			BrokerItem item = asmoBrokerItems.get(i);
			if(item != null && item.getExpireTime().getTime() <= currentTime.getTime())
			{
				putToSettled(Race.ASMODIANS, item, false);
				this.asmodianBrokerItems.set(i, null);
			}
		}

		for(int i = 0; i < elyosBrokerItems.size(); i++)
		{
			BrokerItem item = elyosBrokerItems.get(i);
			if(item != null && item.getExpireTime().getTime() <= currentTime.getTime())
			{
				putToSettled(Race.ELYOS, item, false);
				this.elyosBrokerItems.set(i, null);
			}
		}
	}

	public void onPlayerLogin(Player player)
	{
		List<BrokerItem> brokerSettledItems = getRaceBrokerSettledItems(player.getCommonData().getRace());

		int playerId = player.getObjectId();

		for(BrokerItem item : brokerSettledItems)
		{
			if(item != null && playerId == item.getSellerId())
			{
				PacketSendUtility.sendPacket(player, new SM_BROKER_SETTLED_LIST(true));
				break;
			}
		}
	}
}
