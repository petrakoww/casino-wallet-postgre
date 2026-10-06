export type LedgerEntryType =
  | 'DEPOSIT'
  | 'BONUS_GRANTED'
  | 'BET_STAKE'
  | 'BET_WIN'
  | 'BONUS_CONVERTED'
  | 'BONUS_FORFEITED';

export interface LedgerEntry {
  id: string;

  type: LedgerEntryType;

  realChange: number;
  bonusChange: number;

  realBalanceAfter: number;
  bonusBalanceAfter: number;

  referenceType: string | null;
  referenceId: string | null;

  description: string | null;

  createdAt: string;
}

export interface LedgerPage {
  content: LedgerEntry[];

  page: number;
  size: number;

  totalElements: number;
  totalPages: number;

  first: boolean;
  last: boolean;
}
