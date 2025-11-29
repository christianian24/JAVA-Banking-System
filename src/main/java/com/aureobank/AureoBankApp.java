package com.aureobank;

import com.aureobank.app.AppContext;
import com.aureobank.app.SceneRouter;
import com.aureobank.theme.AureoTheme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AureoBankApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(AureoBankApp.class);

    @Override
    public void start(Stage stage) {
        try {
            AppContext context = AppContext.bootstrap();
            SceneRouter router = new SceneRouter(context);
            Scene scene = router.buildInitialScene();
            AureoTheme.apply(scene);

            stage.setTitle("AureoBank");
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            log.error("Failed to launch application", e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
