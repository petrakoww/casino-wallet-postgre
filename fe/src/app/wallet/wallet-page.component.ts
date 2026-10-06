import {
  Component,
  OnInit,
  inject
} from '@angular/core';
import {
  CommonModule
} from '@angular/common';
import {
  FormsModule
} from '@angular/forms';
import {
  forkJoin
} from 'rxjs';
import {
  WalletApiService
} from '../core/api/wallet-api.service';
import {
  BonusProgress,
  Wallet
} from '../core/models/wallet.models';
import {
  LedgerPage
} from '../core/models/ledger.models';
import {
  Language,
  TranslationKey,
  TranslationService
} from '../i18n/translation.service';
import {
  LedgerEntryType
} from '../core/models/ledger.models';
import {
  HttpErrorResponse
} from '@angular/common/http';
import {
  ApiError
} from '../core/models/api-error.model';
import {
  Router
} from '@angular/router';
import {
  PlayerSessionService
} from '../core/session/player-session.service';

@Component({
  selector: 'app-wallet-page',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl:
    './wallet-page.component.html',

  styleUrl:
    './wallet-page.component.scss'
})
export class WalletPageComponent
  implements OnInit {

  private readonly router =
    inject(Router);

  readonly session =
    inject(PlayerSessionService);

  private readonly api =
    inject(WalletApiService);

  readonly i18n =
    inject(TranslationService);

  private get playerId(): string {

    const player =
      this.session.player();

    if (!player) {
      this.router.navigate([
        '/login'
      ]);

      throw new Error(
        'No active player session'
      );
    }

    return player.id;
  }

  wallet: Wallet | null =
    null;

  bonus: BonusProgress | null =
    null;

  ledger: LedgerPage | null =
    null;

  depositAmount = 20;

  stake = 5;

  ledgerPage = 0;

  ledgerSize = 5;

  loading = false;

  errorMessage = '';

  successMessage = '';

  lastRoundWin:
    number | null = null;

  actionLoading = false;

  ngOnInit(): void {

    if (!this.session.player()) {

      this.router.navigate([
        '/login'
      ]);

      return;
    }

    this.refresh();
  }

  logout(): void {
    this.session.logout();

    this.router.navigate([
      '/login'
    ]);
  }

  t(
    key: TranslationKey
  ): string {

    return this.i18n.translate(key);
  }

  ledgerTypeLabel(
    type: LedgerEntryType
  ): string {

    switch (type) {

      case 'DEPOSIT':
        return this.t('ledgerDeposit');

      case 'BONUS_GRANTED':
        return this.t('ledgerBonusGranted');

      case 'BET_STAKE':
        return this.t('ledgerBetStake');

      case 'BET_WIN':
        return this.t('ledgerBetWin');

      case 'BONUS_CONVERTED':
        return this.t('ledgerBonusConverted');

      case 'BONUS_FORFEITED':
        return this.t('ledgerBonusForfeited');
    }
  }

  changeLanguage(
    language: Language
  ): void {

    this.i18n.setLanguage(language);
  }

  refresh(): void {

    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      wallet:
        this.api.getWallet(
          this.playerId
        ),

      bonus:
        this.api.getBonus(
          this.playerId
        ),

      ledger:
        this.api.getLedger(
          this.playerId,
          this.ledgerPage,
          this.ledgerSize
        )
    }).subscribe({

      next: result => {

        this.wallet =
          result.wallet;

        this.bonus =
          result.bonus;

        this.ledger =
          result.ledger;

        this.loading = false;
      },

      error: error => {

        console.error(error);

        this.errorMessage =
          error?.error?.message ??
          this.t('error');

        this.loading = false;
      }
    });
  }

  createDeposit(): void {

    this.clearMessages();
    this.actionLoading = true;

    this.api.createDeposit(
      this.playerId,
      this.depositAmount
    ).subscribe({

      next: () => {

        this.successMessage =
          this.t('depositCreated');

        this.actionLoading = false;

        this.refresh();
      },

      error: error => {

        this.actionLoading = false;

        this.handleError(error);
      }
    });
  }

  placeBet(): void {

    this.clearMessages();
    this.lastRoundWin = null;
    this.actionLoading = true;

    this.api.placeBet(
      this.playerId,
      this.stake
    ).subscribe({

      next: bet => {

        // Demo game engine. 50% chance:
        // loss - 0
        // win  - stake*2
        const won =
          Math.random() >= 0.5;

        const winAmount =
          won
            ? Number(
              (bet.stake * 2)
                .toFixed(2)
            )
            : 0;

        this.api.settleBet(
          this.playerId,
          bet.id,
          winAmount
        ).subscribe({

          next: () => {

            this.lastRoundWin =
              winAmount;

            this.successMessage =
              winAmount > 0
                ? `${this.t('roundWin')}: €${winAmount.toFixed(2)}`
                : this.t('roundLoss');

            this.actionLoading = false;

            this.refresh();
          },

          error: error => {
            this.actionLoading = false;
            this.handleError(error);
          }
        });
      },

      error: error => {
        this.actionLoading = false;
        this.handleError(error);
      }
    });
  }

  previousPage(): void {

    if (this.ledgerPage === 0) {
      return;
    }

    this.ledgerPage--;

    this.refresh();
  }

  nextPage(): void {

    if (this.ledger?.last) {
      return;
    }

    this.ledgerPage++;

    this.refresh();
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  private handleError(
    error: HttpErrorResponse
  ): void {

    console.error(error);

    const apiError =
      error.error as ApiError | undefined;

    this.errorMessage =
      apiError?.message ??
      this.t('error');
  }
}
