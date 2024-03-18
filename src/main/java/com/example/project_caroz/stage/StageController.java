package com.example.project_caroz.stage;

import com.example.project_caroz.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class StageController {
    private double xOffset = 0;
    private double yOffset = 0;
    // Tạo một Map riêng để lưu trữ các đối tượng Stage, được sử dụng để lưu trữ tất cả các đối tượng Stage
    private HashMap<String, Stage> stages = new HashMap<String, Stage>();

    /**
     * Đặt Stage đã nạp vào Map để quản lý
     *
     * @param name  Tên của Stage
     * @param stage Đối tượng Stage
     */
    public void addStage(String name, Stage stage) {
        stages.put(name, stage);
    }

    /**
     * Lấy đối tượng Stage thông qua tên của Stage
     *
     * @param name Tên của Stage
     * @return Đối tượng Stage tương ứng
     */
    public Stage  getStage(String name) {
        return stages.get(name);
    }


    /**
     * Lưu đối tượng Stage chính, tại đây chỉ để cho trường hợp có thể sử dụng sau này, hiện tại chưa biết có cần không
     *
     * @param primaryStageName Tên của đối tượng Stage chính
     * @param primaryStage     Đối tượng Stage chính, được tạo ra bởi API của JavaFX trong phương thức Start()
     */
    public void setPrimaryStage(String primaryStageName, Stage primaryStage) {
        this.addStage(primaryStageName, primaryStage);
    }


    /**
     * Tải địa chỉ cửa sổ, yêu cầu tệp nguồn FXML thuộc cửa sổ độc lập và sử dụng Pane hoặc lớp con của nó để kế thừa
     *
     * @param name      Tệp FXML đã đăng ký của cửa sổ
     * @param resources Địa chỉ tài nguyên FXML
     * @param styles    Tham số biến đổi, thiết lập tài nguyên kiểu ban đầu được sử dụng cho việc khởi tạo
     * @return Có tải thành công hay không
     */
    public boolean loadStage(String name, String resources, StageStyle... styles) {
        try {
            // Bundle
            ResourceBundle messages = ResourceBundle.getBundle("messages", new Locale(LoginController.languageSelected));

            // Tải tệp nguồn FXML, với ngôn ngữ mặc định là English
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resources), messages);
            Pane tempPane = (Pane) loader.load();

            // Thông qua Loader lấy ViewCtr tương ứng với FXML và đưa StageController này vào ViewCtr
            ControlledStage controlledStage = loader.getController();
            controlledStage.setStageController(this);
            // Xây dựng Stage tương ứng
            Scene tempScene = new Scene(tempPane);
            Stage tempStage = new Stage();
            tempStage.setScene(tempScene);
            // Cấu hình initStyle
            for (StageStyle style : styles) {
                tempStage.initStyle(style);
            }

            // Đặt Stage đã cấu hình vào HashMap
            this.addStage(name, tempStage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadChildStage(String name, String resources, Modality modStyle, StageStyle... stageStyles) {
        try {
            // Bundle
            ResourceBundle messages = ResourceBundle.getBundle("messages", new Locale(LoginController.languageSelected));

            // Tải tệp nguồn FXML, với ngôn ngữ mặc định là English
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resources), messages);
            Pane tempPane = (Pane) loader.load();

            // Thông qua Loader lấy ViewCtr tương ứng với FXML và đưa StageController này vào ViewCtr
            ControlledStage controlledStage = loader.getController();
            controlledStage.setStageController(this);
            // Xây dựng Stage tương ứng
            Scene tempScene = new Scene(tempPane);
            Stage tempStage = new Stage();
            tempStage.setScene(tempScene);
            // Cấu hình initStyle
            tempStage.initModality(modStyle);
            for (StageStyle style : stageStyles) {
                tempStage.initStyle(style);
            }

            // Đặt Stage đã cấu hình vào HashMap
            this.addStage(name, tempStage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Hiển thị Stage mà không ẩn bất kỳ Stage nào
     *
     * @param name Tên của cửa sổ cần hiển thị
     * @return true nếu hiển thị thành công
     */
    public boolean setStage(String name) {
        // Tải biểu tượng icon
        Image appIcon = new Image(getClass().getResourceAsStream("/MessUp-icon.png"));
        // Đặt biểu tượng cho cửa sổ ứng dụng
        this.getStage(name).getIcons().add(appIcon);

        // Kéo cửa sổ
        this.getStage(name).getScene().setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        this.getStage(name).getScene().setOnMouseDragged(event -> {
            this.getStage(name).setX(event.getScreenX() - xOffset);
            this.getStage(name).setY(event.getScreenY() - yOffset);
        });

//        this.getStage(name).getScene().getStylesheets().add(getClass().getResource("/windowStyle.css").toExternalForm());

        this.getStage(name).show();
        return true;
    }


    /**
     * Hiển thị Stage và ẩn cửa sổ tương ứng
     *
     * @param show  Cửa sổ cần hiển thị
     * @param close Cửa sổ cần đóng
     * @return
     */
    public boolean setStage(String show, String close) {
        getStage(close).close();
        setStage(show);
        return true;
    }


    /**
     * Xóa đối tượng Stage đã tải khỏi Map
     *
     * @param name Tên tệp fxml cần xóa
     * @return true nếu xóa thành công
     */
    public boolean unloadStage(String name) {
        if (stages.remove(name) == null) {
//            System.out.println("Cửa sổ không tồn tại, vui lòng kiểm tra tên");
            return false;
        } else {
            stages.remove(name);
//            System.out.println("Cửa sổ không tồn tại, vui lòng kiểm tra tên");
            return true;
        }
    }
}
