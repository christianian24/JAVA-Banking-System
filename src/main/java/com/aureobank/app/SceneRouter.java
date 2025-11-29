package com.aureobank.app;

import com.aureobank.controller.AdminDashboardController;
import com.aureobank.controller.LoginController;
import com.aureobank.controller.UserDashboardController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class SceneRouter {
    private final AppContext ctx;
    private final StackPane root;

    public SceneRouter(AppContext ctx) {
        this.ctx = ctx;
        this.root = new StackPane();
        this.root.setAlignment(Pos.CENTER);
    }

    public Scene buildInitialScene() {
        showLogin();
        return new Scene(root, 1200, 800);
    }

    public void showLogin() {
        LoginController view = new LoginController(ctx, this);
        root.getChildren().setAll(view.getView());
    }

    public void showAdminDashboard(String userId) {
        AdminDashboardController view = new AdminDashboardController(ctx, this, userId);
        root.getChildren().setAll(view.getView());
    }

    public void showUserDashboard(String userId) {
        UserDashboardController view = new UserDashboardController(ctx, this, userId);
        root.getChildren().setAll(view.getView());
    }
}
