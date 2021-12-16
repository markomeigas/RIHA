package ee.ria.riha.services;

import ee.ria.riha.models.Approval;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class ApprovalStorageServiceTest {

  private ApprovalStorageService service = spy(new ApprovalStorageService());
  private Path storageFilePath;

  @Before
  public void setUp() throws Exception {
    storageFilePath = Files.createTempFile("", "");
    service.file = storageFilePath.toFile();
  }

  @Test
  public void allApprovals() {
    Properties properties = new Properties();
    properties.setProperty("http://base.url2/shortname-2", "2015-10-10T01:10:10|KOOSKÕLASTATUD");
    properties.setProperty("http://base.url1/shortname-1", "2016-01-01T10:00:00|MITTE KOOSKÕLASTATUD");
    doReturn(properties).when(service).loadProperties();

    List<Approval> result = service.allApprovals();

    assertEquals(2, result.size());
    assertEquals("http://base.url1/shortname-1", result.get(0).getUri());
    assertEquals("2016-01-01T10:00:00", result.get(0).getTimestamp());
    assertEquals("MITTE KOOSKÕLASTATUD", result.get(0).getStatus());
    assertEquals("http://base.url2/shortname-2", result.get(1).getUri());
    assertEquals("2015-10-10T01:10:10", result.get(1).getTimestamp());
    assertEquals("KOOSKÕLASTATUD", result.get(1).getStatus());
  }

  @Test
  public void approvedApprovals() {
    Approval approved1 = new Approval("http://base.url/shortname-2", "2015-10-10T01:10:10", "KOOSKÕLASTATUD");
    Approval notApproved = new Approval("http://base.url/shortname-1", "2016-01-01T10:00:00", "MITTE KOOSKÕLASTATUD");
    Approval approved2 = new Approval("http://base.url/shortname-3", "2014-01-01T10:00:00", "KOOSKÕLASTATUD");
    doReturn(asList(approved1, notApproved, approved2)).when(service).allApprovals();

    List<Approval> result = service.approvedApprovals();

    assertEquals(asList(approved1, approved2), result);
  }

  @Test
  public void saveInfosystemApproval_noExistingFile() throws IOException {
    service.saveInfosystemApproval(new Approval("http://base.url/infosystem-name","2016-12-12T08:05:08.4567", "MITTE KOOSKÕLASTATUD"));

    assertEquals("2016-12-12T08:05:08.4567|MITTE KOOSKÕLASTATUD", approvals().getProperty("http://base.url/infosystem-name"));
  }

  @Test
  public void saveInfosystemApproval_existingFileWithData() throws IOException {
    Properties existingApprovals = approvals();
    existingApprovals.setProperty("http://base.url/other-infosystem-name", "2016-12-12T01:01:01|KOOSKÕLASTATUD");
    existingApprovals.store(Files.newOutputStream(storageFilePath), null);

    service.saveInfosystemApproval(new Approval("http://base.url/infosystem-name","2016-12-12T08:05:08.4567", "MITTE KOOSKÕLASTATUD"));

    Properties approvals = approvals();
    assertEquals(2, approvals.size());
    assertEquals("2016-12-12T08:05:08.4567|MITTE KOOSKÕLASTATUD", approvals.getProperty("http://base.url/infosystem-name"));
    assertEquals("2016-12-12T01:01:01|KOOSKÕLASTATUD", approvals.getProperty("http://base.url/other-infosystem-name"));
  }

  @Test
  public void saveInfosystemApproval_isThreadSafe() throws IOException, InterruptedException {
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Thread thread = new Thread(() -> {
        try {
          service.saveInfosystemApproval(new Approval(Thread.currentThread().getName(),"2016-12-12T08:05:08.4567", "MITTE KOOSKÕLASTATUD"));
        }
        catch (Throwable e) {
          e.printStackTrace();
        }
      }, String.valueOf("thread" + i));
      threads.add(thread);
    }

    threads.forEach(Thread::start);

    for (Thread thread : threads) {
      thread.join();
    }

    assertEquals(10, approvals().size());
  }

  private Properties approvals() throws IOException {
    try(InputStream inputStream = Files.newInputStream(storageFilePath)) {
      Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
    }
  }
}