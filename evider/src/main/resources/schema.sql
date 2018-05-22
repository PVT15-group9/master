CREATE TABLE 'sensor'(
	'sensor_id' BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	'creation_time' TIMESTAMP(0),
	'event_start_time' TIMESTAMP(0),
	'event_end_time' TIMESTAMP(0),
	'door_open_time' TIMESTAMP(0),
	'sensor_value' int(10),
	PRIMARY KEY ('sensor_id')
)

CREATE TABLE 'simulated_value'(
	'id' BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	'sensor_id' BIGINT(20) NOT NULL,
	'simulated_value' int(10),
	PRIMARY KEY ('id'),
	KEY 'sensor_id' ('sensor_id'),
	ALTER TABLE 'simulated_value' ADD CONSTRAINT 'simulated_value_fk_1' FOREIGN KEY ('sensor_id') REFERENCES ('sensor')
)