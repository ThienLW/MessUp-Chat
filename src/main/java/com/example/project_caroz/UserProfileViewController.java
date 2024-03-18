package com.example.project_caroz;

import com.example.project_caroz.model.ClientModel;
import com.example.project_caroz.stage.ControlledStage;
import com.example.project_caroz.stage.StageController;
import com.google.gson.Gson;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class UserProfileViewController implements ControlledStage, Initializable {

    private static UserProfileViewController instance;
    private StageController stageController;
    private Gson gson = new Gson();
    ClientModel model;

    @FXML
    private ImageView userProfileImg;


    public UserProfileViewController() {
        super();
        instance = this;
    }

    public static UserProfileViewController getInstance() {
        return instance;
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
        model = ClientModel.getInstance();
    }

    // Internationalize
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", new Locale(LoginController.languageSelected));
    private String confirmExit = resourceBundle.getString("confirmExit");
    private String sureExit = resourceBundle.getString("sureExit");
    private String select = resourceBundle.getString("select");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Đóng cửa sổ và thoát chương trình
     * @param event
     */
    @FXML public void closeBtnAction(ActionEvent event){
        event.consume(); // Ngăn cửa sổ đóng ngay lập tức
        Stage stage = stageController.getStage(MainApp.UserProfileID);
        stage.close();
    }

    @FXML
    public void onLogoutBtn(ActionEvent event) throws IOException {
//        Stage userProfileStage = stageController.getStage(MainApp.UserProfileID);
//        Stage mainChatStage = stageController.getStage(MainApp.mainViewID);
//
//        mainChatStage.close();
//        userProfileStage.close();
//
////        HashMap map = new HashMap();
////        map.put(COMMAND, COM_LOGOUT);
////        model.sentMessage(gson.toJson(map));
//
//        // Tải lại trang Login
//        stageController.loadStage(MainApp.loginViewID, MainApp.loginViewRes, StageStyle.UNDECORATED);
//        stageController.setStage(MainApp.loginViewID);

        System.out.println("On Button Logout!!");

    }

}
