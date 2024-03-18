package com.example.project_caroz.emoji;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Queue;

/**
 *
 * Trình hiển thị Emoji:
 * ① Tạo nút hình ảnh emoji cho tập hợp thực thể emoji,
 * ② Tách chuỗi chứa emoji và tạo nút hình ảnh emoji và nút văn bản.
 * @Title: EmojiParser.java
 */
public class EmojiDisplayer {
    /**
     * Phân tách chuỗi thành các nút hình ảnh emoji và nút văn bản
     * @param input Chuỗi đầu vào
     * @return
     */
    public static Node[] createEmojiAndTextNode(String input) {
        Queue<Object> queue = EmojiHandler.getInstance().toEmojiAndText(input);
        Node[] nodes = new Node[queue.size()];
        int i = 0;
        while (!queue.isEmpty()) {
            Object ob = queue.poll();
            if (ob instanceof String) {
                String text = (String) ob;
                nodes[i++] = createTextNode(text);
            } else if (ob instanceof Emoji) {
                Emoji emoji = (Emoji) ob;
                nodes[i++] = createEmojiNode(emoji, 24, 10);
            }
        }
        return nodes;
    }

    /**
     * Tạo nút hình ảnh emoji
     *
     * @param emoji Emoji
     * @param size Kích thước hiển thị hình ảnh
     * @param pad Khoảng cách giữa các hình ảnh
     * @return
     */
    public static Node createEmojiNode(Emoji emoji, int size, int pad) {
        // Đặt icon cười vào StackPane
        StackPane stackPane = new StackPane();
        stackPane.setMaxSize(size, size);
        stackPane.setPrefSize(size, size);
        stackPane.setMinSize(size, size);
        stackPane.setPadding(new Insets(pad));
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
        stackPane.getChildren().add(imageView);
        return stackPane;
    }

    /**
     * Tạo nút văn bản
     *
     * @param text Văn bản
     * @return
     */
    private static Node createTextNode(String text) {
        Text textNode = new Text(text);
        textNode.setFont(Font.font("Arial", 15));// Kiểu và kích thước phông chữ
        return textNode;
    }

    /**
     * Lấy đường dẫn hình ảnh emoji dựa trên hex của emoji
     *
     * @param hexStr
     * @return
     */
    private static String getEmojiImagePath(String hexStr) {
        String imagePath = "/emoji_png/" + hexStr + ".png";
        URL url = EmojiDisplayer.class.getResource(imagePath);
        return url.toExternalForm();
    }
}
