INSERT INTO authority(authority_name, created_date_time, modified_date_time) values ('ROLE_DISABLED_USER', now(), now());
INSERT INTO authority(authority_name, created_date_time, modified_date_time) values ('ROLE_USER', now(), now());
INSERT INTO authority(authority_name, created_date_time, modified_date_time) values ('ROLE_WITHDRAW_USER', now(), now());

INSERT INTO category(CATEGORY_ID, CATEGORY_NAME) values (1, '채소');
INSERT INTO category(CATEGORY_ID, CATEGORY_NAME) values (2, '과일');
INSERT INTO category(CATEGORY_ID, CATEGORY_NAME) values (3, '간편식');
INSERT INTO category(CATEGORY_ID, CATEGORY_NAME) values (4, '정육');
INSERT INTO category(CATEGORY_ID, CATEGORY_NAME) values (5, '수산물');
INSERT INTO category(CATEGORY_ID, CATEGORY_NAME) values (6, '기타');
