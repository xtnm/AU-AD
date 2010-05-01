-- ----------------------------
-- inventory
-- ----------------------------

CREATE TABLE IF NOT EXISTS `inventory` (
  `itemUniqueId` int(11) NOT NULL,
  `itemId` int(11) NOT NULL,
  `itemCount` int(11) NOT NULL DEFAULT '0',
  `itemColor` int(11) NOT NULL DEFAULT '0',
  `itemOwner` int(11) NOT NULL,
  `isEquiped` TINYINT(1) NOT NULL DEFAULT '0',
  `slot` INT NOT NULL DEFAULT '0',
  `itemLocation` TINYINT(1) DEFAULT '0',
  `enchant` TINYINT(1) DEFAULT '0',
  PRIMARY KEY (`itemUniqueId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;