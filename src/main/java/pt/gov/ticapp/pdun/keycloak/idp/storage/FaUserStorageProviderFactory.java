package pt.gov.ticapp.pdun.keycloak.idp.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class FaUserStorageProviderFactory implements UserStorageProviderFactory<FaUserStorageProvider> {

  public static final String PROVIDER_NAME = "fa-user-storage";

  @Override
  public String getId() {

    return PROVIDER_NAME;
  }

  @Override
  public FaUserStorageProvider create(KeycloakSession session, ComponentModel model) {

    return new FaUserStorageProvider(session, model);
  }

}
