export const translations = {

  en: {
    title: 'Casino Wallet',

    language: 'Language',

    realBalance: 'Real balance',
    bonusBalance: 'Bonus balance',
    totalBalance: 'Total balance',

    bonusProgress: 'Bonus progress',
    wageringCompleted: 'Wagered',
    wageringRemaining: 'Remaining',
    expiresAt: 'Expires',
    noActiveBonus: 'No active bonus',

    deposit: 'Deposit',
    depositAmount: 'Deposit amount',
    createDeposit: 'Create deposit',

    playRound: 'Play a round',
    stake: 'Stake',
    placeBet: 'Place bet',

    settlement: 'Settle round',
    winAmount: 'Win amount',
    settle: 'Settle',

    ledger: 'Ledger',

    type: 'Type',
    realChange: 'Real change',
    bonusChange: 'Bonus change',
    realAfter: 'Real after',
    bonusAfter: 'Bonus after',
    date: 'Date',

    previous: 'Previous',
    next: 'Next',

    loading: 'Loading...',
    error: 'Something went wrong',

    depositCreated:
      'Deposit created. Waiting for payment-provider callback.',

    betCreated:
      'Bet placed successfully.',

    betSettled:
      'Round settled successfully.',

    ledgerDeposit: 'Deposit',
    ledgerBonusGranted: 'Bonus granted',
    ledgerBetStake: 'Bet stake',
    ledgerBetWin: 'Bet win',
    ledgerBonusConverted: 'Bonus converted',
    ledgerBonusForfeited: 'Bonus forfeited',

    roundResult: 'Round result',
    roundWin: 'You won',
    roundLoss: 'No win this round',

    login: 'Continue',
    username: 'Player name',
    loginDescription:
      'Enter a player name to continue.',
    loginHint:
      'If the player does not exist, a new one will be created automatically.',
    logout: 'Log out',
  },

  bg: {
    title: 'Casino Wallet',

    language: 'Език',

    realBalance: 'Реален баланс',
    bonusBalance: 'Бонус баланс',
    totalBalance: 'Общ баланс',

    bonusProgress: 'Бонус прогрес',
    wageringCompleted: 'Превъртяно',
    wageringRemaining: 'Остава',
    expiresAt: 'Изтича',
    noActiveBonus: 'Няма активен бонус',

    deposit: 'Депозит',
    depositAmount: 'Сума за депозит',
    createDeposit: 'Създай депозит',

    playRound: 'Играй рунд',
    stake: 'Залог',
    placeBet: 'Постави залог',

    settlement: 'Приключи рунда',
    winAmount: 'Печалба',
    settle: 'Приключи',

    ledger: 'История на транзакциите',

    type: 'Тип',
    realChange: 'Промяна real',
    bonusChange: 'Промяна bonus',
    realAfter: 'Real след',
    bonusAfter: 'Bonus след',
    date: 'Дата',

    previous: 'Назад',
    next: 'Напред',

    loading: 'Зареждане...',
    error: 'Възникна грешка',

    depositCreated:
      'Депозитът е създаден и чака callback от payment provider.',

    betCreated:
      'Залогът е поставен успешно.',

    betSettled:
      'Рундът е приключен успешно.',

    ledgerDeposit: 'Депозит',
    ledgerBonusGranted: 'Начислен бонус',
    ledgerBetStake: 'Поставен залог',
    ledgerBetWin: 'Печалба от залог',
    ledgerBonusConverted: 'Бонусът е конвертиран',
    ledgerBonusForfeited: 'Бонусът е отнет',

    roundResult: 'Резултат от рунда',
    roundWin: 'Спечели',
    roundLoss: 'Този рунд няма печалба',

    login: 'Продължи',
    username: 'Име на играч',
    loginDescription:
      'Въведи име на играч, за да продължиш.',
    loginHint:
      'Ако такъв играч не съществува, ще бъде създаден автоматично.',
    logout: 'Изход',
  }

} as const;
