package ee.ria.riha.services;

import ee.ria.riha.models.Infosystem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class InfosystemStorageService {

  Path filePath = Paths.get("systems.json");

  @Value("${base.url}")
  String baseUrl;

  public void save(Infosystem infosystem) {
    save(null, infosystem);
  }

  synchronized public void save(String shortName, Infosystem infosystem) {
    JSONArray infosystems = new JSONArray(load());
    if (shortName != null) {
      infosystems.remove(findIndex(shortName, infosystems));
    }
    infosystems.put(new JSONObject(infosystem));
    save(infosystems);
  }

  synchronized public void delete(String shortName) {
    JSONArray infosystems = new JSONArray(load());
    infosystems.remove(findIndex(shortName, infosystems));
    save(infosystems);
  }

  public Infosystem find(String shortName) {
    JSONArray infosystems = new JSONArray(load());
    int index = findIndex(shortName, infosystems);
    return index < 0 ? null : new Infosystem(infosystems.getJSONObject(index), baseUrl);
  }

  private int findIndex(String shortName, JSONArray infosystems) {
    for (int i = 0; i < infosystems.length(); i++) {
      JSONObject infosystem = infosystems.getJSONObject(i);
      if (infosystem.getString("shortname").equals(shortName)) {
        return i;
      }
    }
    return -1;
  }

  public String load() {
    if (!filePath.toFile().exists()) return "[]";
    try {
      return new String(Files.readAllBytes(filePath), UTF_8);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void save(JSONArray infosystems) {
    try {
      Files.write(filePath, infosystems.toString().getBytes(UTF_8));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
