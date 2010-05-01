-- ----------------------------
-- legions
-- ----------------------------

CREATE TABLE IF NOT EXISTS `legions` (
  `id` int(11) NOT NULL,
  `name` varchar(16) NOT NULL,
  `level` int(1) NOT NULL DEFAULT '1',
  `contribution_points` INT NOT NULL DEFAULT '0',
  `legionar_permission2` int(11) NOT NULL DEFAULT '64',
  `centurion_permission1` int(11) NOT NULL DEFAULT '104',
  `centurion_permission2` int(11) NOT NULL DEFAULT '8',
  `disband_time` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name_unique` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;