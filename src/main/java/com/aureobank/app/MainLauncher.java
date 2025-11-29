package com.aureobank.app;

import com.aureobank.util.DataInit;
import com.aureobank.AureoBankApp;   // <-- added import
import javafx.application.Application;

// Create this file if your project doesn't already have a reliable main launcher.
// Update AureoBankApp below with your actual Application class name if different.
public class MainLauncher {
    public static void main(String[] args) {
        // Ensure folders exist before the app starts
        DataInit.ensureDataFolders();

        // Launch the real JavaFX Application class (replace AureoBankApp if needed)
        Application.launch(AureoBankApp.class, args);
    }
}