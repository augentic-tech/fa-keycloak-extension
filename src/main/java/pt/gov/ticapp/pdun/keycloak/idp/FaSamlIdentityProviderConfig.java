package pt.gov.ticapp.pdun.keycloak.idp;

import org.keycloak.broker.saml.SAMLIdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

/**
 * Based on code from https://github.com/grnet/eidas-keycloak-extension.
 */
public class FaSamlIdentityProviderConfig extends SAMLIdentityProviderConfig {

  public static final String FA_REQUESTED_ATTRIBUTES = "faRequestedAttributes";
  public static final String FA_AA_LEVEL = "faAaLevel";

  public FaSamlIdentityProviderConfig() {

    super();
  }

  public FaSamlIdentityProviderConfig(IdentityProviderModel identityProviderModel) {

    super(identityProviderModel);
  }

  public String getFaRequestedAttributes() {

    return getConfig().get(FA_REQUESTED_ATTRIBUTES);
  }

  public void setFaRequestedAttributes(String requestedAttributes) {

    getConfig().put(FA_REQUESTED_ATTRIBUTES, requestedAttributes);
  }

  public String getFaAaLevel() {

    return getConfig().get(FA_AA_LEVEL);
  }

  public void setFaAaLevel(String faAaLevel) {

    getConfig().put(FA_AA_LEVEL, faAaLevel);
  }
}
