CREATE TABLE FOO (
  ID SERIAL NOT NULL PRIMARY KEY,
  NAME VARCHAR NOT NULL,
  DESCRIPTION VARCHAR NOT NULL,
  CREATION_DATE TIMESTAMP DEFAULT NOW()
);
