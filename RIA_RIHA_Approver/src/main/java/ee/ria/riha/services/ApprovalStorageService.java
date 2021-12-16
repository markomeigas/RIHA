package ee.ria.riha.services;

import ee.ria.riha.models.Approval;
import ee.ria.riha.models.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class ApprovalStorageService {

  @Autowired DateTimeService dateTimeService;

  private Logger logger = LoggerFactory.getLogger(ApprovalStorageService.class);

  File file = new File("approvals.db");

  synchronized public void saveInfosystemApproval(Approval approval) {
    Properties properties = loadProperties();
    properties.setProperty(approval.getUri(), approval.getTimestamp() + "|" + approval.getStatus());
    save(properties);
  }

  public List<Approval> allApprovals() {
    //todo review to use get..., setProperty
    return loadProperties().entrySet().stream().map(property -> {
      String[] value = ((String)property.getValue()).split("\\|");
      return new Approval((String)property.getKey(), value[0], value[1]);
    }).collect(Collectors.toList());
  }

  public List<Approval> approvedApprovals() {
    return allApprovals().stream().filter(a -> a.getStatus().equals(Status.APPROVED.getValue())).collect(Collectors.toList());
  }

  private void save(Properties properties) {
    try (OutputStream outputStream = new FileOutputStream(file)) {
      properties.store(outputStream, null);
    }
    catch (IOException e) {
      logger.error("Could not save approvals", e);
      throw new RuntimeException(e);
    }
  }

  Properties loadProperties() {
    if (!file.exists()) return new Properties();
      try (InputStream inputStream = new FileInputStream(file)) {
      Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
    }
    catch (IOException e) {
      logger.error("Could not load approvals", e);
      throw new RuntimeException(e);
    }
  }
}
