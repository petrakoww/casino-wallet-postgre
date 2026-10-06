export interface Wallet {
  playerId: string;
  username: string;
  realBalance: number;
  bonusBalance: number;
  totalBalance: number;
}

export interface BonusProgress {
  active: boolean;
  bonusId: string | null;

  initialAmount: number;

  wageringRequired: number;
  wageringCompleted: number;
  wageringRemaining: number;

  progressPercent: number;

  expiresAt: string | null;
  status: 'ACTIVE' | 'COMPLETED' | 'EXPIRED' | null;
}

export interface Deposit {
  id: string;
  playerId: string;
  amount: number;
  status: 'PENDING' | 'COMPLETED';
  providerReference: string | null;
  createdAt: string;
  completedAt: string | null;
}

export interface Bet {
  id: string;
  playerId: string;

  stake: number;
  realStake: number;
  bonusStake: number;

  status: 'OPEN' | 'SETTLED';

  createdAt: string;
}

export interface SettledBet {
  id: string;

  stake: number;
  realStake: number;
  bonusStake: number;

  winAmount: number;
  realWin: number;
  bonusWin: number;

  status: 'SETTLED';
  settledAt: string | null;
}

export interface Player {
  id: string;
  username: string;
  realBalance: number;
  bonusBalance: number;
}
