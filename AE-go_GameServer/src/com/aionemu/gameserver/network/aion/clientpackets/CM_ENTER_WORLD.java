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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.ShopItemsDAO;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ENTER_WORLD_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_ID;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRICES;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECIPE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UI_SETTINGS;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.ChatService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.GroupService;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.MailService;
import com.aionemu.gameserver.services.PeriodicSaveService;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * In this packets aion client is asking if given char [by oid] may login into game [ie start playing].
 * 
 * @author -Nemesiss-, Avol, xitanium
 * 
 */
public class CM_ENTER_WORLD extends AionClientPacket
{
	/**
	 * Object Id of player that is entering world
	 */
	private int					objectId;
	@Inject
	private World				world;
	@Inject
	private PlayerService		playerService;
	@Inject
	private LegionService		legionService;
	@Inject
	private GroupService		groupService;
	@Inject
	private TeleportService		teleportService;
	@Inject
	private PunishmentService	punishmentService;
	@Inject
	private MailService			mailService;
	@Inject
	private ItemService			itemService;
	@Inject
	private StigmaService		stigmaService;
	@Inject
	private PeriodicSaveService periodicSaveService;
	@Inject
	private BrokerService		brokerService;
	@Inject
	private ChatService			chatService;

	/**
	 * Constructs new instance of <tt>CM_ENTER_WORLD </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_ENTER_WORLD(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		objectId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		AionConnection client = getConnection();
		Account account = client.getAccount();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);

		if(playerAccData == null)
		{
			// Somebody wanted to login on character that is not at his account
			return;
		}

		Player player = playerService.getPlayer(objectId, account);

		if(player != null && client.setActivePlayer(player))
		{
			player.setClientConnection(client);
			/*
			 * Store player into World.
			 */
			world.storeObject(player);

			sendPacket(new SM_SKILL_LIST(player));
			stigmaService.onPlayerLogin(player);
			
			if(player.getSkillCoolDowns() != null)
				sendPacket(new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));
			
			if(player.getItemCoolDowns() != null)
				sendPacket(new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));

			sendPacket(new SM_QUEST_LIST(player));
			sendPacket(new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));

			/*
			 * Needed
			 */
			sendPacket(new SM_ENTER_WORLD_CHECK());

			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();

			if(uiSettings != null)
				sendPacket(new SM_UI_SETTINGS(uiSettings, 0));

			if(shortcuts != null)
				sendPacket(new SM_UI_SETTINGS(shortcuts, 1));

			// Cubesize limit set in inventory.
			int cubeSize = player.getCubeSize();
			player.getInventory().setLimit(27 + cubeSize * 9);

			// items
			Storage inventory = player.getInventory();
			List<Item> equipedItems = player.getEquipment().getEquippedItems();
			if(equipedItems.size() != 0)
			{
				sendPacket(new SM_INVENTORY_INFO(player.getEquipment().getEquippedItems(), cubeSize));
			}

			List<Item> unequipedItems = inventory.getAllItems();
			int itemsSize = unequipedItems.size();

			if(itemsSize != 0)
			{
				int index = 0;
				while(index + 10 < itemsSize)
				{
					sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, index + 10), cubeSize));
					index += 10;
				}
				sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, itemsSize), cubeSize));
			}

			/*
			 * Shop Items
			 */
			List<Integer> shopItems = DAOManager.getDAO(ShopItemsDAO.class).loadAddedItems(player.getObjectId());
			
			if(shopItems.size() > 0)
			{
				Iterator<Integer> items = shopItems.iterator();
				int buffer = 0;
				int kinahCount = 0;
				
				while(items.hasNext())
				{
					int iid = items.next();
					if(player.getInventory().getKinahItem().getItemTemplate().getTemplateId() == iid)
					{
						kinahCount++;
					}
					else
					{
						itemService.addItem(player, iid, 1);
					}
				}
				
				if(kinahCount > 0)
				{
					itemService.addItem(player, player.getInventory().getKinahItem().getItemTemplate().getTemplateId(), kinahCount);
				}
			}
			sendPacket(new SM_INVENTORY_INFO());

			playerService.playerLoggedIn(player);
			
			sendPacket(new SM_STATS_INFO(player));
			sendPacket(new SM_CUBE_UPDATE(player, 6));

			teleportService.sendSetBindPoint(player);

			sendPacket(new SM_MACRO_LIST(player));
			sendPacket(new SM_GAME_TIME());
			player.getController().updateNearbyQuests();

			sendPacket(new SM_TITLE_LIST(player));		
			sendPacket(new SM_CHANNEL_INFO(player.getPosition()));
			sendPacket(new SM_PLAYER_SPAWN(player));
			sendPacket(new SM_EMOTION_LIST());
			sendPacket(new SM_INFLUENCE_RATIO());
			sendPacket(new SM_PRICES());
			sendPacket(new SM_PLAYER_ID(player));
			sendPacket(new SM_ABYSS_RANK(player.getAbyssRank()));

			sendPacket(new SM_MESSAGE(0, null, "Bienvenue sur " + GSConfig.SERVER_NAME
				+ "\nEmulateur Aion Dream Emu v2 RC1\nFork Aion Unique by Aion Dream Team",
				ChatType.ANNOUNCEMENTS));

			if(player.isInPrison())
				punishmentService.updatePrisonStatus(player);

			if(player.isLegionMember())
				legionService.onLogin(player);

			if(player.isInGroup())
				groupService.onLogin(player);

			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));

			ClassChangeService.showClassChangeDialog(player);
			
			/**
			 * Notify mail service to load all mails
			 */
			mailService.onPlayerLogin(player);
			/**
			 * Schedule periodic saving of this player
			 */
			periodicSaveService.onPlayerLogin(player);
			/**
			 * Notify player if have broker settled items
			 */
			brokerService.onPlayerLogin(player);
			/**
			 * Start initializing chat connection(/1, /2, /3, /4 channels)
			 */
			if(!GSConfig.DISABLE_CHAT_SERVER)
				chatService.onPlayerLogin(player);
		}
		else
		{
			// TODO this is an client error - inform client.
		}
	}

}