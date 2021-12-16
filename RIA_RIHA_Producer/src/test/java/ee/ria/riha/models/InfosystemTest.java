package ee.ria.riha.models;

import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InfosystemTest {

  @Test
  public void allInfosystemPropertiesAreNotRequired() {
    Infosystem infosystem = new Infosystem(new JSONObject("{\"owner\":{\"code\":\"123\"}}"), "http://base.url");

    assertEquals("123", infosystem.getOwner().getCode());
    assertNull(infosystem.getUri());
    assertNull(infosystem.getDocumentation());
    assertNull(infosystem.getName());
    assertNull(infosystem.getShortname());
    assertNull(infosystem.getMeta().getSystem_status().getTimestamp());
  }

  @Test
  public void deserializeFromJson() {
    String json =
      "{" +
        "  \"name\": \"Rebaste register\"," +
        "  \"shortname\": \"fox\"," +
        "  \"owner\": {" +
        "    \"code\": \"12345\"," +
        "  }," +
        "  \"documentation\": \"http://riha.eesti.ee\"," +
        "  \"meta\": {" +
        "    \"system_status\": {" +
        "      \"timestamp\": \"2016-12-13T17:10:20.785\"" +
        "    }" +
        "  }," +
        "  \"uri\": \"http://base.url/fox\"," +
        "  \"purpose\": \"Rebaste kataloog\"" +
    "}";

    Infosystem infosystem = new Infosystem(new JSONObject(json), "http://base.url");

    assertEquals("12345", infosystem.getOwner().getCode());
    assertEquals("http://base.url/fox", infosystem.getUri());
    assertEquals("http://riha.eesti.ee", infosystem.getDocumentation());
    assertEquals("Rebaste kataloog", infosystem.getPurpose());
    assertEquals("Rebaste register", infosystem.getName());
    assertEquals("fox", infosystem.getShortname());
    assertEquals("2016-12-13T17:10:20.785", infosystem.getMeta().getSystem_status().getTimestamp());
  }

  @Test
  public void serializeToJson() {
    String expectedJson =
      "{" +
      "  \"name\": \"Eesti kirikute, koguduste ja koguduste liitude register\"," +
      "  \"shortname\": \"Eesti kirikuregister\"," +
      "  \"owner\": {" +
      "    \"code\": \"70000562\"" +
      "  }," +
      "  \"documentation\": \"eesti_kirikute_koguduste_ja_koguduste_liitude_register\"," +
      "  \"purpose\": \"Kirikute kataloog\"," +
      "  \"meta\": {" +
      "    \"system_status\": {" +
      "      \"timestamp\": \"2015-09-05T00:36:26.255215\"" +
      "    }" +
      "  }," +
      "  \"uri\": \"http://base.url/Eesti%20kirikuregister\"" +
      "}";

    Infosystem infosystem = new Infosystem(
      "Eesti kirikute, koguduste ja koguduste liitude register",
      "Eesti kirikuregister",
      "eesti_kirikute_koguduste_ja_koguduste_liitude_register",
      "Kirikute kataloog",
      "70000562",
      "2015-09-05T00:36:26.255215",
      "http://base.url");

    JSONAssert.assertEquals(expectedJson, new JSONObject(infosystem).toString(), true);
  }

  @Test
  public void buildUri() {
    assertNull(Infosystem.buildUri("http://base.url", null));
    assertEquals("http://base.url/foobar", Infosystem.buildUri("http://base.url", "foobar"));
    assertEquals("http://base.url/Eesti%20kirikuregister", Infosystem.buildUri("http://base.url", "Eesti kirikuregister"));
    assertEquals("https://base.url/Eesti%20kirikuregister", Infosystem.buildUri("https://base.url", "Eesti kirikuregister"));
    assertEquals("http://base.url/%C3%95ppurite%20register", Infosystem.buildUri("http://base.url", "Õppurite register"));
    assertEquals("http://base.url/K%C3%A4%C3%A4rik%C3%B5%20suusah%C3%BCppeb%C3%B6%C3%B6s", Infosystem.buildUri("http://base.url", "Käärikõ suusahüppeböös"));
  }
}