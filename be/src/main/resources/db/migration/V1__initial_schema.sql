CREATE TABLE players (
     id UUID PRIMARY KEY,
     username VARCHAR(100) NOT NULL UNIQUE,

     real_balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
     bonus_balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,

     version BIGINT NOT NULL DEFAULT 0,

     created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE deposits (
      id UUID PRIMARY KEY,
      player_id UUID NOT NULL REFERENCES players(id),

      amount NUMERIC(19, 2) NOT NULL,
      status VARCHAR(30) NOT NULL,

      provider_reference VARCHAR(255),

      created_at TIMESTAMP WITH TIME ZONE NOT NULL,
      completed_at TIMESTAMP WITH TIME ZONE,

      CONSTRAINT chk_deposit_amount_positive
          CHECK (amount > 0),

      CONSTRAINT uq_deposit_provider_reference
          UNIQUE (provider_reference)
);

CREATE INDEX idx_deposits_player_id
    ON deposits(player_id);

CREATE TABLE bonuses (
     id UUID PRIMARY KEY,
     player_id UUID NOT NULL REFERENCES players(id),
     deposit_id UUID NOT NULL REFERENCES deposits(id),

     initial_amount NUMERIC(19, 2) NOT NULL,
     wagering_required NUMERIC(19, 2) NOT NULL,
     wagering_completed NUMERIC(19, 2) NOT NULL DEFAULT 0.00,

     status VARCHAR(30) NOT NULL,

     granted_at TIMESTAMP WITH TIME ZONE NOT NULL,
     expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
     completed_at TIMESTAMP WITH TIME ZONE,

     CONSTRAINT uq_bonus_deposit
         UNIQUE (deposit_id),

     CONSTRAINT chk_bonus_initial_amount
         CHECK (initial_amount >= 0),

     CONSTRAINT chk_bonus_wagering_required
         CHECK (wagering_required >= 0),

     CONSTRAINT chk_bonus_wagering_completed
         CHECK (wagering_completed >= 0)
);

CREATE INDEX idx_bonuses_player_id
    ON bonuses(player_id);

CREATE TABLE bets (
      id UUID PRIMARY KEY,
      player_id UUID NOT NULL REFERENCES players(id),

      stake NUMERIC(19, 2) NOT NULL,
      real_stake NUMERIC(19, 2) NOT NULL,
      bonus_stake NUMERIC(19, 2) NOT NULL,

      win_amount NUMERIC(19, 2),

      real_win NUMERIC(19, 2),
      bonus_win NUMERIC(19, 2),

      status VARCHAR(30) NOT NULL,

      created_at TIMESTAMP WITH TIME ZONE NOT NULL,
      settled_at TIMESTAMP WITH TIME ZONE,

      CONSTRAINT chk_bet_stake_positive
          CHECK (stake > 0),

      CONSTRAINT chk_bet_real_stake
          CHECK (real_stake >= 0),

      CONSTRAINT chk_bet_bonus_stake
          CHECK (bonus_stake >= 0)
);

CREATE INDEX idx_bets_player_id
    ON bets(player_id);

CREATE TABLE ledger_entries (
        id UUID PRIMARY KEY,
        player_id UUID NOT NULL REFERENCES players(id),

        type VARCHAR(50) NOT NULL,

        real_change NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
        bonus_change NUMERIC(19, 2) NOT NULL DEFAULT 0.00,

        real_balance_after NUMERIC(19, 2) NOT NULL,
        bonus_balance_after NUMERIC(19, 2) NOT NULL,

        reference_type VARCHAR(50),
        reference_id UUID,

        description VARCHAR(500),

        created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_ledger_entries_player_created
    ON ledger_entries(player_id, created_at DESC);

CREATE INDEX idx_ledger_entries_reference
    ON ledger_entries(reference_type, reference_id);