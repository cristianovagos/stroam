DROP TABLE IF EXISTS CHECKOUT;
DROP TABLE IF EXISTS MERCHANT;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS CLIENT;
DROP TABLE IF EXISTS CREDIT_CARD;
DROP TABLE IF EXISTS BILLING_ADDRESS;
DROP TABLE IF EXISTS ITEM;

CREATE TABLE USER(
  id        TEXT PRIMARY KEY,
  email     TEXT NOT NULL,
  password  TEXT NOT NULL
);

CREATE TABLE MERCHANT(
  id        TEXT PRIMARY KEY,
  name      TEXT NOT NULL,
  domain    TEXT,
  FOREIGN KEY (id) REFERENCES USER(id)
);

CREATE TABLE CLIENT(
  id        TEXT PRIMARY KEY,
  name      TEXT NOT NULL,
  nif       INT,
  FOREIGN KEY (id) REFERENCES USER(id)
);

CREATE TABLE CREDIT_CARD(
  cc_number   INT NOT NULL,
  csv         INT NOT NULL,
  expiration  TEXT NOT NULL,
  owner_name  TEXT NOT NULL,
  user_id     TEXT NOT NULL,
  PRIMARY KEY (cc_number, user_id),
  FOREIGN KEY (user_id) REFERENCES USER(id)
);

CREATE TABLE BILLING_ADDRESS(
  name          TEXT NOT NULL,
  street_name   TEXT NOT NULL,
  street_number INT NOT NULL,
  state_district TEXT NOT NULL,
  country        TEXT NOT NULL,
  user_id        TEXT PRIMARY KEY,
  FOREIGN KEY (user_id) REFERENCES USER(id)
);


CREATE TABLE CHECKOUT (
  id          TEXT PRIMARY KEY,
  amount      REAL NOT NULL,
  return_url  TEXT NOT NULL,
  cancel_url  TEXT NOT NULL,
  merchant    TEXT NOT NULL,
  currency    TEXT DEFAULT "EUR",
  paid_with   INT,
  paid_by     TEXT,
  status      TEXT DEFAULT "CREATED",
  FOREIGN KEY (merchant) REFERENCES MERCHANT(id),
  FOREIGN KEY (paid_with) REFERENCES CREDIT_CARD(cc_number),
  FOREIGN KEY (paid_by) REFERENCES CLIENT(id)
);

CREATE TABLE ITEM (
  name        TEXT NOT NULL,
  price       REAL NOT NULL,
  quantity    INT DEFAULT 1,
  url         TEXT,
  checkout    TEXT NOT NULL,
  FOREIGN KEY (checkout) REFERENCES CHECKOUT(id)
);

INSERT INTO USER (id, email, password) VALUES ("tokensample123", "geral@stroam.net", "password123");
INSERT INTO MERCHANT (id, name) VALUES ("tokensample123", "Stroam");
INSERT INTO CLIENT (id, name) VALUES ("tokensample123", "Cristiano Vagos");