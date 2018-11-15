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
  logo      TEXT,
  FOREIGN KEY (id) REFERENCES USER(id)
);

CREATE TABLE CLIENT(
  id        TEXT PRIMARY KEY,
  name      TEXT NOT NULL,
  nif       INT,
  FOREIGN KEY (id) REFERENCES USER(id)
);

CREATE TABLE CREDIT_CARD(
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  cc_number   INT NOT NULL,
  csv         INT NOT NULL,
  expiration  TEXT NOT NULL,
  owner_name  TEXT NOT NULL,
  user_id     TEXT NOT NULL,
  visibility  INT CHECK( visibility IN (0,1) ) NOT NULL DEFAULT 1,
  FOREIGN KEY (user_id) REFERENCES USER(id)
);

CREATE TABLE BILLING_ADDRESS(
  id             INTEGER PRIMARY KEY AUTOINCREMENT,
  first_name     TEXT NOT NULL,
  last_name      TEXT NOT NULL,
  country        TEXT NOT NULL,
  city           TEXT NOT NULL,
  address        TEXT NOT NULL,
  post_code      TEXT NOT NULL,
  phone          INT NOT NULL,
  user_id        TEXT NOT NULL,
  visibility     INT CHECK( visibility IN (0,1) ) NOT NULL DEFAULT 1,
  FOREIGN KEY (user_id) REFERENCES USER(id)
);


CREATE TABLE CHECKOUT (
  id          TEXT PRIMARY KEY,
  amount      REAL NOT NULL,
  return_url  TEXT NOT NULL,
  cancel_url  TEXT NOT NULL,
  merchant    TEXT NOT NULL,
  currency    TEXT DEFAULT "EUR" NOT NULL,
  status      TEXT CHECK( status IN ('CREATED','READY','PAID') ) NOT NULL DEFAULT 'CREATED',
  paid_with   INT,
  paid_by     TEXT,
  billing_address INT,
  FOREIGN KEY (merchant) REFERENCES MERCHANT(id),
  FOREIGN KEY (paid_with) REFERENCES CREDIT_CARD(cc_number),
  FOREIGN KEY (paid_by) REFERENCES CLIENT(id),
  FOREIGN KEY (billing_address) REFERENCES BILLING_ADDRESS(id)
);

CREATE TABLE ITEM (
  name        TEXT NOT NULL,
  price       REAL NOT NULL,
  quantity    INT DEFAULT 1,
  url         TEXT,
  checkout    TEXT NOT NULL,
  image       TEXT,
  PRIMARY KEY (name, checkout),
  FOREIGN KEY (checkout) REFERENCES CHECKOUT(id) ON DELETE CASCADE
);

INSERT INTO USER (id, email, password) VALUES ("tokensample123", "geral@stroam.net", "password123");
INSERT INTO MERCHANT (id, name) VALUES ("tokensample123", "Stroam");
INSERT INTO CLIENT (id, name) VALUES ("tokensample123", "Cristiano Vagos");
