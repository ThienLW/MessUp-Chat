package com.example.project_caroz;

import com.example.project_caroz.stage.StageController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    public static String mainViewID = "MainView";
    public static String mainViewRes = "/com/example/project_caroz/main-chat-view.fxml";
    public static String loginViewID = "LoginView";
    public static String loginViewRes = "/com/example/project_caroz/login-view.fxml";
    public static String forgotPasswdViewID = "ForgotPasswdView";
    public static String forgotPasswdViewRes = "/com/example/project_caroz/forgot-passwd-view.fxml";
    public static String registerViewID = "RegisterView";
    public static String registerViewRes = "/com/example/project_caroz/register-view.fxml";

    public static String EmojiSelectorID = "EmojiSelector";
    public static String EmojiSelectorRes = "/com/example/project_caroz/emoji-selector-view.fxml";

    public static String UserProfileID = "UserProfile";
    public static String UserProfileRes = "/com/example/project_caroz/user-profile-view.fxml";

    private StageController stageController;



    @Override
    public void start(Stage primaryStage) {
        // Tạo một StageController
        stageController = new StageController();

        // Giao phần chính cho controller quản lý
        stageController.setPrimaryStage("primaryStage", primaryStage);

        // Tải hai stage, mỗi giao diện một stage
        stageController.loadStage(loginViewID, loginViewRes, StageStyle.UNDECORATED);

        // Hiển thị stage LoginView
        stageController.setStage(loginViewID);
    }


    public static void main(String[] args) {
        launch(args);
    }
}