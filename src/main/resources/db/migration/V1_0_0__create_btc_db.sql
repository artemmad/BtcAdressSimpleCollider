CREATE TABLE IF NOT EXISTS btc_f_address (

    id TEXT  NOT NULL PRIMARY KEY,
    private_key TEXT,
    created_date TIMESTAMP WITH TIME ZONE,
    modified_date TIMESTAMP WITH TIME ZONE
);