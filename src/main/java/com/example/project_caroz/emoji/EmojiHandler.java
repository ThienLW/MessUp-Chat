package com.example.project_caroz.emoji;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Quản lý Emoji:
 * ① Nhập các thực thể emoji,
 * ② Quản lý các bộ sưu tập emoji,
 * ③ Chuyển đổi các biểu thức emoji khác nhau (viết tắt, unicode, hex),
 * ④ Phân tích và tách biểu thức emoji và văn bản từ chuỗi đầu vào.
 * @Title: EmojiHandler.java
 * Tham khảo: https://github.com/UltimateZero/EmojiOneJava
 */
public class EmojiHandler {
    private static final String path = "emoji.json";

    private static EmojiHandler instance = new EmojiHandler();

    private Map<String, Emoji> emojiMap;
    private HashMap<String, String> shortnameToUnicode = new HashMap<>();
    private  HashMap<String, String> unicodeToShortname = new HashMap<>();
    private  HashMap<String, String> shortameToHex = new HashMap<>();

    Gson gson = new Gson();
    // Mô hình phù hợp với biểu thức chính quy
    private Pattern UNICODE_PATTERN;
    private  Pattern SHORTNAME_PATTERN;

    private EmojiHandler() {
        loadEmoji();
    }

    // Đối tượng duy nhất (singleton)
    public static EmojiHandler getInstance() {
        return instance;
    }

    public Map<String, Emoji> getEmojiMap() {
        return emojiMap;
    }

    /**
     * Nhập biểu tượng cười và khởi tạo trình quản lý emoji
     */
    private void loadEmoji() {

        JsonReader reader;
        try {
            // Lấy JSON từ resource
            URL url =  this.getClass().getClassLoader().getResource(path);
            System.out.println(url.toString());
            reader = new JsonReader(new FileReader(url.getFile()));
            emojiMap = gson.fromJson(reader, new TypeToken<Map<String, Emoji>>() {
            }.getType());

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        emojiMap.forEach((name, entry) -> {

            String shortname = entry.getShortname();
            // Unicode trong JSON có định dạng hex
            String hex = entry.getUnicode();
            String unicode = null;
            if (hex != null && !hex.isEmpty()) {
                unicode = convert(hex);
                // Cập nhật lại unicode và hex trong đối tượng Emoji
                entry.setUnicode(unicode);
                entry.setHex(hex);
            }
            //logger.debug("{}", entry);
            if (shortname == null || shortname.isEmpty() || unicode == null || unicode.isEmpty()) {
                return;
            }
            shortnameToUnicode.put(shortname, unicode);
            unicodeToShortname.put(unicode, shortname);
            shortameToHex.put(unicode, hex);
        });

        // Sử dụng dấu gạch đứng '|' (đại diện trong biểu thức chính quy) để kết hợp các phần tử trong tập hợp thành chuỗi, dùng cho so khớp mẫu
        SHORTNAME_PATTERN = Pattern
                .compile(String.join("|", shortnameToUnicode.keySet().stream().collect(Collectors.toList())));
        UNICODE_PATTERN = Pattern.compile(String.join("|",
                unicodeToShortname.keySet().stream().map(u -> Pattern.quote(u)).collect(Collectors.toList())));
    }

    /**
     * Tìm kiếm các biểu tượng cười dựa trên từ khóa
     * @param keywords
     * @return
     */
    public List<Emoji> search(String keywords) {

        return emojiMap.values().stream()
                .filter(emoji -> emoji.getShortname().contains(keywords))
                .collect(Collectors.toList());

    }

    /**
     * Phân tách chuỗi thành biểu tượng cười và văn bản (biểu tượng cười ở dạng viết tắt)
     *
     * @param input Chuỗi đầu vào
     * @return Queue lưu trữ các biểu tượng cười và văn bản đã phân tách
     */
    public Queue<Object> toEmojiAndText(String input) {
        Queue<Object> queue = new LinkedList<>();
        Matcher matcher = SHORTNAME_PATTERN.matcher(input);
        int lastEnd = 0;
        while (matcher.find()) {
            String lastText = input.substring(lastEnd, matcher.start());
            if (!lastText.isEmpty())
                queue.add(lastText);
            String m = matcher.group();
            emojiMap.values().stream().filter(entry -> entry.getShortname().equals(m)).forEach(entry -> {
                if (entry.getHex() != null && !entry.getHex().isEmpty()) {
                    queue.add(entry);
                }
            });
            lastEnd = matcher.end();
        }
        String lastText = input.substring(lastEnd);
        if (!lastText.isEmpty())
            queue.add(lastText);
        return queue;
    }

    /**
     * Chuyển đổi các biểu tượng cười từ viết tắt sang unicode trong chuỗi
     * @param str
     * @return
     */
    public String shortnameToUnicode(String str) {
        String output = replaceWithFunction(str, SHORTNAME_PATTERN, (shortname) -> {
            // Triển khai một đối tượng chức năng
            if (shortname == null || shortname.isEmpty() || (!shortnameToUnicode.containsKey(shortname))) {
                return shortname;
            }
            if (shortnameToUnicode.get(shortname).isEmpty()) {
                return shortname;
            }
            String unicode = shortnameToUnicode.get(shortname).toUpperCase();
            return convert(unicode);
        });

        return output;
    }

    /**
     * Chuyển đổi các biểu tượng cười từ unicode sang viết tắt trong chuỗi
     * @param str
     * @return
     */
    public String unicodeToShortname(String str) {
        String output = replaceWithFunction(str, UNICODE_PATTERN, (unicode) -> {
            if (unicode == null || unicode.isEmpty() || (!unicodeToShortname.containsKey(unicode))) {
                return unicode;
            }
            return unicodeToShortname.get(unicode);
        });
        return output;
    }

    /**
     * Thực hiện thay thế biểu tượng cười trong chuỗi bằng cách sử dụng một hàm chức năng cho trước.
     *
     * @param input Chuỗi đầu vào
     * @param pattern Mô hình phù hợp
     * @param func Hàm chức năng để xử lý việc thay thế
     * @return Chuỗi kết quả sau khi thay thế
     */
    private String replaceWithFunction(String input, Pattern pattern, Function<String, String> func) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = pattern.matcher(input);
        int lastEnd = 0;

        while (matcher.find()) {
            // Lấy phần văn bản trước khi gặp biểu tượng cười
            String lastText = input.substring(lastEnd, matcher.start());
            builder.append(lastText);

            // Gán phần biểu tượng cười dưới dạng Unicode bằng cách sử dụng hàm chức năng
            builder.append(func.apply(matcher.group()));

            lastEnd = matcher.end();
        }

        // Lấy phần văn bản còn lại của chuỗi
        builder.append(input.substring(lastEnd));
        return builder.toString();
    }

    /**
     * Chuyển đổi dãy byte của biểu tượng cười thành dạng Unicode, tức là chuyển đổi từ hex sang dạng byte của Unicode.
     *
     * @param unicodeStr Dãy byte của biểu tượng cười
     * @return Dãy byte Unicode kết quả
     */
    private String convert(String unicodeStr) {
        if (unicodeStr.isEmpty())
            return unicodeStr;
        String[] parts = unicodeStr.split("-");
        StringBuilder buff = new StringBuilder();
        for (String s : parts) {
            int part = Integer.parseInt(s, 16);
            if (part >= 0x10000 && part <= 0x10FFFF) {
                int hi = (int) (Math.floor((part - 0x10000) / 0x400) + 0xD800);
                int lo = ((part - 0x10000) % 0x400) + 0xDC00;
                buff.append(new String(Character.toChars(hi)) + new String(Character.toChars(lo)));
            } else {
                buff.append(new String(Character.toChars(part)));
            }
        }
        return buff.toString();
    }

}
