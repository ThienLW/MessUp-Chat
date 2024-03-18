package com.example.project_caroz.emoji;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @Title: Emoji.java
 * @Description: Đối tượng Emoji, được sử dụng để lưu trữ các thuộc tính được nhập từ emoji.json
 */
public class Emoji {

    //Tên viết tắt của emoji
    private String shortname;
    //Mã Unicode của emoji (cần phải chuyển đổi)
    private String unicode;
    //Giá trị hex của emoji (biểu diễn hex của Unicode, được sử dụng để định danh hình ảnh)
    private String hex;
    //Thứ tự của emoji
    @SerializedName("emoji_order")
    private int emojiOrder;
    //Phân loại của emoji
    private String category;

    public String getShortname() {
        return shortname;
    }
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    public String getUnicode() {
        return unicode;
    }
    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }
    public String getHex() {
        return hex;
    }
    public void setHex(String hex) {
        this.hex = hex;
    }
    public int getEmojiOrder() {
        return emojiOrder;
    }
    public void setEmojiOrder(int emojiOrder) {
        this.emojiOrder = emojiOrder;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    @Override
    public String toString() {
        return "Emoji: [shortname: " +  shortname + ", unicode: " + unicode + ", hex: " + hex +
                ", emojiOrder: " +emojiOrder + ", category: " + category + "]";
    }
}

