import { Injectable, signal } from '@angular/core';

import {
  translations
} from './translations';

export type Language =
  keyof typeof translations;

export type TranslationKey =
  keyof typeof translations.en;

@Injectable({
  providedIn: 'root'
})
export class TranslationService {
  readonly language =
    signal<Language>('en');

  setLanguage(
    language: Language
  ): void {

    this.language.set(language);
  }

  translate(
    key: TranslationKey
  ): string {

    return translations[
      this.language()
      ][key];
  }
}
