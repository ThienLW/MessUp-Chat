package com.example.project_caroz;

import bean.ClientUser;
import bean.Message;
import com.example.project_caroz.emoji.EmojiDisplayer;
import com.example.project_caroz.model.ClientModel;
import com.example.project_caroz.stage.ControlledStage;
import com.example.project_caroz.stage.StageController;
import com.google.gson.Gson;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import utils.NotificationUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.Constants.*;

public class MainChatViewController implements ControlledStage, Initializable {

    @FXML
    private Label onlineUsersLab;
    @FXML
    public Button btnEmoji;
    @FXML
    public TextArea textSend;
    @FXML
    public Button btnSend;
    @FXML
    private Button btnMicro;
    @FXML
    public ListView chatWindow;
    @FXML
    public ListView userGroup;
    @FXML
    public Label labUserName;
    @FXML
    public Label labPartnerName;
    @FXML
    public Label labUserCounter;
    @FXML
    private ImageView userImageView;
    @FXML
    private ImageView partnerImageView;

    @FXML
    public ComboBox<String> comboBoxStatus;

    @FXML
    public Circle circleStatus;

    private Gson gson = new Gson();
    private StageController stageController;
    private ClientModel model;
    private static MainChatViewController instance;
    private boolean pattern = GROUP; //chat model
    private String selectUser = "Global Chat";
    private static String thisUser;
    private static String thisUserFirstName;
    private static String thisUserSurname;
    private ObservableList<ClientUser> uselist;
    private ObservableList<Message> chatRecorder;
    private static String selectingUser = "Global Chat";
    private static boolean isEmojiStageOpen = false;



    public MainChatViewController() {
        super();
        instance = this;
    }

    public static MainChatViewController getInstance() {
        return instance;
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }

    // Internationalize
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", new Locale(LoginController.languageSelected));

    private String confirmExit = resourceBundle.getString("confirmExit");
    private String sureExit = resourceBundle.getString("sureExit");
    private String select = resourceBundle.getString("select");
    private String mess = resourceBundle.getString("mess");
    private String messFrom = resourceBundle.getString("messFrom");
    private String online = resourceBundle.getString("online");
    private String away = resourceBundle.getString("away");
    private String busy = resourceBundle.getString("busy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Dùng resourceBundle để lấy các chuỗi theo ngôn ngữ
        String onlineUsers = resourceBundle.getString("onlineUsers");
        comboBoxStatus.getItems().addAll(online, away, busy);
        // Đặt mục đầu tiên là mục mặc định
        comboBoxStatus.setValue(online); // Đặt giá trị mặc định

        // Áp dụng chuỗi vào giao diện người dùng
        onlineUsersLab.setText(onlineUsers);

        model = ClientModel.getInstance();
        uselist = model.getUserList();
        chatRecorder = model.getChatRecorder();
        userGroup.setItems(uselist);
        chatWindow.setItems(chatRecorder);
        thisUser = model.getThisUser();
        thisUserFirstName = model.getThisUserFirstName();
        thisUserSurname = model.getThisUserSurname();
        labUserName.setText(thisUserFirstName + " " + thisUserSurname);

        // Add an event handler to the ComboBox
        comboBoxStatus.setOnAction((ActionEvent event) -> {
            int selectedStatus = comboBoxStatus.getSelectionModel().getSelectedIndex();
            if (selectedStatus==0) {
                circleStatus.setFill(Color.valueOf("#8EFF1F"));
//                HashMap map = new HashMap();
//                map.put(COMMAND, COM_CHANGESTATUS);
//                map.put(STATUS, "online");
//                model.sentMessage(gson.toJson(map));
            } else if (selectedStatus==1) {
                circleStatus.setFill(Color.valueOf("#FFD700"));
//                HashMap map = new HashMap();
//                map.put(COMMAND, COM_CHANGESTATUS);
//                map.put(STATUS, "away");
//                model.sentMessage(gson.toJson(map));
            } else if (selectedStatus==2) {
                circleStatus.setFill(Color.valueOf("#FF6969"));
//                HashMap map = new HashMap();
//                map.put(COMMAND, COM_CHANGESTATUS);
//                map.put(STATUS, "busy");
//                model.sentMessage(gson.toJson(map));
            }
            else {
                circleStatus.setFill(Color.valueOf("#A9A9A9"));
            }
        });

        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Khi nhấn nút Send, đóng cửa sổ Emoji selector (nếu nó mở)
                if(isEmojiStageOpen){
                    stageController.getStage(MainApp.EmojiSelectorID).close();
                    isEmojiStageOpen = false;
                }

                // Xử lý sự kiện nút Send
                if(!textSend.getText().trim().isEmpty()){
                    if (pattern == GROUP) {
                        HashMap map = new HashMap();
                        map.put(COMMAND, COM_CHATALL);
                        map.put(CONTENT, textSend.getText().trim());
                        model.sentMessage(gson.toJson(map));
                    } else if (pattern == SINGLE) {
                        HashMap map = new HashMap();
                        map.put(COMMAND, COM_CHATWITH);
                        map.put(RECEIVER, selectUser);
                        map.put(SPEAKER, model.getThisUser());
                        map.put(CONTENT, textSend.getText().trim());
                        model.sentMessage(gson.toJson(map));
                    }
                }
                textSend.clear();
            }
        });

        // Tạo hiệu ứng phóng to dần cho btnSend
        ScaleTransition scaleTransitionSend = new ScaleTransition(Duration.millis(200), btnSend);
        scaleTransitionSend.setFromX(1.0);
        scaleTransitionSend.setFromY(1.0);
        scaleTransitionSend.setToX(1.2);
        scaleTransitionSend.setToY(1.2);

        btnSend.setOnMouseEntered(event -> {
            scaleTransitionSend.setRate(1.0);
            scaleTransitionSend.play();
        });

        btnSend.setOnMouseExited(event -> {
            scaleTransitionSend.setRate(-1.0);
            scaleTransitionSend.play();
        });

        // Tạo hiệu ứng phóng to dần cho btnMicro
        ScaleTransition scaleTransitionMicro = new ScaleTransition(Duration.millis(200), btnMicro);
        scaleTransitionMicro.setFromX(1.0);
        scaleTransitionMicro.setFromY(1.0);
        scaleTransitionMicro.setToX(1.2);
        scaleTransitionMicro.setToY(1.2);

        btnMicro.setOnMouseEntered(event -> {
            scaleTransitionMicro.setRate(1.0);
            scaleTransitionMicro.play();
        });

        btnMicro.setOnMouseExited(event -> {
            scaleTransitionMicro.setRate(-1.0);
            scaleTransitionMicro.play();
        });

        // Tạo hiệu ứng phóng to dần cho userImageView
        ScaleTransition scaleTransitionUserProfile = new ScaleTransition(Duration.millis(200), userImageView);
        scaleTransitionUserProfile.setFromX(1.0);
        scaleTransitionUserProfile.setFromY(1.0);
        scaleTransitionUserProfile.setToX(1.2);
        scaleTransitionUserProfile.setToY(1.2);

        userImageView.setOnMouseEntered(event -> {
            scaleTransitionUserProfile.setRate(1.0);
            scaleTransitionUserProfile.play();
        });

        userImageView.setOnMouseExited(event -> {
            scaleTransitionUserProfile.setRate(-1.0);
            scaleTransitionUserProfile.play();
        });

        // Enter vẫn gửi text đi được, shift enter thì xuống hàng
        textSend.setOnKeyPressed(event -> {
            if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
                textSend.appendText("\n"); // Thêm một dòng mới vào văn bản
            } else if (event.getCode() == KeyCode.ENTER) {
                btnSend.fire(); // Kích hoạt hành động của nút "Send"
                event.consume(); // Đánh dấu sự kiện đã được xử lý, tránh việc Enter hiển thị trong ô nhập tin nhắn
            }
        });

        // Đảm bảo rằng danh sách người dùng (userGroup) đã được khởi tạo và có phần tử
        if (userGroup.getItems().size() > 0) {
            // Đặt phần tử đầu tiên là mặc định được chọn
            userGroup.getSelectionModel().select(0);
        }
        // Chat with...
        userGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ClientUser user = (ClientUser) newValue;
            System.out.println("You are chatting with " + user.getUserName());
            selectingUser = user.getUserName();
            if (user.getUserName().equals("Global Chat")) {
                pattern = GROUP;
                if (!selectUser.equals("Global Chat")) {
                    model.setChatUser("Global Chat");
                    selectUser = "Global Chat";
                    partnerImageView.setImage(new Image(getClass().getResource("/img/global-chat-icon.png").toExternalForm()));
                    Platform.runLater(() -> labPartnerName.setText("MessUp Community Global Chat"));
//                    labPartnerName.setText("MessUp Community Global Chat");
                }
            } else {
                pattern = SINGLE;
                if (!selectUser.equals(user.getUserName())) {
                    model.setChatUser(user.getUserName());
                    selectUser = user.getUserName();
                    String chatWith = user.getFirstName() + " " + user.getSurname();
                    partnerImageView.setImage(new Image(getClass().getResource("/img/default-profile-img.png").toExternalForm()));
                    labPartnerName.setText(chatWith);
                }
            }
        });

        chatWindow.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ChatCell();
            }
        });

        userGroup.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new UserCell();
            }
        });

        // Bật chế độ tự động xuống hàng cho Text Send
        textSend.setWrapText(true);
        // Đặt một Text ẩn để đo độ dài của nội dung
        javafx.scene.text.Text text = new javafx.scene.text.Text();
        text.textProperty().bind(textSend.textProperty());

        // Open User Profile View
        userImageView.setOnMouseClicked(event -> {
            onUserProfileBtnClicked(); // Thay "onUserProfileBtnClicked()" bằng phương thức bạn muốn gọi khi ImageView được nhấp
        });

    }

    /**
     * Thu nhỏ cửa sổ
     * @param event
     */
    @FXML public void minBtnAction(ActionEvent event){
        Stage stage = stageController.getStage(MainApp.mainViewID);
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
    public void onEmojiBtnClicked() {
        // Gán điều kiện để chỉ tối đa 1 Emoji selector được mở
        if(!isEmojiStageOpen){
            stageController.loadStage(MainApp.EmojiSelectorID, MainApp.EmojiSelectorRes);
            stageController.setStage(MainApp.EmojiSelectorID);
            isEmojiStageOpen = true;
        }
    }

    public void onUserProfileBtnClicked() {
        stageController.loadChildStage(MainApp.UserProfileID, MainApp.UserProfileRes, Modality.APPLICATION_MODAL, StageStyle.UNDECORATED);
        stageController.setStage(MainApp.UserProfileID);
    }

    public TextArea getMessageBoxTextArea() {
        return textSend;
    }

    public Label getLabUserCounter() {
        return labUserCounter;
    }

    // User Cell
    public static class UserCell extends ListCell<ClientUser> {
        @Override
        protected void updateItem(ClientUser item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (item != null) {
                        HBox hbox = new HBox(10);
                        hbox.setStyle("-fx-font-family: Verdana;");
                        ImageView imageHead = new ImageView(new Image(getClass().getResource("/img/default-profile-img.png").toExternalForm()));
                        imageHead.setFitHeight(45);
                        imageHead.setFitWidth(45);
                        ClientUser user = (ClientUser) item;
                        ImageView imageStatus;

                        if(user.getUserName().equals("Global Chat")){
                            imageHead = new ImageView(new Image(getClass().getResource("/img/global-chat-icon.png").toExternalForm()));
                            imageHead.setFitHeight(45);
                            imageHead.setFitWidth(45);
                            imageStatus = new ImageView(new Image(getClass().getResource("/img/online.png").toExternalForm()));
                        } else if(user.isNotify()==true){
                            imageStatus = new ImageView(new Image(getClass().getResource("/img/message.png").toExternalForm()));
                            if (instance.stageController.getStage(MainApp.mainViewID).isIconified()){
                                NotificationUtils.showInformation(instance.mess, instance.messFrom+" "+user.getFirstName()+"!", instance.stageController);
                            }
                        }else {
                            if(user.getStatus().equals("online")){
                                imageStatus = new ImageView(new Image(getClass().getResource("/img/online.png").toExternalForm()));
                            }else if(user.getStatus().equals("away")){
                                imageStatus = new ImageView(new Image(getClass().getResource("/img/away.png").toExternalForm()));
                            }else if(user.getStatus().equals("busy")){
                                imageStatus = new ImageView(new Image(getClass().getResource("/img/busy.png").toExternalForm()));
                            }else{
                                imageStatus = new ImageView(new Image(getClass().getResource("/img/offline.png").toExternalForm()));
                            }
                        }
                        imageStatus.setFitWidth(7);
                        imageStatus.setFitHeight(7);
                        Label label = new Label();
                        if(user.getUserName().equals("Global Chat")){
                            label = new Label(user.getUserName());
                        }
                        else {
                            label = new Label(user.getFirstName() + " " + user.getSurname());
                        }


                        label.setStyle("-fx-font-size: 14px;");
                        hbox.setAlignment(Pos.CENTER_LEFT); // Căn giữa các thành phần theo chiều ngang
                        hbox.getChildren().addAll(imageHead, label,imageStatus);
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });
        }
    }

    // Chat Cell
    public static class ChatCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(() -> {
                if (item != null) {
                    HBox chatCell = new HBox();
                    chatCell.setStyle("-fx-font-family: Verdana;");
                    chatCell.setSpacing(10);
                    ImageView userAvatar = new ImageView(new Image(getClass().getResource("/img/default-profile-img.png").toExternalForm()));
                    userAvatar.setFitHeight(40);
                    userAvatar.setFitWidth(40);

                    VBox box = new VBox();
                    HBox hbox = new HBox();
                    TextFlow txtContent = new TextFlow(EmojiDisplayer.createEmojiAndTextNode(item.getContent()));
                    txtContent.setMaxWidth(630);
                    txtContent.getStyleClass().add("text-style");
                    Label labUser = new Label();
                    labUser.getStyleClass().add("user-label");
                    Label labTimer = new Label(item.getTimer());
                    labTimer.getStyleClass().add("timer-style");

                    if (item.getSpeaker().equals(thisUser)) {
                        labUser.setText("You");
                        hbox.getChildren().addAll(labUser);
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                        txtContent.setTextAlignment(TextAlignment.RIGHT);
                        box.getStyleClass().add("sender-box");
                        setAlignment(Pos.CENTER_RIGHT);
                        labTimer.setTextAlignment(TextAlignment.LEFT);
                        if (selectingUser.equals("Global Chat")){
                            box.getChildren().addAll(hbox, txtContent, labTimer);
                        }else {
                            box.getChildren().addAll(txtContent, labTimer);
                        }
                        box.setMaxWidth(USE_PREF_SIZE);
                        chatCell.getChildren().addAll(box, userAvatar);
                        chatCell.setAlignment(Pos.TOP_RIGHT);
                    } else {
                        labUser.setText(item.getSpeakerFirstname());
                        ImageView image = new ImageView(new Image(getClass().getResource("/img/default-profile-img.png").toExternalForm()));
                        image.setFitHeight(20);
                        image.setFitWidth(20);
                        hbox.getChildren().addAll(labUser);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        box.getStyleClass().add("receiver-box");
                        labTimer.setTextAlignment(TextAlignment.LEFT);
                        if (selectingUser.equals("Global Chat")){
                            box.getChildren().addAll(hbox, txtContent, labTimer);
                        }else {
                            box.getChildren().addAll(txtContent, labTimer);
                        }
                        box.setMaxWidth(USE_PREF_SIZE);
                        chatCell.getChildren().addAll(userAvatar, box);
                        chatCell.setAlignment(Pos.TOP_LEFT);
                    }

                    setGraphic(chatCell);
                } else {
                    setGraphic(null);
                }
            });
        }
    }



}
