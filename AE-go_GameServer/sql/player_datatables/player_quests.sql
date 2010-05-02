-- ----------------------------
-- player_quests
-- ----------------------------

CREATE TABLE IF NOT EXISTS `player_quests` (
`player_id` int(11) NOT NULL,
`quest_id` int(10) unsigned NOT NULL default '0',
`status` varchar(10) NOT NULL default 'NONE',
`quest_vars` int(10) unsigned NOT NULL default '0',
`complete_count` int(3) unsigned NOT NULL default '0',
PRIMARY KEY (`player_id`,`quest_id`),
FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;