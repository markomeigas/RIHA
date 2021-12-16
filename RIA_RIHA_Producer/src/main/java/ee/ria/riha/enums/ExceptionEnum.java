package ee.ria.riha.enums;

public enum ExceptionEnum{

    ERROR_BAD_REQUEST("error.bad.request"),
    ERROR_INFOSYSTEM_ALREADY_EXISTS("error.already.exists");

    private String errorCode;
    ExceptionEnum(String errorCode){
        this.errorCode=errorCode;
    }
    public String getErrorCode(){
        return errorCode;
    }



}
