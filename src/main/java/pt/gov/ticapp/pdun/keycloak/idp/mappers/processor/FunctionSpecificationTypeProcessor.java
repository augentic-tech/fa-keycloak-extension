package pt.gov.ticapp.pdun.keycloak.idp.mappers.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionSpecificationTypeProcessor {

  private static final Pattern CONCAT_FUNCTION = Pattern.compile("concat\\(\\$\\{AttributeValue},'([^,]+)'\\)");

  String process(String value, String selector) {
    final Matcher matcher = CONCAT_FUNCTION.matcher(selector);

    if (matcher.matches()) {
      return value + matcher.group(1);
    } else {
      throw new IllegalArgumentException("'" + selector + "' is malformed!");
    }
  }
}
