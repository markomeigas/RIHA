package ee.ria.riha.services;

import ee.ria.riha.models.Infosystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class InfosystemStorageServiceTest {

  @Spy
  @InjectMocks
  InfosystemStorageService service;

  @Before
  public void setUp() throws Exception {
    service.filePath = Files.createTempFile("", "");
  }

  @Test
  public void updateExistingInfosystem() throws IOException {
    Files.write(service.filePath, "[{\"shortname\":\"existing-short-name\"}]".getBytes());

    service.save("existing-short-name", new Infosystem("name", "short-name", "http://doc.url", "abc", "ownerCode", "status-timestamp", "http://base.url"));

    JSONAssert.assertEquals("["+ infosystemJson()+"]", fileData(), true);
  }

  private String infosystemJson() {
    return
        "{" +
        "  \"name\": \"name\"," +
        "  \"shortname\": \"short-name\"," +
        "  \"owner\": {" +
        "    \"code\": \"ownerCode\"" +
        "  }," +
        "  \"documentation\": \"http://doc.url\"," +
        "  \"purpose\": \"abc\"," +
        "  \"meta\": {" +
        "    \"system_status\": {" +
        "      \"timestamp\": \"status-timestamp\"" +
        "    }" +
        "  }," +
        "  \"uri\": \"http://base.url/short-name\"" +
        "}";
  }

  @Test
  public void save() throws IOException {
    doReturn("[]").when(service).load();

    service.save(null, new Infosystem("name", "short-name", "http://doc.url", "abc", "ownerCode", "status-timestamp", "http://base.url"));

    JSONAssert.assertEquals("["+ infosystemJson()+"]", fileData(), true);
  }

  @Test
  public void save_mergesWithExistingInfosystems() throws IOException {
    Files.write(service.filePath, "[{\"name\":\"existing-system-name\"}]".getBytes());

    service.save(null, new Infosystem("name", "short-name", "http://doc.url", "abc", "ownerCode", "status-timestamp", "http://base.url"));

    JSONAssert.assertEquals("[{\"name\":\"existing-system-name\"}," + infosystemJson()+ "]", fileData(), true);
  }

  @Test
  public void delete() throws IOException {
    Files.write(service.filePath, "[{\"shortname\":\"other-short-name\"}, {\"shortname\":\"short-name\"}]".getBytes());

    service.delete("short-name");

    JSONAssert.assertEquals("[{\"shortname\":\"other-short-name\"}]", fileData(), true);
  }

  @Test
  public void findByShortName() throws IOException {
    Files.write(service.filePath, "[{\"shortname\":\"other-short-name\",\"owner\":{\"code\":\"12345\"}}, {\"shortname\":\"short-name\",\"owner\":{\"code\":\"23456\"}}]".getBytes());

    Infosystem infosystem = service.find("short-name");

    assertEquals("short-name", infosystem.getShortname());
    assertEquals("23456", infosystem.getOwner().getCode());
  }

  private String fileData() throws IOException {
    return new String(Files.readAllBytes(service.filePath), UTF_8);
  }
}