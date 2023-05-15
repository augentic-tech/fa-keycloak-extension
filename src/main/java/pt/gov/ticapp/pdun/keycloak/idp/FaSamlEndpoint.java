package pt.gov.ticapp.pdun.keycloak.idp;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.keycloak.broker.provider.IdentityProvider.AuthenticationCallback;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.broker.saml.SAMLIdentityProvider;
import org.keycloak.broker.saml.SAMLIdentityProviderConfig;
import org.keycloak.dom.saml.v2.protocol.ResponseType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.saml.common.constants.GeneralConstants;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.saml.validators.DestinationValidator;

public class FaSamlEndpoint extends SAMLEndpoint {

  @Context
  private KeycloakSession session;

  public FaSamlEndpoint(RealmModel realm, SAMLIdentityProvider provider, SAMLIdentityProviderConfig config,
      AuthenticationCallback callback, DestinationValidator destinationValidator) {

    super(realm, provider, config, callback, destinationValidator);
  }

  @Override
  @Path("clients/{client_id}")
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response postBinding(@FormParam(GeneralConstants.SAML_REQUEST_KEY) String samlRequest,
      @FormParam(GeneralConstants.SAML_RESPONSE_KEY) String samlResponse,
      @FormParam(GeneralConstants.RELAY_STATE) String relayState, @PathParam("client_id") String clientId) {

    return new FaSamlPostBinding().execute(samlRequest, samlResponse, relayState, clientId);
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response postBinding(@FormParam(GeneralConstants.SAML_REQUEST_KEY) String samlRequest,
      @FormParam(GeneralConstants.SAML_RESPONSE_KEY) String samlResponse,
      @FormParam(GeneralConstants.RELAY_STATE) String relayState) {

    return new FaSamlPostBinding().execute(samlRequest, samlResponse, relayState, null);
  }

  class FaSamlPostBinding extends SAMLEndpoint.PostBinding {

    @Override
    protected Response handleLoginResponse(String samlResponse, SAMLDocumentHolder holder, ResponseType responseType,
        String relayState, String clientId) {

      return new FaSamlPostBindingWorker(session, callback, this).handleLoginResponse(samlResponse, holder, responseType,
          relayState, clientId);
    }

    Response handleLoginResponseWithPrincipal(String samlResponse, SAMLDocumentHolder holder, ResponseType responseType,
        String relayState, String clientId) {

      return super.handleLoginResponse(samlResponse, holder, responseType, relayState, clientId);
    }
  }
}
