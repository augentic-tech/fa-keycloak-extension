package pt.gov.ticapp.pdun.keycloak.idp.storage;

import org.infinispan.Cache;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.infinispan.InfinispanConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

public class FaUserStorageProvider implements UserStorageProvider, UserLookupProvider, UserRegistrationProvider {

  private static final String CACHE_PREFIX = "CAFEBABE_";

  private final KeycloakSession session;
  private final ComponentModel model;
  private final InfinispanConnectionProvider infinispanConnectionProvider;


  public FaUserStorageProvider(KeycloakSession session, ComponentModel model) {

    this.session = session;
    this.model = model;
    infinispanConnectionProvider = session.getProvider(InfinispanConnectionProvider.class);
  }

  @Override
  public UserModel getUserByUsername(RealmModel realm, String username) {
    FaUser faUser = (FaUser) getCache().get(getCacheKeyFromUsername(username));
    UserModel userModel = null;

    if (faUser != null) {
      userModel = getAndUpdate(realm, faUser);
    } else if (session.getContext().getAuthenticationSession() != null) {
      userModel = createAdapter(realm, username);
    }

    return userModel;
  }

  private static String getCacheKeyFromUsername(String username) {

    return CACHE_PREFIX + username;
  }

  private Cache<Object, Object> getCache() {

    return infinispanConnectionProvider.getCache(InfinispanConnectionProvider.USER_CACHE_NAME);
  }

  private UserModel getAndUpdate(RealmModel realm, FaUser faUser) {
    UserModel userModel;

    if (session.getContext().getAuthenticationSession() != null) {
      userModel = createAdapter(realm, faUser.getUsername());
    } else {
      userModel = new FaUserModel(session, realm, faUser);
    }

    return userModel;
  }

  private UserModel createAdapter(RealmModel realm, String username) {
    final FaUserModel faUserModel = new FaUserModel(session, realm, model, username);

    getCache().put(getCacheKeyFromUsername(username), faUserModel.getFaUser());

    return  faUserModel;
  }

  @Override
  public UserModel getUserById(RealmModel realm, String id) {

    return getUserByUsername(realm, StorageId.externalId(id));
  }

  @Override
  public UserModel getUserByEmail(RealmModel realm, String email) {

    return null;
  }

  @Override
  public UserModel addUser(RealmModel realm, String username) {

    return getUserByUsername(realm, username);
  }

  @Override
  public boolean removeUser(RealmModel realm, UserModel user) {

    return getCache().remove(getCacheKeyFromUsername(user.getUsername())) != null;
  }

  @Override
  public void close() {
  }
}