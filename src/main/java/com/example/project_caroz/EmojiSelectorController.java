package com.example.project_caroz;

import com.example.project_caroz.emoji.Emoji;
import com.example.project_caroz.emoji.EmojiDisplayer;
import com.example.project_caroz.emoji.EmojiHandler;
import com.example.project_caroz.stage.ControlledStage;
import com.example.project_caroz.stage.StageController;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @Title: EmojiSelectorController.java
 * @Description: Bộ chọn Emoji
 */
public class EmojiSelectorController  implements ControlledStage, Initializable {
    @FXML
    private ScrollPane showScrollPane;
    @FXML
    private FlowPane showFlowPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private ScrollPane searchScrollPane;
    @FXML
    private FlowPane searchFlowPane;

    // Đối tượng EmojiSelectorController
    private static EmojiSelectorController instance;

    private StageController myController;

    private MainChatViewController chatController = MainChatViewController.getInstance();

    public EmojiSelectorController() {
        instance = this;
    }

    public static EmojiSelectorController getInstance() {
        return instance;
    }

    @Override
    public void setStageController(StageController stageController) {
        this.myController = stageController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        // Đặt biểu tượng
        // setIcon("images/icon_emoji.png");
        // Cài đặt giao diện hiển thị emoji
        showScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        showFlowPane.setHgap(5);
        showFlowPane.setVgap(5);
        // Cài đặt giao diện kết quả tìm kiếm
        searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        searchFlowPane.setHgap(5);
        searchFlowPane.setVgap(5);
        // Trình theo dõi hộp văn bản tìm kiếm emoji
        searchTextField.textProperty().addListener(x -> {
            String text = searchTextField.getText();
            if (text.isEmpty() || text.length() < 2) {
                searchFlowPane.getChildren().clear();
                searchScrollPane.setVisible(false);
                showScrollPane.setVisible(true);
            } else {
                showScrollPane.setVisible(false);
                searchScrollPane.setVisible(true);
                List<Emoji> results = EmojiHandler.getInstance().search(text);
                searchFlowPane.getChildren().clear();
                results.forEach(emoji -> searchFlowPane.getChildren().add(addEmojiNodeListener(emoji)));
            }
        });
        // Khởi tạo
        init();

    }

    /**
     * Đóng cửa sổ
     * @throws IOException
     */
    @FXML public void closeImgViewPressedAction() {
//		closeLocalStage();
    }

    /**
     * Khởi tạo và làm mới hiển thị emoji
     */
    private void init() {
        Platform.runLater(() -> {
            showFlowPane.getChildren().clear();
            EmojiHandler.getInstance().getEmojiMap().values()
                    .forEach(emoji -> showFlowPane.getChildren().add(addEmojiNodeListener(emoji)));
            showScrollPane.requestFocus();
        });
    }

    /**
     * Tạo nút emoji dạng stackpane và thêm bộ lắng nghe sự kiện cho nó.
     *
     * @param emoji Emoji cần tạo nút cho
     * @return Nút emoji stackpane
     */
    private Node addEmojiNodeListener(Emoji emoji) {
        // Kiểm tra xem có cần thiết lập con trỏ không
        Node stackPane = EmojiDisplayer.createEmojiNode(emoji, 32, 3);
        if (stackPane instanceof StackPane) {
            // Thiết lập con trỏ là hình tay
            stackPane.setCursor(Cursor.HAND);
            ScaleTransition st = new ScaleTransition(Duration.millis(90), stackPane);
            // Thiết lập gợi ý
            Tooltip tooltip = new Tooltip(emoji.getShortname());
            Tooltip.install(stackPane, tooltip);
            // Thiết lập sự kiện khi con trỏ được đưa vào
            stackPane.setOnMouseEntered(e -> {
                // stackPane.setStyle("-fx-background-color: #a6a6a6;
                // -fx-background-radius: 3;");
                stackPane.setEffect(new DropShadow());
                st.setToX(1.2);
                st.setToY(1.2);
                st.playFromStart();
                if (searchTextField.getText().isEmpty())
                    searchTextField.setPromptText(emoji.getShortname());
            });
            // Thiết lập sự kiện khi con trỏ rời đi
            stackPane.setOnMouseExited(e -> {
                // stackPane.setStyle("");
                stackPane.setEffect(null);
                st.setToX(1.);
                st.setToY(1.);
                st.playFromStart();
            });
            // Thiết lập sự kiện khi con trỏ được nhấp
            stackPane.setOnMouseClicked(e -> {
                // Lấy tên ngắn của emoji
                String shortname = emoji.getShortname();
                chatController.getMessageBoxTextArea().appendText(shortname);
            });
        }
        return stackPane;
    }

}
