CREATE TABLE IF NOT EXISTS container
(
    name  varchar(50)   NOT NULL PRIMARY KEY,
    price DECIMAL(4, 2) NOT NULL DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS menu_item
(
    name              varchar(200)  NOT NULL PRIMARY KEY,
    serving_size      varchar(10)   NOT NULL DEFAULT 'g',
    normal_price      DECIMAL(4, 2) NOT NULL DEFAULT 0,
    discounted_price  DECIMAL(4, 2) NOT NULL DEFAULT 0,
    recurring_days    INTEGER       NOT NULL DEFAULT 0,
    first_posible_day date          NOT NULL,
    last_posible_day  date          NOT NULL,
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS daily_menu
(
    id               int          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    description      varchar(256) NOT NULL,
    recurring_days   INTEGER      NOT NULL DEFAULT 0,
    last_posible_day date         NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS daily_menu_containers
(
    daily_menu_id int         NOT NULL,
    container_id  varchar(50) NOT NULL,
    quantity      int         NOT NULL DEFAULT 1,
    PRIMARY KEY (daily_menu_id, container_id),
    FOREIGN KEY (daily_menu_id) REFERENCES daily_menu (id),
    FOREIGN KEY (container_id) REFERENCES container (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS menu_item_containers
(
    menu_item_id varchar(200) NOT NULL,
    container_id varchar(50)  NOT NULL,
    quantity     int          NOT NULL DEFAULT 1,
    PRIMARY KEY (menu_item_id, container_id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_item (name),
    FOREIGN KEY (container_id) REFERENCES container (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS cantina_order
(
    id          INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at  datetime                DEFAULT NULL,
    status      varchar(20)    NOT NULL DEFAULT 'CREATED'
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS order_item
(
    id               INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cantina_order_id INT            NOT NULL,
    menu_item_name   varchar(50)    NOT NULL,
    quantity         int            NOT NULL DEFAULT 1,
    price            DECIMAL(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (cantina_order_id) REFERENCES cantina_order (id),
    FOREIGN KEY (menu_item_name) REFERENCES menu_item (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS daily_menu_order_item
(
    id               INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cantina_order_id INT            NOT NULL,
    daily_menu_id    INT            NOT NULL,
    quantity         int            NOT NULL DEFAULT 1,
    price            DECIMAL(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (cantina_order_id) REFERENCES cantina_order (id),
    FOREIGN KEY (daily_menu_id) REFERENCES daily_menu (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;




