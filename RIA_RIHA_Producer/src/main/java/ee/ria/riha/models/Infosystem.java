package ee.ria.riha.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.springframework.web.util.UriUtils.encode;

@Getter
public class Infosystem {
  String name;
  String shortname;
  String documentation;
  String purpose;
  Owner owner;
  Meta meta;
  String uri;

  public Infosystem(String name, String shortName, String documentation, String purpose, String ownerCode, String statusTimestamp, String baseUrl) {
    this.name = name;
    this.shortname = shortName;
    this.documentation = documentation;
    this.purpose = purpose;
    this.owner = new Owner(ownerCode);
    this.meta = new Meta(statusTimestamp);
    this.uri = buildUri(baseUrl, shortName);
  }

  @Getter
  @AllArgsConstructor
  public class SystemStatus {
    String timestamp;
  }

  @Getter
  @AllArgsConstructor
  public class Owner {
    String code;
  }

  @Getter
  public class Meta {
    SystemStatus system_status;

    Meta(String statusTimestamp) {
      this.system_status = new SystemStatus(statusTimestamp);
    }
  }

  public Infosystem(JSONObject jsonObject, String baseUrl) {
    this(
      getPropertyValue(jsonObject, "name"),
      getPropertyValue(jsonObject, "shortname"),
      getPropertyValue(jsonObject, "documentation"),
      getPropertyValue(jsonObject, "purpose"),
      getPropertyValue(jsonObject.getJSONObject("owner"), "code"),
      jsonObject.has("meta") && jsonObject.getJSONObject("meta").has("system_status")
        ? getPropertyValue(jsonObject.getJSONObject("meta").getJSONObject("system_status"), "timestamp")
        : null,
      baseUrl
    );
  }

  static String buildUri(String baseUrl, String shortName) {
    if (shortName == null) return null;
    try {
      return baseUrl + "/" + encode(shortName, UTF_8);
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getPropertyValue(JSONObject jsonObject, String name) {
    return jsonObject.has(name) ? jsonObject.getString(name) : null;
  }
}
