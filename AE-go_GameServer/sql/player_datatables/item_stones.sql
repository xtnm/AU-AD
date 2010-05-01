-- ----------------------------
-- item_stones
-- ----------------------------

CREATE TABLE IF NOT EXISTS `item_stones` (
  `itemUniqueId` int(11) NOT NULL,
  `itemId` int(11) NOT NULL,
  `slot` int(2) NOT NULL,
  `category` int(2) NOT NULL default 0,
  PRIMARY KEY (`itemUniqueId`, `slot`, `category`),
  FOREIGN KEY (`itemUniqueId`) references inventory (`itemUniqueId`) ON DELETE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;