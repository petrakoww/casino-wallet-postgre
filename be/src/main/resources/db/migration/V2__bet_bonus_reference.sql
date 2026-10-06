ALTER TABLE bets
    ADD COLUMN bonus_id UUID NULL;

ALTER TABLE bets
    ADD CONSTRAINT fk_bets_bonus
        FOREIGN KEY (bonus_id)
            REFERENCES bonuses(id);

CREATE INDEX idx_bets_bonus_id
    ON bets(bonus_id);