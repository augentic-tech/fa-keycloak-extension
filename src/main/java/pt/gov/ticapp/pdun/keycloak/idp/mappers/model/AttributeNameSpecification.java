package pt.gov.ticapp.pdun.keycloak.idp.mappers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

public class AttributeNameSpecification {

  @JsonProperty("type")
  private AttributeNameSpecificationType attributeNameSpecificationType;
  private String name;
  private String selector;

  public AttributeNameSpecificationType getAttributeNameSpecificationType() {

    return attributeNameSpecificationType;
  }

  public String getName() {

    return name;
  }

  public String getSelector() {

    return selector;
  }

  @Override
  public String toString() {

    return new StringJoiner(", ", AttributeNameSpecification.class.getSimpleName() + "[", "]")
        .add("attributeNameSpecificationType=" + attributeNameSpecificationType)
        .add("name='" + name + "'")
        .add("selector='" + selector + "'")
        .toString();
  }
}