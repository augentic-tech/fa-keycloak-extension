package pt.gov.ticapp.pdun.keycloak.idp;

import static pt.gov.ticapp.pdun.keycloak.idp.mappers.FaSamlUserAttributeMapper.CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_USERNAME_KEY;
import static pt.gov.ticapp.pdun.keycloak.idp.mappers.FaSamlUserAttributeMapper.USER_ATTRIBUTE_EMAIL_KEY;
import static pt.gov.ticapp.pdun.keycloak.idp.mappers.FaSamlUserAttributeMapper.USER_ATTRIBUTE_FIRST_NAME_KEY;
import static pt.gov.ticapp.pdun.keycloak.idp.mappers.FaSamlUserAttributeMapper.USER_ATTRIBUTE_LAST_NAME_KEY;
import static pt.gov.ticapp.pdun.keycloak.idp.mappers.FaSamlUserAttributeMapper.USER_ATTRIBUTE_USERNAME_KEY;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.keycloak.broker.provider.IdentityProvider;
import org.keycloak.broker.provider.IdentityProvider.AuthenticationCallback;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType.ASTChoiceType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.dom.saml.v2.protocol.ResponseType;
import org.keycloak.dom.saml.v2.protocol.ResponseType.RTChoiceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.saml.common.constants.JBossSAMLURIConstants;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import pt.gov.ticapp.pdun.keycloak.idp.FaSamlEndpoint.FaSamlPostBinding;
import pt.gov.ticapp.pdun.keycloak.idp.configuration.PropertyReader;
import pt.gov.ticapp.pdun.keycloak.idp.data.UsernameObfuscator;
import pt.gov.ticapp.pdun.keycloak.idp.exception.MissingMandatoryUserDataException;
import pt.gov.ticapp.pdun.keycloak.idp.mappers.FaSamlUserAttributeMapper;

public class FaSamlPostBindingWorker {

  private final KeycloakSession session;
  private final IdentityProvider.AuthenticationCallback callback;
  private final FaSamlPostBinding faSamlPostBinding;

  public FaSamlPostBindingWorker(KeycloakSession session, AuthenticationCallback callback,
      FaSamlPostBinding faSamlPostBinding) {

    this.session = session;
    this.callback = callback;
    this.faSamlPostBinding = faSamlPostBinding;
  }

  Response handleLoginResponse(String samlResponse, SAMLDocumentHolder holder, ResponseType responseType,
      String relayState, String clientId) {

    if (responseType != null && responseType.getStatus() != null
        && PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaCancelledAuthenticationStatusMessage()
        .equals(responseType.getStatus().getStatusMessage())) {

      session.getContext().setAuthenticationSession(callback.getAndVerifyAuthenticationSession(relayState));

      return createResponse(
          PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusBackWasPressed());
    } else {
      try {
        addPrincipalAssertion(responseType);
        final Response response = faSamlPostBinding.handleLoginResponseWithPrincipal(samlResponse, holder, responseType,
            relayState, clientId);

        validateMandatoryAttributesExistence();

        return response;
      } catch (MissingMandatoryUserDataException ex) {
        return createResponse(
            PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusMissingUserAttributesOnAutenticacaoGov());
      } catch (Exception ex) {
        return createResponse(ex.getCause() != null && ex.getCause() instanceof MissingMandatoryUserDataException
            ? PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusMissingUserAttributesOnAutenticacaoGov()
            : PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusUnexpectedError());
      }
    }
  }

  private Response createResponse(int mainCause) {

    try {
      return Response.status(Status.SEE_OTHER)
          .location(new URI(session.getContext().getAuthenticationSession().getRedirectUri() + "?"
              + PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusQueryStringParameter()
              + "=" + mainCause))
          .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void addPrincipalAssertion(ResponseType responseType) {

    if (isSuccessfulSamlResponse(responseType)) {
      final List<AttributeStatementType> attributeStatementTypes = getAttributeStatementTypes(responseType);

      if (!attributeStatementTypes.isEmpty()) {
        final List<String> values = attributeStatementTypes.stream().map(AttributeStatementType::getAttributes)
            .flatMap(Collection::stream).map(ASTChoiceType::getAttribute).filter(
                attribute -> PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaCalculatedAssertionPrincipalFrom()
                    .equalsIgnoreCase(attribute.getName())).map(AttributeType::getAttributeValue)
            .filter(Objects::nonNull).flatMap(Collection::stream).map(Object::toString).collect(Collectors.toList());

        if (!values.isEmpty()) {
          addCalculatedPrincipalAssertion(attributeStatementTypes.get(0),
              createCalculatedPrincipalAssertion(values.get(0)));
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private List<AttributeStatementType> getAttributeStatementTypes(ResponseType responseType) {
    List<AttributeStatementType> statementAbstractTypes = Collections.EMPTY_LIST;

    if (responseType.getAssertions() != null) {
      statementAbstractTypes = responseType.getAssertions().stream().filter(Objects::nonNull)
          .map(RTChoiceType::getAssertion).filter(Objects::nonNull).map(AssertionType::getStatements)
          .filter(Objects::nonNull).flatMap(Collection::stream).filter(sat -> sat instanceof AttributeStatementType)
          .map(sat -> (AttributeStatementType) sat)
          .collect(Collectors.toList());
    }

    return statementAbstractTypes;
  }

  private AttributeType createCalculatedPrincipalAssertion(String value) {
    final AttributeType calculatedPrincipalAssertion = new AttributeType(
        PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaCalculatedAssertionPrincipalName());

    calculatedPrincipalAssertion.setNameFormat(JBossSAMLURIConstants.NAMEID_FORMAT_PERSISTENT.get());
    calculatedPrincipalAssertion.addAttributeValue(UsernameObfuscator.obfuscateUsername(value));

    return calculatedPrincipalAssertion;
  }

  private void addCalculatedPrincipalAssertion(AttributeStatementType statement,
      AttributeType calculatedPrincipalAssertion) {

    statement.addAttribute(new ASTChoiceType(calculatedPrincipalAssertion));
  }

  private void validateMandatoryAttributesExistence() {

    final Map<String, String> clientNotes = session.getContext().getAuthenticationSession().getClientNotes();

    if (!clientNotes.containsKey(FaSamlUserAttributeMapper.CONTEXT_CLIENT_NOTES_KEY_USER_ATTRIBUTE_EMAIL_KEY)) {
      throw new MissingMandatoryUserDataException(USER_ATTRIBUTE_EMAIL_KEY);
    } else if (!clientNotes.containsKey(
        FaSamlUserAttributeMapper.CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_FIRST_NAME_KEY)) {
      throw new MissingMandatoryUserDataException(USER_ATTRIBUTE_FIRST_NAME_KEY);
    } else if (!clientNotes.containsKey(
        FaSamlUserAttributeMapper.CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_LAST_NAME_KEY)) {
      throw new MissingMandatoryUserDataException(USER_ATTRIBUTE_LAST_NAME_KEY);
    } else if (!clientNotes.containsKey(
        CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_USERNAME_KEY)) {
      throw new MissingMandatoryUserDataException(USER_ATTRIBUTE_USERNAME_KEY);
    }
  }

  private boolean isSuccessfulSamlResponse(ResponseType responseType) {

    return responseType != null
        && responseType.getStatus() != null
        && responseType.getStatus().getStatusCode() != null
        && responseType.getStatus().getStatusCode().getValue() != null
        && Objects.equals(responseType.getStatus().getStatusCode().getValue().toString(),
        JBossSAMLURIConstants.STATUS_SUCCESS.get());
  }
}
