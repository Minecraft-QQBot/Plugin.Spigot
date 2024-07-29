package org.lonelysail.qqbot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class Utils {
    private final Gson gson = new Gson();
    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Type type = new TypeToken<HashMap<String, Object>>() {}.getType();

    public String encode(HashMap<String, ?> originalMap) {
        String string = this.gson.toJson(originalMap);
        return this.encoder.encodeToString(string.getBytes(StandardCharsets.UTF_8));
    }

    public HashMap<String, ?> decode(String originalString) {
//        解码
        byte[] stringBytes = this.decoder.decode(originalString.getBytes(StandardCharsets.UTF_8));
        String decodeString = new String(stringBytes, StandardCharsets.UTF_8);
        return this.gson.fromJson(decodeString, this.type);
    }
}
