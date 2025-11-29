AureoBank Documentation

1. Introduction
AureoBank is a professional desktop banking system built with Java 17 and JavaFX. It features an MVC architecture, DAO storage backed by JSON files, robust security, and a polished modern UI. The system includes Admin and User portals and enforces a fixed Bank Reserve of ₱1,000,000.

2. System Overview
- Tech Stack: Java 17+, JavaFX 21, Maven, Gson, SLF4J + Logback
- Architecture: MVC + Services + DAO + JSON storage
- Security: Password hashing (BCrypt preferred, SHA-256 fallback), MFA simulation, validation, logging
- UI: Custom theme, rounded panels, drop shadows, responsive panes, iconography
- Data: Data JSON under data/, logs under logs/
- Key modules: Authentication, User Accounts, Accounts & Cards, Transactions, Reports, Security Center, Reserve Enforcement

3. User Manual
3.1 Roles
- Admin: Manage users/roles, approve/decline applications, monitor transactions, view reports, manage reserve alerts, audit logs.
- Customer: View balances, deposit, withdraw (virtual keypad), transfer, bill payments, apply for accounts/cards, report lost/stolen card, security center.

3.2 Getting Started
- Start application, login. Default admin: admin / Admin@123. Customers can be created by admin or via application form.

3.3 Admin Features
- Dashboard: KPIs, reserve status, alerts.
- Users: Create/Update/Delete, approve/decline applications, reset passwords, manage roles.
- Transactions: Global log view and filters.
- Reports: Export financial summaries, audit logs, admin activity.
- Reserve: Enforced at service layer; deposits decrease reserve; withdrawals increase reserve.

3.4 Customer Features
- Dashboard: Account snapshot, notifications.
- Transactions: Deposit, Withdraw (virtual keypad), Transfer, Bill Pay.
- Accounts & Cards: Apply for new accounts or cards, view card details, report lost/stolen.
- Security Center: Change password, enable MFA (code-based), login history, suspicious activity alerts.

4. Technical Documentation
4.1 Packages
- com.aureobank.app: Application bootstrap and stage management
- com.aureobank.controller: Controllers for JavaFX views
- com.aureobank.view: FXML (if used) and UI components; here we use programmatic JavaFX for custom styling
- com.aureobank.model: Entities (User, Account, Transaction, Card, Role, AuditLog, etc.)
- com.aureobank.dao: DAO interfaces and JSON implementations
- com.aureobank.service: Business services (AuthService, AccountService, TransactionService, ReserveService, ReportService)
- com.aureobank.security: Hashing, MFA, validation
- com.aureobank.util: Helpers (JsonUtil, Ids, Dates, Exporters)
- com.aureobank.theme: Styling and Icon resources mapping

4.2 Data Storage
JSON files: data/users.json, data/accounts.json, data/transactions.json, data/cards.json, data/applications.json, data/audit.json, data/config.json

4.3 Reserve Enforcement
- ReserveService tracks reserve. Fixed base = 1,000,000. Deposits reduce reserve; withdrawals increase reserve.
- Service-level validation prevents operations that would break reserve >= 0.
- Alerts triggered when reserve < threshold (e.g., 10% of base).

4.4 Security
- BCrypt hash with salt by default; if not available, fallback SHA-256 + random salt.
- MFA: time-limited code generated and validated at login if enabled.
- Validation on all inputs; prevents negative/overflow. Transactions check sufficient funds.
- Admin operations logged to audit log with who/when/what.

4.5 Logging
- SLF4J + Logback. Logs written to logs/app.log; rolling policy.

5. Pseudocode (Selected)
AuthService.login(username, password, code):
  user = usersDao.findByUsername(username)
  if user == null or user.disabled: return FAIL
  if !PasswordHasher.verify(password, user.hash): return FAIL
  if user.mfaEnabled:
     if !MFA.verifyCode(user.id, code): return FAIL
  record login audit
  return SUCCESS

TransactionService.deposit(accountId, amount, actor):
  validate amount > 0
  account = accountsDao.get(accountId)
  reserveService.ensureCanDeposit(amount) // deposit reduces reserve
  account.balance += amount
  reserveService.onDeposit(amount)
  transactionsDao.save(Deposit(...))
  audit.log(actor, "DEPOSIT", accountId, amount)
  notify(account.userId)

ReserveService.ensureCanDeposit(amount):
  if (reserve - amount) < 0: throw ReserveExceeded

TransactionService.withdraw(accountId, amount, actor):
  validate amount > 0
  account = accountsDao.get(accountId)
  if account.balance < amount: throw InsufficientFunds
  reserveService.onWithdraw(amount) // withdrawal increases reserve
  account.balance -= amount
  transactionsDao.save(Withdrawal(...))
  audit.log(actor, "WITHDRAW", accountId, amount)

6. Flowcharts (Textual)
Login:
[Start] -> Enter creds -> Validate -> [MFA?] -> Enter code -> Success -> Dashboard | Fail -> Error dialog

Deposit:
[Start] -> Select account -> Enter amount -> Validate -> Reserve check -> Update balance/reserve -> Persist -> Notify -> Success dialog

Withdraw (Virtual Keypad):
[Start] -> Keypad input -> Validate -> Sufficient funds -> Update -> Persist -> Success

Transfer:
[Start] -> From/To -> Amount -> Validate -> Sufficient funds -> Reserve impacts (withdrawal then deposit) -> Persist -> Success

7. Known Issues
- JSON storage is not ACID; concurrent writes can overwrite. In desktop single-user mode, it is acceptable.
- Large datasets may be slower with JSON; consider moving to embedded DB (H2/SQLite) for scale.
- MFA is simulated; not integrated with real SMS/Email providers.

8. Appendices (AI Usage)
- This project structure and code were generated with the assistance of an AI coding system and curated by a senior engineer.
- Prompts and responses are kept in docs/AI-TRACE.md.
