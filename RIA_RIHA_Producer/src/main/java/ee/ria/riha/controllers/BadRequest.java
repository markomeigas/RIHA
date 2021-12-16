package ee.ria.riha.controllers;

import ee.ria.riha.enums.ExceptionEnum;

public class BadRequest extends RuntimeException {

  private ExceptionEnum exceptionEnum;

  public BadRequest() {
    super("");
  }
  public BadRequest(String message) {
    super(message);
  }

  public BadRequest(ExceptionEnum exceptionEnum) {
    this.exceptionEnum = exceptionEnum;

  }

  public String getErrorCode(){
    return exceptionEnum.getErrorCode();
  }

}
