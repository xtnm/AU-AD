-- ----------------------------
-- abyss_rank
-- ----------------------------

CREATE TABLE IF NOT EXISTS `abyss_rank` (
  `player_id` int(11) NOT NULL,
  `ap` int(11) NOT NULL,
  `rank` int(2) NOT NULL default '1',
  `all_kill` int(4) NOT NULL default '0',
  `max_rank` int(2) NOT NULL default '1',
  PRIMARY KEY  (`player_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

