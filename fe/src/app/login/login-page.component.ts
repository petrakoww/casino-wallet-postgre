import {
  Component,
  inject
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  Router
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  WalletApiService
} from '../core/api/wallet-api.service';

import {
  PlayerSessionService
} from '../core/session/player-session.service';

import {
  TranslationKey,
  TranslationService
} from '../i18n/translation.service';

@Component({
  selector: 'app-login-page',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl:
    './login-page.component.html',

  styleUrl:
    './login-page.component.scss'
})
export class LoginPageComponent {

  private readonly api =
    inject(WalletApiService);

  private readonly router =
    inject(Router);

  private readonly session =
    inject(PlayerSessionService);

  readonly i18n =
    inject(TranslationService);

  username = '';

  loading = false;

  errorMessage = '';

  t(
    key: TranslationKey
  ): string {
    return this.i18n.translate(key);
  }

  login(): void {

    const username =
      this.username.trim();

    if (!username) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.api.login(
      username
    ).subscribe({

      next: player => {

        this.session.login(
          player
        );

        this.router.navigate([
          '/wallet'
        ]);
      },

      error: (
        error: HttpErrorResponse
      ) => {

        this.loading = false;

        this.errorMessage =
          error?.error?.message ??
          this.t('error');
      }
    });
  }
}
