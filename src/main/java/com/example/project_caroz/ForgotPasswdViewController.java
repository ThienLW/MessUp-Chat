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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class ForgotPasswdViewController implements ControlledStage, Initializable {

    StageController stageController;
    ClientModel model;
    @FXML
    private Button btnBack;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnMinimize;

    @FXML
    private Button btnProceed;

    @FXML
    private Button btnResetPasswd;

    @FXML
    private ComboBox<String> comboBoxSecQuestion;

    @FXML
    private AnchorPane forgotPasswdPane;
    @FXML
    private AnchorPane resetPasswdPane;

    @FXML
    private TextField txtAnsw;

    @FXML
    private PasswordField txtConfirmPasswd;

    @FXML
    private Label txtForgotPasswd;

    @FXML
    private PasswordField txtNewPasswd;

    @FXML
    private Label txtResetPasswd;

    @FXML
    private TextField txtUserName;


    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
        model = ClientModel.getInstance();
    }

    // Internationalize
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", new Locale(LoginController.languageSelected));

    // Alert Language Modify
    private String error = resourceBundle.getString("error");
    private String passwdMismatch = resourceBundle.getString("passwdMismatch");
    private String messError = resourceBundle.getString("messError");
    private String notExist = resourceBundle.getString("notExist");
    private String inforFail = resourceBundle.getString("inforFail");
    private String notMatch = resourceBundle.getString("notMatch");
    private String wrongAnswer = resourceBundle.getString("wrongAnswer");
    private String changePasswdDone = resourceBundle.getString("changePasswdDone");
    private String passwdUpdateDone = resourceBundle.getString("passwdUpdateDone");
    private String congratsUpdate = resourceBundle.getString("congratsUpdate");
    private String sysError = resourceBundle.getString("sysError");

    private String confirmExit = resourceBundle.getString("confirmExit");
    private String sureExit = resourceBundle.getString("sureExit");
    private String select = resourceBundle.getString("select");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String forgotPasswd = resourceBundle.getString("forgotPasswd");
        String phoneNumOrEmail = resourceBundle.getString("phoneNumOrEmail");
        String selectSecQuestions = resourceBundle.getString("selectSecQuestions");
        String answer = resourceBundle.getString("answer");
        String proceed = resourceBundle.getString("proceed");
        String back = resourceBundle.getString("back");
        String resetPasswd = resourceBundle.getString("resetPasswd");
        String newPasswd = resourceBundle.getString("newPasswd");
        String confirmNewPasswd = resourceBundle.getString("confirmNewPasswd");
        String resetMyPasswd = resourceBundle.getString("resetMyPasswd");

        String secQuesTravel = resourceBundle.getString("secQuesTravel");
        String secQuesFictional = resourceBundle.getString("secQuesFictional");
        String secQuesChildhood = resourceBundle.getString("secQuesChildhood");

        // Áp dụng chuỗi vào giao diện người dùng
        txtForgotPasswd.setText(forgotPasswd);
        txtUserName.setPromptText(phoneNumOrEmail);
        comboBoxSecQuestion.setPromptText(selectSecQuestions);
        txtAnsw.setPromptText(answer);
        btnProceed.setText(proceed);
        btnBack.setText(back);
        txtResetPasswd.setText(resetPasswd);
        txtNewPasswd.setPromptText(newPasswd);
        txtConfirmPasswd.setPromptText(confirmNewPasswd);
        btnResetPasswd.setText(resetMyPasswd);

        comboBoxSecQuestion.getItems().addAll(secQuesTravel, secQuesFictional, secQuesChildhood);

    }

    /**
     * Thu nhỏ cửa sổ
     * @param event
     */
    @FXML
    public void minBtnAction(ActionEvent event){
        Stage stage = stageController.getStage(MainApp.forgotPasswdViewID);
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

    @FXML public void backToLoginButton(ActionEvent event) throws IOException {
        stageController.loadStage(MainApp.loginViewID,MainApp.loginViewRes, StageStyle.UNDECORATED);
        stageController.setStage(MainApp.loginViewID,MainApp.forgotPasswdViewID);
        stageController.getStage(MainApp.loginViewID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                model.disConnect();
                stageController.unloadStage(MainApp.EmojiSelectorID);
            }
        });
    }

    private boolean isInforInputValid() {
        return !comboBoxSecQuestion.getSelectionModel().isEmpty() &&
                !txtAnsw.getText().isEmpty() &&
                !txtUserName.getText().isEmpty();
    }
    private boolean isPasswdInputValid() {
        return !txtNewPasswd.getText().isEmpty() &&
                !txtConfirmPasswd.getText().isEmpty();
    }
    private boolean arePasswordsMatching() {
        String password = txtNewPasswd.getText();
        String confirmPassword = txtConfirmPasswd.getText();

        return password.equals(confirmPassword);
    }
    private void showPasswordMismatchAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(passwdMismatch);
        alert.showAndWait();
    }

    public void showSecurityInforError(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(messError);
        if(error.equalsIgnoreCase("Account does not exist!")) {
            error = notExist;
        }
        else if(error.equalsIgnoreCase("Security question do not match!")) {
            error = notMatch;
        }
        else {
            error = wrongAnswer;
        }

        alert.setContentText(inforFail + "\n" + error);
        System.out.println(error);
        alert.show();
    }

    @FXML public void onProceedButton(ActionEvent event) throws IOException {
        if (isInforInputValid()) {
            String username = txtUserName.getText();
            String secQuestion;
            int selectedIndex = comboBoxSecQuestion.getSelectionModel().getSelectedIndex();

            if (selectedIndex == 0) {
                secQuestion = "secQuesTravel";
            } else if (selectedIndex == 1) {
                secQuestion = "secQuesFictional";
            } else {
                secQuestion = "secQuesChildhood";
            }

            String answer = txtAnsw.getText();
            StringBuffer result = new StringBuffer();
            if (model.checkSecurityInfor(username, secQuestion, answer, result)) {
                forgotPasswdPane.setVisible(false);
                resetPasswdPane.setVisible(true);
            } else {
                showSecurityInforError(result.toString());
            }
        } else {
            handleEmptyTextField(txtUserName);
            handleEmptyTextField(txtAnsw);
            handleEmptyComboBox(comboBoxSecQuestion);
        }
    }

    private void handleEmptyTextField(TextField textField) {
        if (textField.getText().isEmpty()) {
            textField.getStyleClass().add("error-border");
        } else {
            textField.getStyleClass().remove("error-border");
        }
    }

    private void handleEmptyComboBox(ComboBox<String> comboBox) {
        if (comboBox.getValue() == null) {
            comboBox.getStyleClass().add("error-border");
        } else {
            comboBox.getStyleClass().remove("error-border");
        }
    }

    @FXML public void onResetPasswdButton(ActionEvent event) throws IOException {
        if (isPasswdInputValid()) {
            if (arePasswordsMatching()) {
                String username = txtUserName.getText();
                String passwd = txtNewPasswd.getText();
                StringBuffer result = new StringBuffer();

                if (model.changePasswd(username, passwd, result)) {
                   btnBack.fire();
                   showChangePasswdSuccess();
                } else {
                    showChangePasswdError();
                    System.out.println(result);
                }
            } else {
                showPasswordMismatchAlert();
            }
        } else {
            handleEmptyTextField(txtNewPasswd);
            handleEmptyTextField(txtConfirmPasswd);
        }
    }

    public void showChangePasswdSuccess() {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(changePasswdDone);
        successAlert.setHeaderText(passwdUpdateDone);
        successAlert.setContentText(congratsUpdate);

        successAlert.showAndWait();
    }
    public void showChangePasswdError() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(messError);
        error = sysError;
        alert.setContentText(error);
        System.out.println(error);
        alert.show();
    }

}
