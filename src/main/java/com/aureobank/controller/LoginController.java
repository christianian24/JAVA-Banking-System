package com.aureobank.controller;

import com.aureobank.app.AppContext;
import com.aureobank.app.SceneRouter;
import com.aureobank.theme.UI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginController {
    private final AppContext ctx;
    private final SceneRouter router;
    private final VBox view;

    public LoginController(AppContext ctx, SceneRouter router) {
        this.ctx = ctx; this.router = router;
        this.view = build();
    }

    public Node getView() { return view; }

    private VBox build() {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));

        Label title = UI.h1("AureoBank");
        Label subtitle = UI.caption("Secure Banking Portal");

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        TextField mfa = new TextField();
        mfa.setPromptText("MFA Code (if enabled)");

        Button login = UI.primaryButton("Sign In");
        Button sendCode = UI.secondaryButton("Send MFA Code");
        Label error = UI.errorLabel();

        login.setOnAction(e -> {
            var res = ctx.authService.login(username.getText(), password.getText(), mfa.getText());
            switch (res) {
                case MFA_REQUIRED -> error.setText("MFA code required. Click 'Send MFA Code'.");
                case FAIL -> error.setText("Invalid credentials or inactive account.");
                case SUCCESS_ADMIN -> ctx.usersDao.findByUsername(username.getText())
                        .ifPresent(user -> router.showAdminDashboard(user.getId()));
                case SUCCESS_USER -> ctx.usersDao.findByUsername(username.getText())
                        .ifPresent(user -> router.showUserDashboard(user.getId()));
            }
        });
        sendCode.setOnAction(e -> {
            ctx.usersDao.findByUsername(username.getText()).ifPresentOrElse(u -> {
                if (!u.isMfaEnabled()) {
                    error.setText("MFA is not enabled for this user.");
                } else {
                    String code = ctx.mfaService.issue(u.getId());
                    new Alert(Alert.AlertType.INFORMATION, "Your MFA code: " + code).show();
                }
            }, () -> error.setText("Unknown user."));
        });

        VBox card = UI.card(360);
        HBox actions = new HBox(8, login, sendCode);
        card.getChildren().addAll(title, subtitle, username, password, mfa, actions, error);
        box.getChildren().add(card);
        return box;
    }
}
