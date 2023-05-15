package pt.gov.ticapp.pdun.keycloak.idp.mappers.processor;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ValueSelector {

  private static final Pattern PATTERN = Pattern.compile("([^:|]+):?'?([^|]*)'?\\|?(.*)");
  private final String key;
  private final String value;
  private final String fieldToFetchValue;

  ValueSelector(String rawSelector) {
    final Matcher matcher = PATTERN.matcher(rawSelector);

    if (matcher.matches()) {
      key = matcher.group(1);
      value = matcher.group(2).length() > 0 ? matcher.group(2) : null;
      fieldToFetchValue = matcher.group(3).length() > 0 ? matcher.group(3) : null;
    } else {
      throw new IllegalArgumentException("'" + rawSelector + "' is malformed!");
    }
  }

  public String getKey() {

    return key;
  }

  public String getValue() {

    return value;
  }

  public String getFieldToFetchValue() {

    return fieldToFetchValue;
  }

  @Override
  public String toString() {

    return new StringJoiner(", ", ValueSelector.class.getSimpleName() + "[", "]").add("key='" + key + "'")
        .add("value='" + value + "'").add("fieldToFetchValue='" + fieldToFetchValue + "'").toString();
  }
}