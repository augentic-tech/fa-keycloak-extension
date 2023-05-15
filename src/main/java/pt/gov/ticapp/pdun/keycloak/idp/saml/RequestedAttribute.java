package pt.gov.ticapp.pdun.keycloak.idp.saml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

/**
 * Based on code from https://github.com/grnet/eidas-keycloak-extension.
 */
public class RequestedAttribute {

  @JsonProperty("xmlns")
  private String xmlns;
  @JsonProperty("name")
  private String name;
  @JsonProperty("format")
  private String format;
  @JsonProperty("required")
  private String required;

  public RequestedAttribute() {

    super();
  }

  public RequestedAttribute(String xmlns, String name, String format, String required) {

    super();

    this.xmlns = xmlns;
    this.name = name;
    this.format = format;
    this.required = required;
  }

  public String getXmlns() {

    return xmlns;
  }

  public void setXmlns(String xmlns) {

    this.xmlns = xmlns;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getFormat() {

    return format;
  }

  public void setFormat(String format) {

    this.format = format;
  }

  public String getRequired() {

    return required;
  }

  public void setRequired(String required) {

    this.required = required;
  }

  @Override
  public String toString() {

    return new StringJoiner(", ", RequestedAttribute.class.getSimpleName() + "[", "]").add("xmlns='" + xmlns + "'")
        .add("name='" + name + "'").add("format='" + format + "'").add("required='" + required + "'").toString();
  }
}
