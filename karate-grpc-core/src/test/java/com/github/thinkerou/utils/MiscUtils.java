package com.github.thinkerou.utils;

import com.github.thinkerou.karate.service.Pair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.StringReader;

public class MiscUtils {

  public static String extractMessage(String raw) {
    return extract(raw, "message");
  }

  public static String extractDescription(String raw) {
    return extract(raw, "description");
  }

  private static String extract(String raw, String key) {
    JsonElement response = new JsonParser().parse(new StringReader(raw));
    return response.getAsJsonObject().get(key).getAsString();
  }

  public static int extractOfferNumber(String raw) {
    JsonElement response = new JsonParser().parse(new StringReader(raw));
    return response.getAsJsonObject().get("offer_number").getAsInt();
  }

  public static JsonArray asArray(String raw) {
    return new JsonParser().parse(new StringReader(raw)).getAsJsonArray();
  }

  public static JsonArray filter(JsonArray array, Pair<String, String> kvp) {
    JsonArray result = new JsonArray();
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i).getAsJsonObject().get(kvp.left()).getAsString().equals(kvp.right())) {
        result.add(array.get(i));
      }
    }
    return result;
  }
}
