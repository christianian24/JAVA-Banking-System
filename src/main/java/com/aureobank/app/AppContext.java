package com.aureobank.app;

import com.aureobank.dao.*;
import com.aureobank.dao.json.*;
import com.aureobank.security.MFAService;
import com.aureobank.security.PasswordHasher;
import com.aureobank.service.*;
import com.aureobank.util.JsonStore;
import com.aureobank.util.LoggerSetup;
import java.nio.file.Path;

public class AppContext {
    public final UsersDao usersDao;
    public final AccountsDao accountsDao;
    public final TransactionsDao transactionsDao;
    public final CardsDao cardsDao;
    public final ApplicationsDao applicationsDao;
    public final AuditDao auditDao;
    public final ConfigDao configDao;

    public final AuthService authService;
    public final AccountService accountService;
    public final TransactionService transactionService;
    public final ReserveService reserveService;
    public final ReportService reportService;
    public final NotificationService notificationService;

    public final PasswordHasher passwordHasher;
    public final MFAService mfaService;

    private AppContext(
            UsersDao usersDao,
            AccountsDao accountsDao,
            TransactionsDao transactionsDao,
            CardsDao cardsDao,
            ApplicationsDao applicationsDao,
            AuditDao auditDao,
            ConfigDao configDao,
            AuthService authService,
            AccountService accountService,
            TransactionService transactionService,
            ReserveService reserveService,
            ReportService reportService,
            NotificationService notificationService,
            PasswordHasher passwordHasher,
            MFAService mfaService) {
        this.usersDao = usersDao;
        this.accountsDao = accountsDao;
        this.transactionsDao = transactionsDao;
        this.cardsDao = cardsDao;
        this.applicationsDao = applicationsDao;
        this.auditDao = auditDao;
        this.configDao = configDao;
        this.authService = authService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.reserveService = reserveService;
        this.reportService = reportService;
        this.notificationService = notificationService;
        this.passwordHasher = passwordHasher;
        this.mfaService = mfaService;
    }

    public static AppContext bootstrap() {
        LoggerSetup.init();
        Path base = Path.of("data");
        JsonStore store = new JsonStore(base);

        UsersDao usersDao = new JsonUsersDao(store);
        AccountsDao accountsDao = new JsonAccountsDao(store);
        TransactionsDao transactionsDao = new JsonTransactionsDao(store);
        CardsDao cardsDao = new JsonCardsDao(store);
        ApplicationsDao applicationsDao = new JsonApplicationsDao(store);
        AuditDao auditDao = new JsonAuditDao(store);
        ConfigDao configDao = new JsonConfigDao(store);

        PasswordHasher hasher = new PasswordHasher();
        MFAService mfa = new MFAService();
        ReserveService reserve = new ReserveService(configDao, auditDao);
        NotificationService notifications = new NotificationService();
        AccountService accounts = new AccountService(accountsDao, usersDao, auditDao);
        TransactionService tx = new TransactionService(accountsDao, transactionsDao, reserve, auditDao, notifications);
        ReportService reports = new ReportService(accountsDao, transactionsDao, auditDao);
        AuthService auth = new AuthService(usersDao, hasher, mfa, auditDao);

        SeedData.ensureSeedAdmin(usersDao, hasher, auditDao, configDao);

        return new AppContext(
                usersDao, accountsDao, transactionsDao, cardsDao, applicationsDao, auditDao, configDao,
                auth, accounts, tx, reserve, reports, notifications, hasher, mfa
        );
    }
}
