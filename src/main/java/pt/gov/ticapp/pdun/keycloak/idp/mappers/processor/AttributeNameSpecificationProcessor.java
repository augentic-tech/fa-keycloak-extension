package pt.gov.ticapp.pdun.keycloak.idp.mappers.processor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.jboss.logging.Logger;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType.ASTChoiceType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import pt.gov.ticapp.pdun.keycloak.idp.mappers.model.AttributeNameSpecification;

public class AttributeNameSpecificationProcessor {

  private static final Logger LOGGER = Logger.getLogger(AttributeNameSpecificationProcessor.class);

  private final DirectSpecificationTypeProcessor directSpecificationTypeProcessor;
  private final XmlSpecificationTypeProcessor xmlSpecificationTypeProcessor;
  private final FunctionSpecificationTypeProcessor functionSpecificationTypeProcessor;

  public AttributeNameSpecificationProcessor() {

    this.directSpecificationTypeProcessor = new DirectSpecificationTypeProcessor();
    this.xmlSpecificationTypeProcessor = new XmlSpecificationTypeProcessor();
    this.functionSpecificationTypeProcessor = new FunctionSpecificationTypeProcessor();
  }

  public Optional<String> findAttributeValuesInContext(AttributeNameSpecification attributeNameSpecification,
      BrokeredIdentityContext context) {
    AssertionType assertion = (AssertionType) context.getContextData().get(SAMLEndpoint.SAML_ASSERTION);

    return getAttributeSelectorValue(attributeNameSpecification,
        assertion.getAttributeStatements().stream().flatMap(statement -> statement.getAttributes().stream())
            .filter(elementWith(attributeNameSpecification.getName()))
            .flatMap(attributeType -> attributeType.getAttribute().getAttributeValue().stream())
            .filter(Objects::nonNull).map(Object::toString).findFirst());
  }

  private Predicate<ASTChoiceType> elementWith(String attributeName) {

    return attributeType -> {
      AttributeType attribute = attributeType.getAttribute();

      return Objects.equals(attribute.getName(), attributeName) || Objects.equals(attribute.getFriendlyName(),
          attributeName);
    };
  }

  private Optional<String> getAttributeSelectorValue(AttributeNameSpecification attributeNameSpecification,
      Optional<String> value) {
    Optional<String> retValue;

    if (value.isPresent()) {
      String xmlValue = null;

      switch (attributeNameSpecification.getAttributeNameSpecificationType()) {
        case XML:
          xmlValue = xmlSpecificationTypeProcessor.process(value.get(), attributeNameSpecification.getSelector());
          break;
        case DIRECT:
          xmlValue = directSpecificationTypeProcessor.process(value.get(), attributeNameSpecification.getSelector());
          break;
        case FUNCTION:
          xmlValue = functionSpecificationTypeProcessor.process(value.get(), attributeNameSpecification.getSelector());
          break;
        default:
          LOGGER.warn("Unknown Attribute Name Specification Type: " + attributeNameSpecification);
      }

      retValue = Optional.ofNullable(xmlValue);
    } else {
      retValue = Optional.empty();
    }

    return retValue;
  }
}


