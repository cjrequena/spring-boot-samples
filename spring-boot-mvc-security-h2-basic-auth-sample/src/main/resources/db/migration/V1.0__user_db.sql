CREATE TABLE T_USER (
  ID IDENTITY NOT NULL PRIMARY KEY,
  USER_NAME VARCHAR NOT NULL,
  PASSWORD VARCHAR NOT NULL,
  ROLES VARCHAR NOT NULL
);

INSERT INTO T_USER (USER_NAME, PASSWORD, ROLES) VALUES ('admin', '$2a$12$8844i5RkN1SCkTIvVrHx5O5Oc2q3moz/FxyDgV/CmCR0QKdwer3Ze','ADMIN,USER') --admin, admin
