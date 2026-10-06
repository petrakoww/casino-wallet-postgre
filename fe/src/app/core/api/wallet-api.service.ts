import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Player,
  Bet,
  BonusProgress,
  Deposit,
  SettledBet,
  Wallet
} from '../models/wallet.models';

import {
  LedgerPage
} from '../models/ledger.models';

@Injectable({
  providedIn: 'root'
})
export class WalletApiService {

  private readonly http = inject(HttpClient);

  private readonly baseUrl =
    '/api';

  getWallet(
    playerId: string
  ): Observable<Wallet> {

    return this.http.get<Wallet>(
      `${this.baseUrl}/players/${playerId}/wallet`
    );
  }

  getBonus(
    playerId: string
  ): Observable<BonusProgress> {

    return this.http.get<BonusProgress>(
      `${this.baseUrl}/players/${playerId}/bonus`
    );
  }

  getLedger(
    playerId: string,
    page: number,
    size: number
  ): Observable<LedgerPage> {

    return this.http.get<LedgerPage>(
      `${this.baseUrl}/players/${playerId}/ledger`,
      {
        params: {
          page,
          size
        }
      }
    );
  }

  createDeposit(
    playerId: string,
    amount: number
  ): Observable<Deposit> {

    return this.http.post<Deposit>(
      `${this.baseUrl}/players/${playerId}/deposits`,
      {
        amount
      }
    );
  }

  placeBet(
    playerId: string,
    stake: number
  ): Observable<Bet> {

    return this.http.post<Bet>(
      `${this.baseUrl}/players/${playerId}/bets`,
      {
        stake
      }
    );
  }

  settleBet(
    playerId: string,
    betId: string,
    winAmount: number
  ): Observable<SettledBet> {

    return this.http.post<SettledBet>(
      `${this.baseUrl}/players/${playerId}/bets/${betId}/settle`,
      {
        winAmount
      }
    );
  }

  login(
    username: string
  ): Observable<Player> {

    return this.http.post<Player>(
      `${this.baseUrl}/players/login`,
      {
        username
      }
    );
  }
}
