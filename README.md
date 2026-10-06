# Casino Wallet

A small casino wallet application built with **Kotlin / Spring Boot**, **PostgreSQL**, and **Angular 17**.

The project demonstrates wallet balance management, append-only ledger accounting, idempotent payment callbacks, HMAC validation, welcome bonus wagering rules, concurrency-safe betting, proportional win settlement, and a simple multilingual Angular UI.

## Tech Stack

### Backend
- Kotlin
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Gradle
- Testcontainers

### Frontend
- Angular 17
- TypeScript
- RxJS
- SCSS

### Infrastructure
- Docker / Docker Compose for PostgreSQL

## Features

### Wallet
Each player has:
- real balance
- bonus balance

Balances are stored as `NUMERIC(19,2)` in PostgreSQL and handled as `BigDecimal` in Kotlin.

Every balance change is recorded in an append-only ledger.

### Deposits
A newly created deposit is `PENDING` until a payment-provider callback arrives.

The callback contains:
- deposit ID
- provider reference
- amount in cents
- HMAC-SHA256 signature in `X-Signature`

The backend validates the raw request body signature, validates the amount, locks the deposit, checks idempotency, credits the player, and creates ledger entries.

Duplicate callbacks do not credit the wallet twice.

### Welcome Bonus
Rules:
- only the first completed deposit is eligible
- the first completed deposit must be at least 20.00
- bonus equals the deposit amount
- bonus is capped at 100.00
- wagering requirement is 20x the granted bonus
- bonus expires after 7 days

Examples:

| First completed deposit | Bonus | Wagering requirement |
|---|---:|---:|
| 10.00 | 0.00 | 0.00 |
| 20.00 | 20.00 | 400.00 |
| 50.00 | 50.00 | 1,000.00 |
| 150.00 | 100.00 | 2,000.00 |

### Betting
When placing a bet:
1. real money is used first
2. bonus money is used for the remainder
3. while a bonus is active, stake above 5.00 is rejected
4. the valid stake counts toward wagering progress

Example:

```text
Real balance:  3.00
Bonus balance: 20.00
Stake:         5.00

Real stake:    3.00
Bonus stake:   2.00
```

### Settlement
Wins are split using the same proportion as the original stake.

Example:

```text
Stake:       5.00
Real stake:  3.00
Bonus stake: 2.00
Win:         10.00

Real win:    6.00
Bonus win:   4.00
```

The real portion is rounded to 2 decimals and the bonus portion is calculated as the remainder so that:

```text
realWin + bonusWin = winAmount
```

### Bonus Completion
When wagering is complete:
- bonus status becomes `COMPLETED`
- remaining bonus balance is converted to real money
- a `BONUS_CONVERTED` ledger entry is written

### Bonus Expiry
When the bonus expires:
- bonus status becomes `EXPIRED`
- remaining bonus balance is forfeited
- a `BONUS_FORFEITED` ledger entry is written

### Concurrency Safety
Wallet-changing operations use PostgreSQL pessimistic row locking (`SELECT ... FOR UPDATE`).

This guarantees the required scenario:

```text
Starting balance: 10.00

Two simultaneous bets:
8.00
8.00

Expected:
1 succeeds
1 fails
Final balance = 2.00
```

An integration test verifies this with PostgreSQL via Testcontainers.

### Demo Login
The UI uses a simple username-only login for testing:
- if the username exists, the existing player is returned
- if it does not exist, a new player is created
- no passwords are used
- the selected player is stored in `localStorage`

## Frontend
The Angular page includes:
- login/logout
- real balance
- bonus balance
- total balance
- bonus progress
- deposit form
- play-a-round form
- automatic demo settlement
- paginated ledger
- English and Bulgarian translations

## Project Structure

```text
casino-wallet/
├── backend/
│   ├── src/main/kotlin/com/casino/wallet/
│   │   ├── betting/
│   │   ├── bonus/
│   │   ├── common/
│   │   ├── deposit/
│   │   ├── ledger/
│   │   ├── payment/
│   │   └── player/
│   ├── src/main/resources/
│   │   └── db/migration/
│   └── src/test/
├── frontend/
│   └── src/app/
│       ├── core/
│       ├── i18n/
│       ├── login/
│       └── wallet/
├── tools/
│   └── sign-callback.js
└── docker-compose.yml
```

# Running the Project

## 1. Start PostgreSQL

From the project root:

```bash
docker compose up -d
```

Database defaults:

```text
Database: casino_wallet
Username: casino
Password: casino
Port: 5432
```

## 2. Start Backend

```bash
cd backend
./gradlew bootRun
```

Windows:

```powershell
cd backend
.\gradlew.bat bootRun
```

Backend:

```text
http://localhost:8080
```

## 3. Start Frontend

```bash
cd frontend
npm install
npm start
```

Frontend:

```text
http://localhost:4200
```

The Angular dev proxy forwards `/api` to the backend.

# Main API Endpoints

## Login or Create Player

```http
POST /api/players/login
```

```json
{
  "username": "alice"
}
```

## Get Wallet

```http
GET /api/players/{playerId}/wallet
```

## Get Bonus Progress

```http
GET /api/players/{playerId}/bonus
```

## Create Deposit

```http
POST /api/players/{playerId}/deposits
```

```json
{
  "amount": 50.00
}
```

## Payment Callback

```http
POST /api/payments/callback
```

Header:

```text
X-Signature: <HMAC-SHA256>
```

Body:

```json
{
  "depositId": "uuid",
  "providerReference": "provider-payment-001",
  "amountCents": 5000
}
```

## Place Bet

```http
POST /api/players/{playerId}/bets
```

```json
{
  "stake": 5.00
}
```

## Settle Bet

```http
POST /api/players/{playerId}/bets/{betId}/settle
```

```json
{
  "winAmount": 10.00
}
```

## Get Ledger

```http
GET /api/players/{playerId}/ledger?page=0&size=10
```

# Payment Callback Testing

Update:

```text
tools/sign-callback.js
```

with:

```js
const payload = {
  depositId: "DEPOSIT_ID",
  providerReference: "provider-payment-001",
  amountCents: 5000,
};
```

Run:

```bash
node tools/sign-callback.js
```

The script prints the raw body, HMAC signature, and a ready-to-use curl request.

Expected:
- first valid callback -> `credited: true`
- same callback again -> `credited: false`
- invalid signature -> HTTP 401

# Running Tests

```bash
cd backend
./gradlew clean test
```

Windows:

```powershell
cd backend
.\gradlew.bat clean test
```

Concurrency test only:

```bash
./gradlew test --tests "*BetConcurrencyIntegrationTest"
```

Docker must be running because the integration test uses Testcontainers.

# Manual Testing

Recommended manual scenarios:
1. login with a new username
2. create a deposit and verify `PENDING`
3. send bad HMAC and verify rejection
4. send valid HMAC and verify credit
5. resend callback and verify no duplicate credit
6. verify welcome bonus and 20x wagering
7. verify EUR 5 active-bonus stake limit
8. verify real-first / bonus-second stake allocation
9. verify proportional settlement
10. verify split rounding
11. verify wagering completion and bonus conversion
12. verify bonus expiry and forfeiture
13. verify EUR 100 bonus cap
14. verify first deposit below EUR 20 grants no later welcome bonus
15. verify ledger pagination
16. verify EN/BG translations
17. verify two simultaneous EUR 8 bets against EUR 10

# Database Access

```bash
docker exec -it casino-wallet-postgres psql -U casino -d casino_wallet
```

Example:

```sql
SELECT username, real_balance, bonus_balance
FROM players;
```

# Reset Local Database

```bash
docker compose down -v
docker compose up -d
```

Warning: this removes all local test data.

# Important Design Choices

- Money uses `BigDecimal`, never `Double`
- Balances use `NUMERIC(19,2)`
- Wallet changes and ledger rows are committed in the same transaction
- Payment callback handling is idempotent
- PostgreSQL row locks prevent double spending
- Ledger rows are treated as append-only by the application
- Demo game outcomes are generated only for UI convenience
