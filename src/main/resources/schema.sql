DROP TABLE web_site;
DROP TABLE page;

CREATE TABLE IF NOT EXISTS web_site
(
    id UUID PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL,
    theme VARCHAR
);

CREATE TABLE IF NOT EXISTS page
(
    id UUID PRIMARY KEY,
    path TEXT NOT NULL,
    code INT NOT NULL,
    content TEXT NOT NULL,
    web_site_id UUID NOT NULL
);