package pt.gov.ticapp.pdun.keycloak.idp.storage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserModelDefaultMethods;
import org.keycloak.models.utils.RoleUtils;
import org.keycloak.storage.UserStorageUtil;

public class FaUserModel extends UserModelDefaultMethods {

  private final KeycloakSession session;
  private final RealmModel realm;
  private final FaUser faUser;

  public FaUserModel(KeycloakSession session, RealmModel realm,
      ComponentModel componentModel, String username) {

    this.session = session;
    this.realm = realm;
    faUser = new FaUser(username, session, componentModel);
  }

  public FaUserModel(KeycloakSession session, RealmModel realm, FaUser faUser) {

    this.session = session;
    this.realm = realm;
    this.faUser = faUser;
  }

  @Override
  public String getId() {

    return faUser.getStorageId().getId();
  }

  @Override
  public String getUsername() {

    return faUser.getUsername();
  }

  @Override
  public void setUsername(String username) {

    faUser.setUsername(username);
  }

  @Override
  public Long getCreatedTimestamp() {

    return faUser.getCreatedTimestamp();
  }

  @Override
  public void setCreatedTimestamp(Long timestamp) {

    if (timestamp != null && timestamp >= 0) {
      faUser.setCreatedTimestamp(timestamp);
    }
  }

  @Override
  public boolean isEnabled() {

    return faUser.isEnabled();
  }

  @Override
  public void setEnabled(boolean enabled) {

    faUser.setEnabled(enabled);
  }

  @Override
  public void setSingleAttribute(String name, String value) {

    faUser.setAttribute(name, value);
  }

  @Override
  public void removeAttribute(String name) {

    faUser.setAttribute(name, null);
  }

  @Override
  public void setAttribute(String name, List<String> values) {

    faUser.setAttribute(name, String.join("", values));
  }

  @Override
  public String getFirstAttribute(String name) {

    return faUser.getAttributeValue(name);
  }

  @Override
  public Stream<String> getAttributeStream(String name) {

    return Stream.of(faUser.getAttributeValue(name));
  }

  @Override
  public Map<String, List<String>> getAttributes() {
    MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();

    faUser.getAttributes().forEach(attributes::putSingle);

    return attributes;
  }

  @Override
  public Stream<String> getRequiredActionsStream() {

    return UserStorageUtil.userFederatedStorage(session).getRequiredActionsStream(realm, faUser.getStorageId().getId());
  }

  @Override
  public void addRequiredAction(String action) {

    UserStorageUtil.userFederatedStorage(session).addRequiredAction(realm, faUser.getStorageId().getId(), action);
  }

  @Override
  public void removeRequiredAction(String action) {

    UserStorageUtil.userFederatedStorage(session).addRequiredAction(realm, faUser.getStorageId().getId(), action);
  }

  @Override
  public boolean isEmailVerified() {

    return faUser.isEmailVerified();
  }

  @Override
  public void setEmailVerified(boolean verified) {

    faUser.setEmailVerified(verified);
  }

  @Override
  public Stream<GroupModel> getGroupsStream() {

    return Collections.EMPTY_LIST.stream();
  }

  @Override
  public void joinGroup(GroupModel group) {

    UserStorageUtil.userFederatedStorage(session).joinGroup(realm, this.getId(), group);
  }

  @Override
  public void leaveGroup(GroupModel group) {

    UserStorageUtil.userFederatedStorage(session).leaveGroup(realm, this.getId(), group);
  }

  @Override
  public boolean isMemberOf(GroupModel group) {

    return RoleUtils.isMember(getGroupsStream(), group);
  }

  @Override
  public String getFederationLink() {

    return null;
  }

  @Override
  public void setFederationLink(String link) {
  }

  @Override
  public String getServiceAccountClientLink() {

    return null;
  }

  @Override
  public void setServiceAccountClientLink(String clientInternalId) {
  }

  @Override
  public SubjectCredentialManager credentialManager() {

    return new LegacyUserCredentialManager(session, realm, this);
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof UserModel)) {
      return false;
    }

    UserModel that = (UserModel) o;

    return that.getId().equals(getId());
  }

  @Override
  public int hashCode() {

    return getId().hashCode();
  }

  @Override
  public Stream<RoleModel> getRealmRoleMappingsStream() {

    return Stream.concat(UserStorageUtil.userFederatedStorage(session).getRoleMappingsStream(realm, this.getId()),
        realm.getDefaultRole().getCompositesStream()).filter(RoleUtils::isRealmRole);
  }

  @Override
  public Stream<RoleModel> getClientRoleMappingsStream(ClientModel clientModel) {

    return getRealmRoleMappingsStream().filter(r -> RoleUtils.isClientRole(r, clientModel));
  }

  @Override
  public boolean hasRole(RoleModel role) {

    return RoleUtils.hasRole(getRoleMappingsStream(), role)
        || RoleUtils.hasRoleFromGroup(getGroupsStream(), role, true);
  }

  @Override
  public void grantRole(RoleModel role) {

    UserStorageUtil.userFederatedStorage(session).grantRole(realm, this.getId(), role);
  }


  @Override
  public Stream<RoleModel> getRoleMappingsStream() {

    return Stream.concat(
        UserStorageUtil.userFederatedStorage(session).getRoleMappingsStream(realm, this.getId()),
        realm.getDefaultRole().getCompositesStream());
  }

  @Override
  public void deleteRoleMapping(RoleModel role) {

    UserStorageUtil.userFederatedStorage(session).deleteRoleMapping(realm, this.getId(), role);
  }

  public FaUser getFaUser() {

    return faUser;
  }
}
