CREATE TABLE colours (
  uuid BINARY(16) NOT NULL,
  colour varchar(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (uuid)
);

CREATE TABLE nicks (
  uuid BINARY(16) NOT NULL,
  nick varchar(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (uuid)
);
