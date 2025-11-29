package com.aureobank.controller;

import com.aureobank.app.AppContext;
import com.aureobank.app.SceneRouter;
import com.aureobank.model.Account;
import com.aureobank.theme.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import java.util.List;
import java.util.Objects;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
public class UserDashboardController {
    private final AppContext ctx;
    private final SceneRouter router;
    private final String userId;
    private final BorderPane root;
    private ListView<Account> accountsListView; // To allow refreshing from other methods

    public UserDashboardController(AppContext ctx, SceneRouter router, String userId) {
        this.ctx = ctx; this.router = router; this.userId = userId;
        this.root = build();
    }

    public Node getView() { return root; }

    private BorderPane build() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(24));

        HBox top = new HBox(12);
        top.getChildren().addAll(UI.h2("User Dashboard"), new Region());
        Button logout = UI.secondaryButton("Logout");
        logout.setOnAction(e -> router.showLogin());
        top.getChildren().add(logout);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        pane.setTop(top);

        VBox nav = new VBox(8);
        nav.getChildren().addAll(
                UI.navButton("Accounts", () -> pane.setCenter(accountsView())),
                UI.navButton("Deposit", () -> pane.setCenter(depositView())),
                UI.navButton("Withdraw", () -> pane.setCenter(withdrawView())),
                UI.navButton("Transfer", () -> pane.setCenter(transferView())),
                UI.navButton("Bills", () -> pane.setCenter(billPayView())),
                UI.navButton("Security", () -> pane.setCenter(securityView()))
        );
        pane.setLeft(UI.navCard(nav));

        pane.setCenter(accountsView());
        return pane;
    }

    private Node accountsView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Your Accounts"));
        accountsListView = new ListView<>();
        refreshAccountsList();
        accountsListView.setCellFactory(v -> new ListCell<>() {
            @Override protected void updateItem(Account a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) setText(null); else setText(a.getAccountNumber() + " | ₱" + String.format("%.2f", a.getBalance()));
            }
        });

        Button open = UI.primaryButton("Open Savings Account");
        open.setOnAction(e -> {
            Account newAccount = ctx.accountService.openAccount(userId, "SAVINGS", userId);
            if (newAccount != null) accountsListView.getItems().add(newAccount);
        });
        box.getChildren().addAll(accountsListView, open);
        return UI.cardWrap(box);
    }

    private Node depositView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Deposit"));
        ComboBox<Account> to = new ComboBox<>(); to.getItems().setAll(ctx.accountService.getUserAccounts(userId));
        to.setConverter(accountStringConverter());
        TextField amount = new TextField(); amount.setPromptText("Amount");
        Label status = UI.caption("");
        Button go = UI.primaryButton("Deposit");
        go.setOnAction(e -> {
            if (to.getValue() == null || amount.getText().isBlank()) {
                status.setText("Please select an account and enter an amount.");
                return;
            }
            try {
                ctx.transactionService.deposit(to.getValue().getId(), Double.parseDouble(amount.getText()), userId);
                status.setText("Success");
                refreshAccountsList();
            } catch (NumberFormatException ex) { status.setText("Invalid amount.");
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });
        box.getChildren().addAll(to, amount, go, status);
        return UI.cardWrap(box);
    }

    private Node withdrawView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Withdraw"));
        ComboBox<Account> from = new ComboBox<>(); from.getItems().setAll(ctx.accountService.getUserAccounts(userId));
        from.setConverter(accountStringConverter());
        com.aureobank.theme.VirtualKeypad keypad = new com.aureobank.theme.VirtualKeypad();
        Label status = UI.caption("");
        Button go = UI.primaryButton("Withdraw");
        go.setOnAction(e -> {
            if (from.getValue() == null || keypad.getField().getText().isBlank()) {
                status.setText("Please select an account and enter an amount.");
                return;
            }
            try {
                ctx.transactionService.withdraw(from.getValue().getId(), Double.parseDouble(keypad.getField().getText()), userId);
                status.setText("Success");
                refreshAccountsList();
            } catch (NumberFormatException ex) { status.setText("Invalid amount.");
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });
        box.getChildren().addAll(from, keypad.getView(), go, status);
        return UI.cardWrap(box);
    }

    private Node transferView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Transfer"));
        ComboBox<Account> from = new ComboBox<>(); from.getItems().setAll(ctx.accountService.getUserAccounts(userId));
        from.setConverter(accountStringConverter());
        TextField toId = new TextField(); toId.setPromptText("To Account ID");
        TextField amount = new TextField(); amount.setPromptText("Amount");
        Label status = UI.caption("");
        Button go = UI.primaryButton("Transfer");
        go.setOnAction(e -> {
            if (from.getValue() == null || toId.getText().isBlank() || amount.getText().isBlank()) {
                status.setText("Please fill all fields.");
                return;
            }
            try {
                ctx.transactionService.transfer(from.getValue().getId(), toId.getText(), Double.parseDouble(amount.getText()), userId);
                status.setText("Success");
                refreshAccountsList();
            } catch (NumberFormatException ex) { status.setText("Invalid amount.");
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });
        box.getChildren().addAll(from, toId, amount, go, status);
        return UI.cardWrap(box);
    }

    private Node billPayView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Bill Payments"));
        ComboBox<Account> from = new ComboBox<>(); from.getItems().setAll(ctx.accountService.getUserAccounts(userId));
        from.setConverter(accountStringConverter());
        TextField biller = new TextField(); biller.setPromptText("Biller Name");
        TextField amount = new TextField(); amount.setPromptText("Amount");
        Label status = UI.caption("");
        Button go = UI.primaryButton("Pay");
        go.setOnAction(e -> {
            if (from.getValue() == null || biller.getText().isBlank() || amount.getText().isBlank()) {
                status.setText("Please fill all fields.");
                return;
            }
            try {
                ctx.transactionService.billPay(from.getValue().getId(), biller.getText(), Double.parseDouble(amount.getText()), userId);
                status.setText("Success");
                refreshAccountsList();
            } catch (NumberFormatException ex) { status.setText("Invalid amount.");
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });
        box.getChildren().addAll(from, biller, amount, go, status);
        return UI.cardWrap(box);
    }

    private Node securityView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Security Center"));
        PasswordField np = new PasswordField(); np.setPromptText("New Password");
        Button change = UI.primaryButton("Change Password");
        Label status = UI.caption("");
        change.setOnAction(e -> {
            if (np.getText().isBlank()) {
                status.setText("Password cannot be empty.");
                return;
            }
            ctx.authService.setPassword(userId, np.getText());
            status.setText("Password updated.");
        });
        box.getChildren().addAll(np, change, status);
        return UI.cardWrap(box);
    }

    private void refreshAccountsList() {
        if (accountsListView != null) {
            List<Account> userAccounts = ctx.accountService.getUserAccounts(userId);
            accountsListView.getItems().setAll(userAccounts);
        }
    }

    private StringConverter<Account> accountStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Account a) {
                return a == null ? null : a.getAccountNumber() + " (₱" + String.format("%.2f", a.getBalance()) + ")";
            }

            @Override
            public Account fromString(String s) {
                return null; // Not needed for ComboBox
            }
        };
    }
}
