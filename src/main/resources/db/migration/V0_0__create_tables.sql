CREATE TABLE IF NOT EXISTS container
(
    name  varchar(50)   NOT NULL PRIMARY KEY,
    price DECIMAL(4, 2) NOT NULL DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS menu_item
(
    name               varchar(200)  NOT NULL PRIMARY KEY,
    serving_size       varchar(10)   NOT NULL                                               DEFAULT 'g',
    type               ENUM ('DAILY_MENU','SOUP','MAIN_COURSE','GARNISH','DESSERT','EXTRA') DEFAULT NULL,
    photo_url          varchar(200)                                                         DEFAULT NULL,
    normal_price       DECIMAL(4, 2) NOT NULL                                               DEFAULT 0,
    discounted_price   DECIMAL(4, 2) NOT NULL                                               DEFAULT 0,
    recurring_days     INTEGER       NOT NULL                                               DEFAULT 0,
    first_possible_day date          NOT NULL,
    last_possible_day  date          NOT NULL
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

CREATE TABLE IF NOT EXISTS user
(
    id           varchar(36) NOT NULL PRIMARY KEY,
    phone_number varchar(10) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS address
(
    id         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    value      varchar(200) NOT NULL,
    user_id    varchar(36)  NOT NULL,
    is_current boolean      NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT unique_current_address UNIQUE (user_id, is_current)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;




