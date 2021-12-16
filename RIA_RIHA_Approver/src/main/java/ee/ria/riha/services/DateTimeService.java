package ee.ria.riha.services;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DateTimeService {

  public ZonedDateTime now() {
    return ZonedDateTime.now();
  }

  public static ZonedDateTime toUTC(ZonedDateTime approvalTimestamp) {
    return ZonedDateTime.ofInstant(approvalTimestamp.toInstant(), ZoneId.of("UTC"));
  }

  public static String format(ZonedDateTime zonedDateTime) {
    return zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}
