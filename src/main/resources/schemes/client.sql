CREATE TABLE "client" (
  "client_id" serial,
  "user_id" serial,
  "name" varchar(255) NOT NULL,
  "doc_number" varchar(18) NOT NULL,
  "fone_number" int4,
  "email" varchar(255),
  PRIMARY KEY ("client_id")
);

ALTER TABLE "client" ADD CONSTRAINT "user_id" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");