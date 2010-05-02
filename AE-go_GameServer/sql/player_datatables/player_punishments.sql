-- ----------------------------
-- player_punisments
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_punishments` (
`player_id` int(11) NOT NULL,
`punishment_status` TINYINT UNSIGNED DEFAULT 0,
`punishment_timer` INT UNSIGNED DEFAULT 0,
PRIMARY KEY (`player_id`),
FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;