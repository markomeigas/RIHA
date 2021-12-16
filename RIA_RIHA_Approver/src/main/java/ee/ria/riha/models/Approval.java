package ee.ria.riha.models;

import lombok.Getter;

@Getter
public class Approval {
  String uri;
  String timestamp;
  String status;

  public Approval(String uri, String timestamp, String status) {
    this.uri = uri;
    this.timestamp = timestamp;
    this.status = status;
  }
}
