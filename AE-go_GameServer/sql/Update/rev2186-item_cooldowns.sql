CREATE TABLE IF NOT EXISTS `item_cooldowns` (
`player_id` int(11) NOT NULL,
`delay_id` int(11) NOT NULL,
`use_delay` SMALLINT UNSIGNED NOT NULL,
`reuse_time` BIGINT(13) NOT NULL,
PRIMARY KEY (`player_id`, `delay_id`),
FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;