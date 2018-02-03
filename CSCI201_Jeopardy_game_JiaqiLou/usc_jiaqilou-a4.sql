DROP DATABASE if exists UserInfo;
CREATE DATABASE UserInfo;
USE UserInfo;
CREATE TABLE UserInfo (
	username varchar(50) not null,
	password varchar(50) not null
);


