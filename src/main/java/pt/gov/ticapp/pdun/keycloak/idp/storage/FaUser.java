package pt.gov.ticapp.pdun.keycloak.idp.storage;

import java.util.HashMap;
import java.util.Map;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.Constants;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import pt.gov.ticapp.pdun.keycloak.idp.mappers.FaSamlUserAttributeMapper;

public class FaUser {

  private final Map<String, String> attributes;
  private String username;
  private StorageId storageId;
  private Long createdTimestamp;
  private boolean emailVerified;
  private boolean enabled;

  public FaUser(String username, KeycloakSession session, ComponentModel componentModel) {

    this.username = username;
    createdTimestamp = System.currentTimeMillis();
    storageId = new StorageId(componentModel.getId(), getUsername());
    emailVerified = true;
    enabled = true;
    attributes = getAttributes(session);
  }


  private Map<String, String> getAttributes(KeycloakSession session) {
    Map<String, String> attributes = new HashMap<>();

    if (session.getContext().getAuthenticationSession() != null) {
      attributes.put(UserModel.USERNAME, username);
      attributes.put(UserModel.FIRST_NAME, session.getContext().getAuthenticationSession()
          .getClientNote(FaSamlUserAttributeMapper.CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_FIRST_NAME_KEY));
      attributes.put(UserModel.EMAIL, session.getContext().getAuthenticationSession()
          .getClientNote(FaSamlUserAttributeMapper.CONTEXT_CLIENT_NOTES_KEY_USER_ATTRIBUTE_EMAIL_KEY));
      attributes.put(UserModel.LAST_NAME, session.getContext().getAuthenticationSession()
          .getClientNote(FaSamlUserAttributeMapper.CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_LAST_NAME_KEY));
      addAdditionalUserAttributes(attributes, session.getContext().getAuthenticationSession().getClientNotes());
    } else {
      throw new IllegalArgumentException("No current authentication Session!");
    }

    return attributes;
  }

  private void addAdditionalUserAttributes(Map<String, String> attributes, Map<String, String> clientNotes) {

    clientNotes.entrySet().stream().filter(entry -> entry.getKey().startsWith(Constants.USER_ATTRIBUTES_PREFIX))
        .forEach(entry -> attributes.put(entry.getKey().substring(Constants.USER_ATTRIBUTES_PREFIX.length()),
            entry.getValue()));
  }

  public String getUsername() {

    return username;
  }

  public void setUsername(String username) {

    this.username = username;
  }

  public StorageId getStorageId() {

    return storageId;
  }

  public void setStorageId(StorageId storageId) {

    this.storageId = storageId;
  }

  public Long getCreatedTimestamp() {

    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {

    this.createdTimestamp = createdTimestamp;
  }

  public boolean isEmailVerified() {

    return emailVerified;
  }

  public void setEmailVerified(boolean emailVerified) {

    this.emailVerified = emailVerified;
  }

  public boolean isEnabled() {

    return enabled;
  }

  public void setEnabled(boolean enabled) {

    this.enabled = enabled;
  }

  public String getAttributeValue(String attribute) {

    return attributes.get(attribute);
  }

  public void setAttribute(String name, String value) {

    attributes.put(name, value);
  }

  public Map<String, String> getAttributes() {

    return attributes;
  }
}
