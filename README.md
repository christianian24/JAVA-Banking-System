AureoBank - Desktop Banking System (Java 17, JavaFX, MVC, DAO)

Overview
- Professional desktop banking system with Admin and User sides
- Java 17+, JavaFX modern UI, MVC architecture, DAO for JSON storage
- Security: SHA-256 hashing with salt, input validation, MFA simulation
- Bank Reserve System: Fixed ₱1,000,000 with enforcement and alerts

How to Run (VS Code)
1) Install requirements
   - Java JDK 17+
   - VS Code + Extension Pack for Java (Microsoft)
   - JavaFX SDK 21+ (or use Maven below which auto-fetches)
   - Maven 3.8+

2) Open Folder in VS Code
   - File > Open Folder... select the project root

3) Build and run (Maven)
   - In VS Code Terminal:
     mvn clean javafx:run

4) Package executable JAR
   - mvn clean package
   - The executable JAR: target/aureobank-1.0.0-shaded.jar
   - Run: java -jar target/aureobank-1.0.0-shaded.jar

Notes
- Uses JavaFX for modern UI: rounded corners, shadows, gradients, custom theme
- JSON data stored under data/ directory
- Logs under logs/ directory
- Default admin: admin / Admin@123 (change on first login)

Documentation is in docs/ directory.
