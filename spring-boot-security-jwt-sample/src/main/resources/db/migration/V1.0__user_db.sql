CREATE TABLE T_USER (
  ID IDENTITY NOT NULL PRIMARY KEY,
  USER_NAME VARCHAR NOT NULL,
  EMAIL VARCHAR NOT NULL,
  PASSWORD VARCHAR NOT NULL,
  ROLES VARCHAR NOT NULL,
  AUTHORITIES VARCHAR NOT NULL
);

INSERT INTO T_USER (USER_NAME, EMAIL, PASSWORD, ROLES, AUTHORITIES) VALUES ('admin', 'admin@admin.com','$2a$12$Nj6eI.GmEQQWu0CYGLL.XOe5n5vnS700FztswQqpSb2VJzVvXzuj6','admin,user','ROLE_admin, authority-1,authority-2')
