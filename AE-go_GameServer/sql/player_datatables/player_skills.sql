-- ----------------------------
-- player_skills
-- ----------------------------

CREATE TABLE IF NOT EXISTS `player_skills` (
  `player_id` int(11) NOT NULL,
  `skillId` int(11) NOT NULL,
  `skillLevel` int(3) NOT NULL default '1',
  PRIMARY KEY  (`player_id`,`skillId`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
