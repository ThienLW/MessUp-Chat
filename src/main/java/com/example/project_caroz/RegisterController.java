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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController implements ControlledStage, Initializable {

    @FXML
    private Button btnClose;
    @FXML
    private Button btnMinimize;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_regis;
    @FXML
    private ComboBox<String> comboBoxGender;
    @FXML
    private ComboBox<String> comboBoxSecQuestion;
    @FXML
    private DatePicker dateOfBirthPicker;
    @FXML
    private TextField txtAnsw;
    @FXML
    private PasswordField txtConfirmPasswd;
    @FXML
    private TextField txtFirsrtName;
    @FXML
    private PasswordField txtPasswd;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtUserName;
    @FXML
    private Label txtUserRegis;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label secQuesLabel;
    @FXML
    private Label errorMessage;
    private StageController stageController;
    ClientModel model;

    /**
     * Thu nhỏ cửa sổ
     * @param event
     */
    @FXML public void minBtnAction(ActionEvent event){
        Stage stage = stageController.getStage(MainApp.registerViewID);
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
    public void changeLoginSceneButton(ActionEvent event) throws IOException {
        stageController.loadStage(MainApp.loginViewID,MainApp.loginViewRes, StageStyle.UNDECORATED);
        stageController.setStage(MainApp.loginViewID,MainApp.registerViewID);
        stageController.getStage(MainApp.loginViewID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                model.disConnect();
                stageController.unloadStage(MainApp.EmojiSelectorID);
            }
        });
    }

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
    private String fullFillInfor = resourceBundle.getString("fullFillInfor");
    private String invalidEmail = resourceBundle.getString("invalidEmail");
    private String messError = resourceBundle.getString("messError");
    private String regisFail = resourceBundle.getString("regisFail");
    private String regisDone = resourceBundle.getString("regisDone");
    private String welcomeMess = resourceBundle.getString("welcomeMess");
    private String congrats = resourceBundle.getString("congrats");
    private String registed = resourceBundle.getString("registed");
    private String errorAddDB = resourceBundle.getString("errorAddDB");

    private String confirmExit = resourceBundle.getString("confirmExit");
    private String sureExit = resourceBundle.getString("sureExit");
    private String select = resourceBundle.getString("select");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String userRegis = resourceBundle.getString("userRegis");
        String firstName = resourceBundle.getString("firstName");
        String surname = resourceBundle.getString("surname");
        String phoneNumOrEmail = resourceBundle.getString("phoneNumOrEmail");
        String password = resourceBundle.getString("password");
        String confirmPasswd = resourceBundle.getString("confirmPasswd");
        String birthDay = resourceBundle.getString("birthDay");
        String gender = resourceBundle.getString("gender");
        String selectGender = resourceBundle.getString("selectGender");
        String secQuestions = resourceBundle.getString("secQuestions");
        String selectSecQuestions = resourceBundle.getString("selectSecQuestions");
        String answer = resourceBundle.getString("answer");
        String registerBtn = resourceBundle.getString("registerBtn");
        String cancel = resourceBundle.getString("cancel");
        String selectGenderMale = resourceBundle.getString("selectGenderMale");
        String selectGenderFemale = resourceBundle.getString("selectGenderFemale");
        String selectGenderCustom = resourceBundle.getString("selectGenderCustom");
        String secQuesTravel = resourceBundle.getString("secQuesTravel");
        String secQuesFictional = resourceBundle.getString("secQuesFictional");
        String secQuesChildhood = resourceBundle.getString("secQuesChildhood");

        txtUserRegis.setText(userRegis);
        txtFirsrtName.setPromptText(firstName);
        txtSurname.setPromptText(surname);
        txtUserName.setPromptText(phoneNumOrEmail);
        txtPasswd.setPromptText(password);
        txtConfirmPasswd.setPromptText(confirmPasswd);
        birthdayLabel.setText(birthDay);
        genderLabel.setText(gender);
        comboBoxGender.setPromptText(selectGender);
        secQuesLabel.setText(secQuestions);
        comboBoxSecQuestion.setPromptText(selectSecQuestions);
        txtAnsw.setPromptText(answer);
        btn_regis.setText(registerBtn);
        btn_cancel.setText(cancel);
        comboBoxGender.getItems().addAll(selectGenderMale, selectGenderFemale, selectGenderCustom);
        comboBoxSecQuestion.getItems().addAll(secQuesTravel, secQuesFictional, secQuesChildhood);

    }

    // Check email
    public static boolean isValidEmail(String input) {
        // Biểu thức chính quy để kiểm tra email
        // Định dạng regex cho email - Chỉ chấp nhận gmail và CTU mail
        String regex = "^[A-Za-z0-9+_.-]+@(gmail\\.com|student\\.ctu\\.edu\\.vn|ctu\\.edu\\.vn)$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }
    private boolean isInputValid() {
        return !comboBoxGender.getSelectionModel().isEmpty() &&
                !comboBoxSecQuestion.getSelectionModel().isEmpty() &&
                dateOfBirthPicker.getValue() != null &&
                !txtAnsw.getText().isEmpty() &&
                !txtConfirmPasswd.getText().isEmpty() &&
                !txtFirsrtName.getText().isEmpty() &&
                !txtPasswd.getText().isEmpty() &&
                !txtSurname.getText().isEmpty() &&
                !txtUserName.getText().isEmpty();
    }
    private boolean arePasswordsMatching() {
        String password = txtPasswd.getText();
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

    public void signUp(ActionEvent actionEvent) {
        StringBuffer result = new StringBuffer();
        if(isInputValid()){
            if(isValidEmail(txtUserName.getText().toString())){
                if(arePasswordsMatching()){
                    String username = txtUserName.getText().toString();
                    String passwd = txtPasswd.getText().toString();
                    String firstName = txtFirsrtName.getText().toString();
                    String surname = txtSurname.getText().toString();

                    // Tạo đối tượng StringConverter sử dụng DateTimeFormatter
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    StringConverter converter = new LocalDateStringConverter(dateFormatter, null);
                    // Thiết lập StringConverter cho DatePicker
                    dateOfBirthPicker.setConverter(converter);
                    // Lấy giá trị từ DatePicker và định dạng nó thành "dd/MM/yyyy"
                    LocalDate selectedDate = dateOfBirthPicker.getValue();
                    String birthDay = dateFormatter.format(selectedDate);

                    int genderIndex = comboBoxGender.getSelectionModel().getSelectedIndex();
                    String gender;
                    if(genderIndex==0){
                        gender = "selectGenderMale";
                    } else if (genderIndex==1) {
                        gender = "selectGenderFemale";
                    }
                    else {
                        gender = "selectGenderCustom";
                    }

                    int selectedIndex = comboBoxSecQuestion.getSelectionModel().getSelectedIndex();
                    String secQuestion;
                    if(selectedIndex==0){
                        secQuestion = "secQuesTravel";
                    } else if (selectedIndex==1) {
                        secQuestion = "secQuesFictional";
                    }
                    else {
                        secQuestion = "secQuesChildhood";
                    }
                    //String secQuestion = comboBoxSecQuestion.getValue().toString();
                    String answer = txtAnsw.getText().toString();
                    String IP = "localhost";
                    if (model.CheckRegister(username,passwd,firstName,surname,birthDay,gender,secQuestion,answer,IP,result)) {
                        goToMain();
                        showRegisterSuccess();
                    } else {
                        showRegisterError(result.toString());
                    }
                }
                else {
                    errorMessage.setVisible(false);
                    showPasswordMismatchAlert();
                }
            }
            else {
                errorMessage.setText(invalidEmail);
                errorMessage.setVisible(true);
            }


        }
        else {
            errorMessage.setText(fullFillInfor);
            errorMessage.setVisible(true);
        }

    }

    public void goToMain() {
        stageController.loadStage(MainApp.mainViewID,MainApp.mainViewRes, StageStyle.UNDECORATED);
        stageController.setStage(MainApp.mainViewID,MainApp.registerViewID);
        stageController.getStage(MainApp.mainViewID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                model.disConnect();
                stageController.unloadStage(MainApp.EmojiSelectorID);
            }
        });
    }
    public void showRegisterError(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(messError);
        if(error.equalsIgnoreCase("This user is already registered!"))
            error = registed;
        else
            error = errorAddDB;
        alert.setContentText(regisFail + " " + error);
        System.out.println(error);
        alert.show();
    }
    public void showRegisterSuccess() {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(regisDone);
        successAlert.setHeaderText(welcomeMess);
        successAlert.setContentText(congrats);

        successAlert.showAndWait();
    }
}
