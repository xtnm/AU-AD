-- ----------------------------
-- player_effects
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_effects` (
`player_id` int(11) NOT NULL,
`skill_id` int(11) NOT NULL,
`skill_lvl` tinyint NOT NULL,
`current_time`int(11) NOT NULL,
`reuse_delay` BIGINT(13) NOT NULL,
PRIMARY KEY (`player_id`, `skill_id`),
FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;