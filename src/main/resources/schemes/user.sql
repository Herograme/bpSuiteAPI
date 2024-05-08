CREATE TABLE "users" (
  "user_id" serial,
  "user_name" varchar(255) NOT NULL,
  "password" varchar(255) NOT NULL,
  "user_role" int2 NOT NULL,
  "salt" varchar(255) NOT NULL,
  PRIMARY KEY ("user_id")
);