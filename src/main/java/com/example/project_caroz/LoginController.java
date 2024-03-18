package com.example.project_caroz;

import com.example.project_caroz.model.ClientModel;
import com.example.project_caroz.stage.ControlledStage;
import com.example.project_caroz.stage.StageController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements ControlledStage, Initializable {

    @FXML
    private Label PasswordLabel;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnMinimize;

    @FXML
    private Button btn_login;

    @FXML
    private Button btn_signup;

    @FXML
    private ComboBox<String> comboBoxLanguage;

    @FXML
    private Label forgotPasswordLabel;

    @FXML
    private Label haveAccountLabel;

    @FXML
    private Label invalid_login_alert;

    @FXML
    private Label loginLabel;

    @FXML
    private Label portLabel;

    @FXML
    private Label serverLabel;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private TextField showTextPassword;

    @FXML
    private PasswordField textPassword;

    @FXML
    private TextField txtHostName;

    @FXML
    private TextField txtPort;

    @FXML
    private TextField txtUsername;

    public static String languageSelected = "en";

    @FXML
    private Label usernameLabel;
    StageController myController;
    ClientModel model;

    public LoginController() {
        super();
    }


    public void setStageController(StageController stageController) {
        this.myController = stageController;
        model = ClientModel.getInstance();
    }

    // Internationalize
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", new Locale(languageSelected));
    // Alert Language Modify
    private String alertLoginInfor = resourceBundle.getString("alertLoginInfor");
    private String alertLoginUsername = resourceBundle.getString("alertLoginUsername");
    private String alertLoginPasswd = resourceBundle.getString("alertLoginPasswd");
    private String alertWelcome = resourceBundle.getString("alertWelcome");
    private String messError = resourceBundle.getString("messError");
    private String incorrectPasswd = resourceBundle.getString("incorrectPasswd");
    private String loggedIn = resourceBundle.getString("loggedIn");
    private String loginFail = resourceBundle.getString("loginFail");
    private String notExist = resourceBundle.getString("notExist");

    private String confirmExit = resourceBundle.getString("confirmExit");
    private String sureExit = resourceBundle.getString("sureExit");
    private String select = resourceBundle.getString("select");

    public void changeLanguageToSelected() {
        resourceBundle = ResourceBundle.getBundle("messages", new Locale(languageSelected));
        alertLoginInfor = resourceBundle.getString("alertLoginInfor");
        alertLoginUsername = resourceBundle.getString("alertLoginUsername");
        alertLoginPasswd = resourceBundle.getString("alertLoginPasswd");
        alertWelcome = resourceBundle.getString("alertWelcome");

        messError = resourceBundle.getString("messError");
        incorrectPasswd = resourceBundle.getString("incorrectPasswd");
        loggedIn = resourceBundle.getString("loggedIn");
        loginFail = resourceBundle.getString("loginFail");
        notExist = resourceBundle.getString("notExist");

        confirmExit = resourceBundle.getString("confirmExit");
        sureExit = resourceBundle.getString("sureExit");
        select = resourceBundle.getString("select");

        updateUI();
    }

    private void updateUI() {
        // Dùng resourceBundle để lấy các chuỗi theo ngôn ngữ
        String welcome = resourceBundle.getString("welcome");
        String login = resourceBundle.getString("login");
        String username = resourceBundle.getString("username");
        String password = resourceBundle.getString("password");
        String showPass = resourceBundle.getString("showPass");
        String toShow = resourceBundle.getString("toShow");
        String forgotPass = resourceBundle.getString("forgotPass");
        String haveAccount = resourceBundle.getString("haveAccount");
        String register = resourceBundle.getString("register");
        String server = resourceBundle.getString("server");
        String port = resourceBundle.getString("port");

        // Áp dụng chuỗi vào giao diện người dùng
        invalid_login_alert.setText(welcome);
        loginLabel.setText(login);
        usernameLabel.setText(username);
        PasswordLabel.setText(password);
        txtUsername.setPromptText(username);
        textPassword.setPromptText(password);
        showTextPassword.setPromptText(toShow);
        showPasswordCheckBox.setText(showPass);
        forgotPasswordLabel.setText(forgotPass);
        btn_login.setText(login);
        haveAccountLabel.setText(haveAccount);
        btn_signup.setText(register);
        serverLabel.setText(server);
        portLabel.setText(port);

    }

    public void initialize(URL location, ResourceBundle resources) {

        comboBoxLanguage.setOnAction(e ->{
            String selectedStatus = comboBoxLanguage.getValue();
            if ("English".equalsIgnoreCase(selectedStatus)) {
                languageSelected = "en";
            } else if("Tiếng Việt".equalsIgnoreCase(selectedStatus)) {
                languageSelected = "vi";
            }
            else if("中文".equalsIgnoreCase(selectedStatus)) {
                languageSelected = "zh";
            }
            else if("Français".equalsIgnoreCase(selectedStatus)) {
                languageSelected = "fr";
            }
            else {
                languageSelected = "ru";
            }
            changeLanguageToSelected();
        });

        textPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btn_login.fire(); // Kích hoạt hành động của nút "Login"
            }
        });
        showTextPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btn_login.fire(); // Kích hoạt hành động của nút "Login"
            }
        });

        // Đăng ký sự kiện cho Label khi khởi tạo controller
        forgotPasswordLabel.setOnMouseClicked(this::forgotPasswordClicked);
    }

    public void goToMain() {
        myController.loadStage(MainApp.mainViewID,MainApp.mainViewRes, StageStyle.UNDECORATED);
        myController.setStage(MainApp.mainViewID,MainApp.loginViewID);
        myController.getStage(MainApp.mainViewID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                model.disConnect();
                myController.unloadStage(MainApp.EmojiSelectorID);
            }
        });
    }

    public void logIn() {
        StringBuffer result = new StringBuffer();

        if (txtUsername.getText().isEmpty() && textPassword.getText().isEmpty()) {
            invalid_login_alert.setText(alertLoginInfor);
            invalid_login_alert.setTextFill(Color.RED);
        } else if (txtUsername.getText().isEmpty()) {
            invalid_login_alert.setText(alertLoginUsername);
            invalid_login_alert.setTextFill(Color.RED);
        } else if (textPassword.getText().isEmpty()) {
            invalid_login_alert.setText(alertLoginPasswd);
            invalid_login_alert.setTextFill(Color.RED);
        } else {
            if (model.CheckLogin(txtUsername.getText(), txtHostName.getText(),textPassword.getText(), result)) {
                goToMain();
            } else {
                invalid_login_alert.setText(alertWelcome);
                invalid_login_alert.setTextFill(Color.valueOf("#2562ba"));
                showLoginError(result.toString());
            }
        }
    }

    private void forgotPasswordClicked(MouseEvent event) {
        myController.loadStage(MainApp.forgotPasswdViewID,MainApp.forgotPasswdViewRes, StageStyle.UNDECORATED);
        myController.setStage(MainApp.forgotPasswdViewID,MainApp.loginViewID);
        myController.getStage(MainApp.forgotPasswdViewID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                model.disConnect();
                myController.unloadStage(MainApp.EmojiSelectorID);
            }
        });
    }

    /**
     * Thu nhỏ cửa sổ
     * @param event
     */
    @FXML public void minBtnAction(ActionEvent event){
        Stage stage = myController.getStage(MainApp.loginViewID);
//        stage.setIconified(true);

        // Tạo một Timeline cho hiệu ứng thu nhỏ
        Timeline timeline = new Timeline();

        // Tạo một DoubleProperty để thay đổi kích thước cửa sổ
        DoubleProperty width = new SimpleDoubleProperty(stage.getWidth());
        DoubleProperty height = new SimpleDoubleProperty(stage.getHeight());

        // Tạo các KeyValue để thay đổi kích thước cửa sổ
        KeyValue kvX = new KeyValue(width, 0);
        KeyValue kvY = new KeyValue(height, 0);

        // Tạo các KeyFrame cho hiệu ứng
        KeyFrame kf = new KeyFrame(Duration.millis(200), kvX, kvY);

        // Thêm KeyFrame vào Timeline
        timeline.getKeyFrames().add(kf);

        // Xử lý sự kiện khi hoàn thành hiệu ứng
        timeline.setOnFinished(e -> {
            stage.setIconified(true); // Thu nhỏ cửa sổ
        });

        // Bắt đầu chạy Timeline
        timeline.play();
    }
    /**
     * Đóng cửa sổ và thoát chương trình
     * @param event
     */
    @FXML public void closeBtnAction(ActionEvent event){
        event.consume(); // Ngăn cửa sổ đóng ngay lập tức
        confirmExitDialog();
    }
    public void confirmExitDialog(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(confirmExit);
        alert.setHeaderText(sureExit);
        alert.setContentText(select);
        // customOkButtonType
        ButtonType customOkButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        // customCancelButtonType
        ButtonType customCancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(customOkButtonType, customCancelButtonType);
        // Custom style for OK and Cancel button
        String okBtnCSS = "-fx-background-color: #3AB0FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand";
        String cancelBtnCSS = "-fx-background-color: #F94C10; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand";
        alert.getDialogPane().lookupButton(customOkButtonType).setStyle(okBtnCSS);
        alert.getDialogPane().lookupButton(customCancelButtonType).setStyle(cancelBtnCSS);

        alert.showAndWait().ifPresent(response -> {
            if (response == customOkButtonType) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @FXML
    public void setShowPasswordCheckBox(ActionEvent event) {
        if (showPasswordCheckBox.isSelected()) {
            showTextPassword.setText(textPassword.getText());
            textPassword.setManaged(false);
            textPassword.setVisible(false);

            // Tạo một listener để theo dõi thay đổi trong showTextPassword -> làm showTextPassword và textPassword đồng bộ
            showTextPassword.textProperty().addListener((observable, oldValue, newValue) -> {
                // Cập nhật textPassword với giá trị mới
                textPassword.setText(newValue);
            });
        } else {
            textPassword.setManaged(true);
            textPassword.setVisible(true);
        }
    }

    public void showLoginError(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(messError);
        if(error.equalsIgnoreCase("Incorrect password!"))
            error = incorrectPasswd;
        else if(error.equalsIgnoreCase("This user is logged in!"))
            error = loggedIn;
        else
            error = notExist;
        alert.setContentText(loginFail + "\n" + error);
        System.out.println(error);
        alert.show();
    }

public void onRegisterBtn(ActionEvent actionEvent) {
    myController.loadStage(MainApp.registerViewID,MainApp.registerViewRes, StageStyle.UNDECORATED);
    myController.setStage(MainApp.registerViewID,MainApp.loginViewID);
    myController.getStage(MainApp.registerViewID).setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            model.disConnect();
            myController.unloadStage(MainApp.EmojiSelectorID);
        }
    });
}
}


