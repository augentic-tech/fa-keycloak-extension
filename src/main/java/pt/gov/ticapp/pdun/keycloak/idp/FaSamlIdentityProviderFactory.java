package pt.gov.ticapp.pdun.keycloak.idp;

import org.keycloak.Config.Scope;
import org.keycloak.broker.saml.SAMLIdentityProvider;
import org.keycloak.broker.saml.SAMLIdentityProviderConfig;
import org.keycloak.broker.saml.SAMLIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.saml.validators.DestinationValidator;
import pt.gov.ticapp.pdun.keycloak.idp.configuration.PropertyReader;

/**
 * Based on code from https://github.com/grnet/eidas-keycloak-extension.
 */
public class FaSamlIdentityProviderFactory extends SAMLIdentityProviderFactory {

  private DestinationValidator destinationValidator;

  @Override
  public void init(Scope config) {

    super.init(config);

    this.destinationValidator = DestinationValidator.forProtocolMap(config.getArray("knownProtocols"));
  }

  @Override
  public String getName() {

    return PropertyReader.INSTANCE.getFaSamlIdentityProviderName();
  }

  @Override
  public String getId() {

    return PropertyReader.INSTANCE.getFaSamlIdentityProviderId();
  }

  @Override
  public SAMLIdentityProviderConfig createConfig() {

    return new FaSamlIdentityProviderConfig();
  }

  @Override
  public SAMLIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {

    return new FaSamlIdentityProvider(session, new FaSamlIdentityProviderConfig(model), destinationValidator);
  }
}
