import {
  Injectable,
  signal
} from '@angular/core';

import {
  Player
} from '../models/wallet.models';

@Injectable({
  providedIn: 'root'
})
export class PlayerSessionService {

  private readonly storageKey =
    'casino-wallet-player';

  readonly player =
    signal<Player | null>(
      this.readFromStorage()
    );

  login(
    player: Player
  ): void {

    this.player.set(player);

    // save in local storage
    localStorage.setItem(
      this.storageKey,
      JSON.stringify(player)
    );
  }

  logout(): void {

    this.player.set(null);

    localStorage.removeItem(
      this.storageKey
    );
  }

  private readFromStorage():
    Player | null {

    const stored =
      localStorage.getItem(
        this.storageKey
      );

    if (!stored) {
      return null;
    }

    try {
      return JSON.parse(stored) as Player;
    } catch {
      localStorage.removeItem(
        this.storageKey
      );

      return null;
    }
  }
}
