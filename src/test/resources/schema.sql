CREATE SCHEMA `r2dbc_demo`;
USE `r2dbc_demo`;

CREATE TABLE transaction (
	t_id integer PRIMARY KEY auto_increment,
	t_period integer not null,
	t_sequence integer not null,
	t_created timestamp not null default CURRENT_TIMESTAMP
);

ALTER TABLE transaction 
  ADD CONSTRAINT t_uniq_period_sequence UNIQUE(t_period, t_sequence);

CREATE TABLE aggregate (
	a_id integer PRIMARY KEY auto_increment,
	a_period integer not null,
	a_count integer not null default 1,
	a_created timestamp not null default CURRENT_TIMESTAMP,
	a_updated timestamp on update CURRENT_TIMESTAMP,
	a_version integer not null
);

ALTER TABLE aggregate
  ADD CONSTRAINT a_uniq_period UNIQUE(a_period);
