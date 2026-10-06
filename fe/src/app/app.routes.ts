import {
  Routes
} from '@angular/router';

import {
  LoginPageComponent
} from './login/login-page.component';

import {
  WalletPageComponent
} from './wallet/wallet-page.component';

export const routes: Routes = [

  {
    path: 'login',
    component: LoginPageComponent
  },

  {
    path: 'wallet',
    component: WalletPageComponent
  },

  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'login'
  }

];
