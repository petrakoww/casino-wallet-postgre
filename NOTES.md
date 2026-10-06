# Notes and Assumptions

This file documents the main assumptions and design decisions made while implementing the casino wallet exercise.

## 1. First Deposit Bonus Interpretation

The requirement says:

> The first completed deposit of 20.00 or more grants a bonus.

This implementation interprets that as:

- only the player's **first completed deposit** is eligible
- if that first completed deposit is below 20.00, no welcome bonus is granted
- later deposits do not become eligible

Example:

```text
First deposit: 10 -> no bonus
Second deposit: 50 -> still no bonus
```

## 2. Wagering Progress

While a bonus is active, the **entire valid stake** counts toward wagering progress, not only the portion taken from the bonus balance.

Example:

```text
Stake: 5
Real stake: 3
Bonus stake: 2

Wagering progress increase: 5
```

## 3. Bonus Stake Limit

While a bonus is active:

```text
5.00 -> allowed
5.01 -> rejected
```

The limit applies to the total stake, not only the bonus-funded portion.

## 4. Win Split

The win is split using the same ratio as the original stake.

To avoid losing or creating one cent because of independent rounding:

- `realWin` is calculated from the real stake ratio and rounded to 2 decimals
- `bonusWin` is calculated as:

```text
winAmount - realWin
```

Therefore:

```text
realWin + bonusWin = winAmount
```

is always preserved.

Example rounding case:

```text
Stake: 3.00
Real stake: 1.00
Bonus stake: 2.00
Win: 10.00

Real win: 3.33
Bonus win: 6.67
```

## 5. Settlement After Bonus Completion

If a bet was placed while a bonus was active but the bonus lifecycle has already ended before that bet settles, the original proportional split is still stored on the bet.

However, the resulting payout is credited as real money so that a completed or expired bonus is not re-created.

## 6. Bonus Expiration

Expired bonuses are processed before betting.

Expiration runs in a separate transaction so that:

- the expired bonus is forfeited and committed
- a following rejected bet does not roll back the expiration

## 7. Payment Callback Idempotency

Duplicate payment callbacks are expected to happen.

Protection is provided by:
- deposit row locking
- completed deposit status
- unique provider reference
- a single database transaction

The second delivery does not credit the wallet again.

## 8. Concurrency

Wallet-changing operations use PostgreSQL pessimistic locking.

For the required scenario:

```text
Balance: 10

Bet A: 8
Bet B: 8
```

one transaction locks the player row first.

Expected result:

```text
1 success
1 insufficient-balance failure
final balance = 2
```

## 9. Ledger

The application treats the ledger as append-only:
- new balance changes create new rows
- existing rows are not updated
- existing rows are not deleted by application logic

## 10. Demo Login

Username-only login is intentionally provided for easier testing.

Behavior:
- existing username -> return existing player
- unknown username -> create player
- no password
- player stored in browser localStorage

## 11. Currency

The project supports only one currency.

All money values are stored with two decimal places.

## 12. Time

Bonus expiration is stored using timestamp values and evaluated server-side.

The seven-day period starts when the bonus is granted.

## 13. Error Handling

The project uses simple HTTP error responses suitable for the exercise.
