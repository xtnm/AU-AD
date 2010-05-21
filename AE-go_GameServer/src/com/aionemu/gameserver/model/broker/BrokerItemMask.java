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
package com.aionemu.gameserver.model.broker;

import com.aionemu.gameserver.model.broker.filter.BrokerContainsExtraFilter;
import com.aionemu.gameserver.model.broker.filter.BrokerContainsFilter;
import com.aionemu.gameserver.model.broker.filter.BrokerFilter;
import com.aionemu.gameserver.model.broker.filter.BrokerMinMaxFilter;
import com.aionemu.gameserver.model.gameobjects.Item;

/**
 * @author kosyachok
 * @author Simple
 * @author ATracer
 */
public enum BrokerItemMask
{
	/**
	 * Weapon Section + sub categories
	 */
	WEAPON(9010, new BrokerMinMaxFilter(1000, 1018), null),
	WEAPON_SWORD(1000, new BrokerContainsFilter(1000), BrokerItemMask.WEAPON),
	WEAPON_MACE(1001, new BrokerContainsFilter(1001), BrokerItemMask.WEAPON),
	WEAPON_DAGGER(1002, new BrokerContainsFilter(1002), BrokerItemMask.WEAPON),
	WEAPON_ORB(1005, new BrokerContainsFilter(1005), BrokerItemMask.WEAPON),
	WEAPON_SPELLBOOK(1006, new BrokerContainsFilter(1006), BrokerItemMask.WEAPON),
	WEAPON_GREATSWORD(1009, new BrokerContainsFilter(1009), BrokerItemMask.WEAPON),
	WEAPON_POLEARM(1013, new BrokerContainsFilter(1013), BrokerItemMask.WEAPON),
	WEAPON_STAFF(1015, new BrokerContainsFilter(1015), BrokerItemMask.WEAPON),
	WEAPON_BOW(1017, new BrokerContainsFilter(1017), BrokerItemMask.WEAPON),

	/**
	 * Armor Section + sub categories
	 */
	ARMOR(9020, new BrokerMinMaxFilter(1101, 1160), null),
	ARMOR_CLOTHING(8010, new BrokerContainsFilter(1100, 1110, 1120, 1130, 1140), BrokerItemMask.ARMOR),
	ARMOR_CLOTHING_JACKET(1100, new BrokerContainsFilter(1100), BrokerItemMask.ARMOR_CLOTHING),
	ARMOR_CLOTHING_GLOVES(1110, new BrokerContainsFilter(1110), BrokerItemMask.ARMOR_CLOTHING),
	ARMOR_CLOTHING_PAULDRONS(1120, new BrokerContainsFilter(1120), BrokerItemMask.ARMOR_CLOTHING),
	ARMOR_CLOTHING_PANTS(1130, new BrokerContainsFilter(1130), BrokerItemMask.ARMOR_CLOTHING),
	ARMOR_CLOTHING_SHOES(1140, new BrokerContainsFilter(1140), BrokerItemMask.ARMOR_CLOTHING),
	ARMOR_CLOTH(8020, new BrokerContainsFilter(1101, 1111, 1121, 1131, 1141), BrokerItemMask.ARMOR),
	ARMOR_CLOTH_JACKET(1101, new BrokerContainsFilter(1101), BrokerItemMask.ARMOR_CLOTH),
	ARMOR_CLOTH_GLOVES(1111, new BrokerContainsFilter(1111), BrokerItemMask.ARMOR_CLOTH),
	ARMOR_CLOTH_PAULDRONS(1121, new BrokerContainsFilter(1121), BrokerItemMask.ARMOR_CLOTH),
	ARMOR_CLOTH_PANTS(1131, new BrokerContainsFilter(1131), BrokerItemMask.ARMOR_CLOTH),
	ARMOR_CLOTH_SHOES(1141, new BrokerContainsFilter(1141), BrokerItemMask.ARMOR_CLOTH),
	ARMOR_LEATHER(8030, new BrokerContainsFilter(1103, 1113, 1123, 1133, 1143), BrokerItemMask.ARMOR),
	ARMOR_LEATHER_JACKET(1103, new BrokerContainsFilter(1103), BrokerItemMask.ARMOR_LEATHER),
	ARMOR_LEATHER_GLOVES(1113, new BrokerContainsFilter(1113), BrokerItemMask.ARMOR_LEATHER),
	ARMOR_LEATHER_PAULDRONS(1123, new BrokerContainsFilter(1123), BrokerItemMask.ARMOR_LEATHER),
	ARMOR_LEATHER_PANTS(1133, new BrokerContainsFilter(1133), BrokerItemMask.ARMOR_LEATHER),
	ARMOR_LEATHER_SHOES(1143, new BrokerContainsFilter(1143), BrokerItemMask.ARMOR_LEATHER),
	ARMOR_CHAIN(8040, new BrokerContainsFilter(1105, 1115, 1125, 1135, 1145), BrokerItemMask.ARMOR),
	ARMOR_CHAIN_JACKET(1105, new BrokerContainsFilter(1105), BrokerItemMask.ARMOR_CHAIN),
	ARMOR_CHAIN_GLOVES(1115, new BrokerContainsFilter(1115), BrokerItemMask.ARMOR_CHAIN),
	ARMOR_CHAIN_PAULDRONS(1125, new BrokerContainsFilter(1125), BrokerItemMask.ARMOR_CHAIN),
	ARMOR_CHAIN_PANTS(1135, new BrokerContainsFilter(1135), BrokerItemMask.ARMOR_CHAIN),
	ARMOR_CHAIN_SHOES(1145, new BrokerContainsFilter(1145), BrokerItemMask.ARMOR_CHAIN),
	ARMOR_PLATE(8050, new BrokerContainsFilter(1106, 1116, 1126, 1136, 1146), BrokerItemMask.ARMOR),
	ARMOR_PLATE_JACKET(1106, new BrokerContainsFilter(1106), BrokerItemMask.ARMOR_PLATE),
	ARMOR_PLATE_GLOVES(1116, new BrokerContainsFilter(1116), BrokerItemMask.ARMOR_PLATE),
	ARMOR_PLATE_PAULDRONS(1126, new BrokerContainsFilter(1126), BrokerItemMask.ARMOR_PLATE),
	ARMOR_PLATE_PANTS(1136, new BrokerContainsFilter(1136), BrokerItemMask.ARMOR_PLATE),
	ARMOR_PLATE_SHOES(1146, new BrokerContainsFilter(1146), BrokerItemMask.ARMOR_PLATE),
	ARMOR_SHIELD(1150, new BrokerContainsFilter(1150), BrokerItemMask.ARMOR),

	/**
	 * Accessory Section + sub categories
	 */
	ACCESSORY(9030, new BrokerMinMaxFilter(1200, 1270), null),
	ACCESSORY_EARRINGS(1200, new BrokerContainsFilter(1200), BrokerItemMask.ACCESSORY),
	ACCESSORY_NECKLACE(1210, new BrokerContainsFilter(1210), BrokerItemMask.ACCESSORY),
	ACCESSORY_RING(1220, new BrokerContainsFilter(1220), BrokerItemMask.ACCESSORY),
	ACCESSORY_BELT(1230, new BrokerContainsFilter(1230), BrokerItemMask.ACCESSORY),
	ACCESSORY_HEADGEAR(7030, new BrokerMinMaxFilter(1250, 1270), BrokerItemMask.ACCESSORY),
	/**
	 * Skill related Section + sub categories
	 */
	SKILL_RELATED(9040, new BrokerContainsFilter(1400, 1695), null),
	SKILL_RELATED_STIGMA(1400, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED),
	SKILL_RELATED_STIGMA_GLADIATOR(6010, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_STIGMA_TEMPLAR(6011, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_STIGMA_ASSASSIN(6012, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_STIGMA_RANGER(6013, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_STIGMA_SORCERER(6014, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_STIGMA_SPIRITMASTER(6015, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_STIGMA_CLERIC(6016, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_STIGMA_CHANTER(6017, new BrokerContainsFilter(1400), BrokerItemMask.SKILL_RELATED_STIGMA),
	SKILL_RELATED_SKILL_MANUAL(1695, new BrokerContainsFilter(1695), BrokerItemMask.SKILL_RELATED),
	SKILL_RELATED_SKILL_MANUAL_GLADIATOR(6020, new BrokerContainsFilter(1695),
		BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),
	SKILL_RELATED_SKILL_MANUAL_TEMPLAR(6021, new BrokerContainsFilter(1695), BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),
	SKILL_RELATED_SKILL_MANUAL_ASSASSIN(6022, new BrokerContainsFilter(1695), BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),
	SKILL_RELATED_SKILL_MANUAL_RANGER(6023, new BrokerContainsFilter(1695), BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),
	SKILL_RELATED_SKILL_MANUAL_SORCERER(6024, new BrokerContainsFilter(1695), BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),
	SKILL_RELATED_SKILL_MANUAL_SPIRITMASTER(6025, new BrokerContainsFilter(1695),
		BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),
	SKILL_RELATED_SKILL_MANUAL_CLERIC(6026, new BrokerContainsFilter(1695), BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),
	SKILL_RELATED_SKILL_MANUAL_CHANTER(6027, new BrokerContainsFilter(1695), BrokerItemMask.SKILL_RELATED_SKILL_MANUAL),

	/**
	 * Craft Section + sub categories
	 */
	CRAFT(9050, new BrokerContainsFilter(1520, 1522), null),
	CRAFT_MATERIALS(1520, new BrokerContainsFilter(1520), BrokerItemMask.CRAFT),
	CRAFT_MATERIALS_COLLECTION(6030, new BrokerContainsExtraFilter(15200), BrokerItemMask.CRAFT_MATERIALS),
	CRAFT_MATERIALS_GAIN(6031, new BrokerContainsExtraFilter(15201), BrokerItemMask.CRAFT_MATERIALS),
	CRAFT_MATERIALS_PARTS(6032, new BrokerContainsExtraFilter(15202), BrokerItemMask.CRAFT_MATERIALS),
	CRAFT_DESIGN(1522, new BrokerContainsFilter(1522), BrokerItemMask.CRAFT),
	CRAFT_DESIGN_WEAPONSMITHING(6040, new BrokerContainsFilter(1522), BrokerItemMask.CRAFT_DESIGN),
	CRAFT_DESIGN_ARMORSMITHING(6041, new BrokerContainsFilter(1522), BrokerItemMask.CRAFT_DESIGN),
	CRAFT_DESIGN_TAILORING(6042, new BrokerContainsFilter(1522), BrokerItemMask.CRAFT_DESIGN),
	CRAFT_DESIGN_HANDICRAFTING(6043, new BrokerContainsFilter(1522), BrokerItemMask.CRAFT_DESIGN),
	CRAFT_DESIGN_ALCHEMY(6044, new BrokerContainsFilter(1522), BrokerItemMask.CRAFT_DESIGN),
	CRAFT_DESIGN_COOKING(6045, new BrokerContainsFilter(1522), BrokerItemMask.CRAFT_DESIGN),

	/**
	 * Consumables Section + sub categories
	 */
	CONSUMABLES(9060, new BrokerContainsFilter(1600, 1620, 1640), null),
	CONSUMABLES_FOOD(1600, new BrokerContainsFilter(1600), BrokerItemMask.CONSUMABLES),
	CONSUMABLES_POTION(1620, new BrokerContainsFilter(1620), BrokerItemMask.CONSUMABLES),
	CONSUMABLES_SCROLL(7060, new BrokerContainsFilter(1640), BrokerItemMask.CONSUMABLES),
	CONSUMABLES_MODIFY(8060, new BrokerContainsFilter(1660, 1670, 1680, 1692), BrokerItemMask.CONSUMABLES),
	CONSUMABLES_MODIFY_ENCHANTMENT_STONE(1660, new BrokerContainsFilter(1660), BrokerItemMask.CONSUMABLES_MODIFY),
	CONSUMABLES_MODIFY_MANASTONE(1670, new BrokerContainsFilter(1670), BrokerItemMask.CONSUMABLES_MODIFY),
	CONSUMABLES_MODIFY_GODSTONE(1680, new BrokerContainsFilter(1680), BrokerItemMask.CONSUMABLES_MODIFY),
	CONSUMABLES_MODIFY_DYE(7061, new BrokerContainsFilter(1692), BrokerItemMask.CONSUMABLES_MODIFY),
	CONSUMABLES_OTHER(7062, new BrokerContainsFilter(0), BrokerItemMask.CONSUMABLES), // TODO

	/**
	 * Other Section
	 */
	OTHER(7070, new BrokerContainsFilter(0), null), // TODO

	UNKNOWN(1, new BrokerContainsFilter(0), null);

	private int				typeId;
	private BrokerFilter	filter;
	private BrokerItemMask	parent;

	/**
	 * 
	 * @param typeId
	 * @param filter
	 * @param parent
	 */
	BrokerItemMask(int typeId, BrokerFilter filter, BrokerItemMask parent)
	{
		this.typeId = typeId;
		this.filter = filter;
		this.parent = parent;
	}

	/**
	 * @return the typeId
	 */
	public int getId()
	{
		return typeId;
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean isMatches(Item item)
	{
		return filter.accept(item.getItemTemplate());
	}

	/**
	 * 
	 * @param maskId
	 * @return
	 */
	public boolean isChildrenMask(int maskId)
	{
		for(BrokerItemMask p = parent; p != null; p = p.parent)
		{
			if(p.typeId == maskId)
				return true;
		}
		return false;
	}

	/**
	 * Return BrokerListType by id.
	 * 
	 * @param id
	 * @return BrokerListType
	 */
	public static BrokerItemMask getBrokerMaskById(int id)
	{
		for(BrokerItemMask mt : values())
		{
			if(mt.typeId == id)
				return mt;
		}
		return UNKNOWN;
	}
}
