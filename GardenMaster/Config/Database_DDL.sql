-- keep a script for DB setup:
create database gardenmaster;

-- setup the user table:
drop table user_sec;
create table SEC_USERS ( 
username VARCHAR(255), 
password VARCHAR(255),
first_name VARCHAR(255), 
last_name VARCHAR(255), 
email_addr VARCHAR(255),
account_type INT,
account_created DATE, 
last_login DATE );


insert into SEC_USERS 
	values ('mkreid', 
			SHA1(CONCAT('Yukon123','ILUV2PARTY!')), 
			'Malcolm', 
			'Reid', 
			'Malcolm.Reid@me.com', 
			1, 
			SYSDATE(), 
			null);
			
-- setup the user table:
drop table sec_pw_reset;
create table sec_pw_reset ( 
token VARCHAR(36), 
email_addr VARCHAR(255),
expires DATE );