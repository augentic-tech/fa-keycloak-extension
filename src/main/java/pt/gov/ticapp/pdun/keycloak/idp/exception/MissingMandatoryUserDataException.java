package pt.gov.ticapp.pdun.keycloak.idp.exception;

public class MissingMandatoryUserDataException extends RuntimeException {

  public MissingMandatoryUserDataException(String attribute) {

    super(attribute);
  }
}
