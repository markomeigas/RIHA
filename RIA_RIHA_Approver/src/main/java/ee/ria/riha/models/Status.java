package ee.ria.riha.models;

import lombok.Getter;

@Getter
public enum Status {
  APPROVED("KOOSKÕLASTATUD"), NOT_APPROVED("MITTE KOOSKÕLASTATUD");
  String value;

  Status(String value) { this.value = value;}
}