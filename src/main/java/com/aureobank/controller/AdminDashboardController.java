package com.aureobank.controller;

import com.aureobank.app.AppContext;
import com.aureobank.app.SceneRouter;
import com.aureobank.model.Account;
import static com.aureobank.model.Role.CUSTOMER;
import com.aureobank.model.User;
import com.aureobank.theme.UI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import java.security.SecureRandom;
import javafx.scene.layout.*;

public class AdminDashboardController {
    private final AppContext ctx;
    private final SceneRouter router;
    private final String userId;
    private final BorderPane root;

    public AdminDashboardController(AppContext ctx, SceneRouter router, String userId) {
        this.ctx = ctx; this.router = router; this.userId = userId;
        this.root = build();
    }

    public Node getView() { return root; }

    private BorderPane build() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(24));

        // Top bar
        HBox top = new HBox(12);
        top.getChildren().addAll(UI.h2("Admin Dashboard"), new Region());
        Button logout = UI.secondaryButton("Logout");
        logout.setOnAction(e -> router.showLogin());
        top.getChildren().add(logout);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        pane.setTop(top);

        // Left navigation
        VBox nav = new VBox(8);
        nav.getChildren().addAll(
                UI.navButton("Users", () -> pane.setCenter(usersView())),
                UI.navButton("Transactions", () -> pane.setCenter(transactionsView())),
                UI.navButton("Reports", () -> pane.setCenter(reportsView())),
                UI.navButton("Reserve", () -> pane.setCenter(reserveView()))
        );
        pane.setLeft(UI.navCard(nav));

        // Center default
        pane.setCenter(usersView());
        return pane;
    }

    private Node usersView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("User Management"));
        ListView<User> list = new ListView<>();
        list.getItems().addAll(ctx.usersDao.findAll());
        list.setCellFactory(v -> new ListCell<>() {
            @Override protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                if (empty || u == null) setText(null); else setText(u.getUsername() + " (" + (u.isActive()?"active":"disabled") + ")");
            }
        });

        Button create = UI.primaryButton("Create User");
        create.setOnAction(e -> {
            User u = new User();
            u.setUsername("user" + System.currentTimeMillis());
            u.setDisplayName("Customer");
            u.setActive(true);
            u.setRole(CUSTOMER);
            // Security: Generate a random password instead of using a hardcoded one.
            String randomPassword = generateRandomPassword();
            u.setPasswordHash(ctx.passwordHasher.hash(randomPassword));
            ctx.usersDao.save(u);
            // More efficient UI update
            list.getItems().add(u);
        });

        Button reset = UI.secondaryButton("Reset Password");
        reset.setOnAction(e -> {
            User u = list.getSelectionModel().getSelectedItem();
            if (u != null) {
                // Security: Generate a random password instead of using a hardcoded one.
                String randomPassword = generateRandomPassword();
                u.setPasswordHash(ctx.passwordHasher.hash(randomPassword));
                // In a real app, you'd show this password to the admin or email it to the user.
                ctx.usersDao.save(u);
            }
        });

        Button delete = UI.dangerButton("Delete User");
        delete.setOnAction(e -> {
            User u = list.getSelectionModel().getSelectedItem();
            if (u != null) {
                if (u.getId().equals(userId)) {
                    new Alert(Alert.AlertType.ERROR, "You cannot delete your own account.").show();
                    return;
                }                ctx.usersDao.delete(u.getId());
                // More efficient UI update
                list.getItems().remove(u);
            }
        });

        HBox actions = new HBox(8, create, reset, delete);
        box.getChildren().addAll(list, actions);
        return UI.cardWrap(box);
    }

    private Node transactionsView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Global Transactions"));
        ListView<String> list = new ListView<>();
        ctx.transactionsDao.findAll().forEach(t -> list.getItems().add(t.getTimestamp()+" "+t.getType()+" ₱"+t.getAmount()+" "+(t.getNote()==null?"":t.getNote())));
        box.getChildren().add(list);
        return UI.cardWrap(box);
    }

    private Node reportsView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Reports"));
        TextField accountId = new TextField(); accountId.setPromptText("Account ID");
        Button export = UI.primaryButton("Export Statement");
        Label status = UI.caption("");
        export.setOnAction(e -> {
            try {
                var path = ctx.reportService.exportStatement(accountId.getText());
                status.setText("Exported to: " + path.toAbsolutePath());
            } catch (Exception ex) {
                status.setText("Failed: " + ex.getMessage());
            }
        });
        box.getChildren().addAll(accountId, export, status);
        return UI.cardWrap(box);
    }

    private Node reserveView() {
        VBox box = new VBox(12);
        box.getChildren().add(UI.h3("Bank Reserve"));
        Label current = UI.h2("Current Reserve: ₱" + String.format("%.2f", ctx.reserveService.getReserve()));
        box.getChildren().add(current);
        return UI.cardWrap(box);
    }

    private String generateRandomPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        // In a real app, this should be displayed to the admin or sent to the user.
        return sb.toString();
    }
}
