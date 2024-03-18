package utils;

import com.example.project_caroz.MainApp;
import com.example.project_caroz.stage.StageController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;
import javafx.util.Duration;

public class NotificationUtils {

    public static void showInformation(String title, String text, StageController stageController) {
        Stage primaryStage = stageController.getStage(MainApp.mainViewID);
        Notifications.create()
                .title(title)
                .text(text)
                .graphic(new ImageView("/MessUp-icon.png")) // Đặt hình ảnh từ đường dẫn
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(3))
                .darkStyle()
//                .styleClass("custom-notification")
                .onAction(event -> {
                    if (primaryStage != null) {
                        primaryStage.setIconified(false); // Phục hồi cửa sổ nếu nó đang minimize
                        primaryStage.toFront(); // Đưa cửa sổ lên trên cùng
                    }
                })
                .show();
    }

    public static void showWarning(String title, String text) {
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(text)
                .graphic(null)
                .hideAfter(Duration.millis(4000))
                .position(Pos.BOTTOM_LEFT);
        notificationBuilder.showWarning();
    }

    public static void showError(String title, String text) {
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(text)
                .graphic(null)
                .hideAfter(Duration.millis(4000))
                .position(Pos.BOTTOM_LEFT);
        notificationBuilder.showError();
    }

    // Bạn có thể thêm các phương thức khác cho các loại thông báo khác.

    public static void showCustom(String title, String text, Pos position) {
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(text)
                .graphic(null)
                .hideAfter(Duration.millis(4000))
                .position(position);
        notificationBuilder.show();
    }
}

