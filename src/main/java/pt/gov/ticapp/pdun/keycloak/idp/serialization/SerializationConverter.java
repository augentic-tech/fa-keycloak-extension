package pt.gov.ticapp.pdun.keycloak.idp.serialization;

import org.jboss.logging.Logger;
import org.keycloak.broker.saml.SAMLIdentityProvider;
import org.keycloak.util.JsonSerialization;

public class SerializationConverter {

  public static final SerializationConverter INSTANCE = new SerializationConverter();

  private static final Logger LOGGER = Logger.getLogger(SAMLIdentityProvider.class);

  public <T> T getStringSerializedAs(String stringToSerialize, Class<T> convertTo) {

    try {
      return JsonSerialization.readValue(stringToSerialize, convertTo);
    } catch (Exception e) {
      LOGGER.warn("Could not json-deserialize config entry: " + stringToSerialize, e);

      throw new RuntimeException(e);
    }
  }
}
