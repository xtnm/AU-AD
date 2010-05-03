CREATE TABLE `broker` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `itemPointer` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL,
  `itemCount` int(11) NOT NULL,
  `seller` varchar(14) NOT NULL,
  `price` int(11) NOT NULL DEFAULT '0',
  `brokerRace` enum('ELYOS','ASMODIAN') NOT NULL,
  `expireTime` timestamp NOT NULL DEFAULT '2010-01-01 02:00:00',
  `settleTime` timestamp NOT NULL DEFAULT '2010-01-01 02:00:00',
  `sellerId` int(11) NOT NULL,
  `isSold` tinyint(1) NOT NULL,
  `isSettled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8;