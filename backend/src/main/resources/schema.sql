-- Esquema de SplitBill. Columnas con prefijo de 3 letras y borrado logico con status 0/1.
-- Los montos son pesos enteros (BIGINT), nunca decimales con coma flotante.

CREATE TABLE IF NOT EXISTS users (
    use_id       BIGSERIAL PRIMARY KEY,
    use_name     VARCHAR(80)  NOT NULL,
    use_email    VARCHAR(120) NOT NULL UNIQUE,
    use_password VARCHAR(100) NOT NULL,
    use_status   SMALLINT     NOT NULL DEFAULT 1 CHECK (use_status IN (0, 1))
);

CREATE TABLE IF NOT EXISTS groups (
    grp_id          BIGSERIAL PRIMARY KEY,
    grp_name        VARCHAR(80)  NOT NULL,
    grp_description VARCHAR(200),
    grp_owner_id    BIGINT       NOT NULL REFERENCES users (use_id),
    grp_status      SMALLINT     NOT NULL DEFAULT 1 CHECK (grp_status IN (0, 1))
);

CREATE TABLE IF NOT EXISTS members (
    mem_id       BIGSERIAL PRIMARY KEY,
    mem_group_id BIGINT      NOT NULL REFERENCES groups (grp_id),
    mem_name     VARCHAR(80) NOT NULL,
    mem_phone    VARCHAR(30),
    mem_status   SMALLINT    NOT NULL DEFAULT 1 CHECK (mem_status IN (0, 1))
);

CREATE TABLE IF NOT EXISTS expenses (
    exp_id          BIGSERIAL PRIMARY KEY,
    exp_group_id    BIGINT       NOT NULL REFERENCES groups (grp_id),
    exp_payer_id    BIGINT       NOT NULL REFERENCES members (mem_id),
    exp_description VARCHAR(120) NOT NULL,
    exp_amount      BIGINT       NOT NULL CHECK (exp_amount > 0),
    exp_date        DATE         NOT NULL DEFAULT CURRENT_DATE,
    exp_status      SMALLINT     NOT NULL DEFAULT 1 CHECK (exp_status IN (0, 1))
);
