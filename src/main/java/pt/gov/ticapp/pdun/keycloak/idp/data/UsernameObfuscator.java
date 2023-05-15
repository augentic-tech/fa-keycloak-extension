package pt.gov.ticapp.pdun.keycloak.idp.data;

import com.google.common.hash.Hashing;
import java.nio.charset.Charset;

public class UsernameObfuscator {

  public static String obfuscateUsername(String username) {

    return Hashing.sha512().hashString(username, Charset.defaultCharset()).toString();
  }
}
