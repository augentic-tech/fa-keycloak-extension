package pt.gov.ticapp.pdun.keycloak.idp.saml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.common.util.StaxUtil;
import pt.gov.ticapp.pdun.keycloak.idp.FaSamlIdentityProviderConfig;
import pt.gov.ticapp.pdun.keycloak.idp.configuration.PropertyReader;
import pt.gov.ticapp.pdun.keycloak.idp.serialization.SerializationConverter;

/**
 * Based on code from https://github.com/grnet/eidas-keycloak-extension.
 */
public class FaAuthnExtensionGenerator implements SamlProtocolExtensionsAwareBuilder.NodeGenerator {

  private final FaSamlIdentityProviderConfig faSamlIdentityProviderConfig;

  public FaAuthnExtensionGenerator(FaSamlIdentityProviderConfig faSamlIdentityProviderConfig) {

    this.faSamlIdentityProviderConfig = faSamlIdentityProviderConfig;
  }

  @Override
  public void write(XMLStreamWriter xmlStreamWriter) throws ProcessingException {

    writeRequestedAttributes(xmlStreamWriter);
    writeFaAaLevel(xmlStreamWriter);
    StaxUtil.flush(xmlStreamWriter);
  }

  private void writeFaAaLevel(XMLStreamWriter xmlStreamWriter) throws ProcessingException {

    StaxUtil.writeStartElement(xmlStreamWriter, PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaPrefix(),
        PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaFaAaLevelTag(),
        PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaXmlNs());
    StaxUtil.writeNameSpace(xmlStreamWriter, PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaPrefix(),
        PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaXmlNs());
    StaxUtil.writeCharacters(xmlStreamWriter, getFaAaLevel());
    StaxUtil.writeEndElement(xmlStreamWriter);
  }

  private String getFaAaLevel() {

    return StringUtils.defaultIfBlank(faSamlIdentityProviderConfig.getFaAaLevel(),
        PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaFaAaLevelDefaultValue());
  }

  private void writeRequestedAttributes(XMLStreamWriter xmlStreamWriter) throws ProcessingException {
    List<RequestedAttribute> requestedAttributes = getRequestedAttributes();
    final Map<String, List<RequestedAttribute>> requestedAttributesByXmlNs = requestedAttributes.stream().collect(
        Collectors.groupingBy(RequestedAttribute::getXmlns,
            Collectors.mapping(requestedAttribute -> requestedAttribute, Collectors.toList())));

    for (Entry<String, List<RequestedAttribute>> entry : requestedAttributesByXmlNs.entrySet()) {
      StaxUtil.writeStartElement(xmlStreamWriter, PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaPrefix(),
          PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaRequestedAttributesTag(),
          PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaXmlNs());
      StaxUtil.writeNameSpace(xmlStreamWriter, PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaPrefix(),
          PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaXmlNs());

      for (RequestedAttribute requestedAttribute : entry.getValue()) {
        StaxUtil.writeStartElement(xmlStreamWriter,
            PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaPrefix(),
            PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeTag(),
            PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaXmlNs());
        StaxUtil.writeAttribute(xmlStreamWriter,
            PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeAttributeName(),
            requestedAttribute.getName());
        StaxUtil.writeAttribute(xmlStreamWriter,
            PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeAttributeNameFormat(),
            requestedAttribute.getFormat());
        StaxUtil.writeAttribute(xmlStreamWriter,
            PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeAttributeIsRequired(),
            requestedAttribute.getRequired());
        StaxUtil.writeEndElement(xmlStreamWriter);
      }

      StaxUtil.writeEndElement(xmlStreamWriter);
    }
  }

  private List<RequestedAttribute> getRequestedAttributes() {
    List<RequestedAttribute> requestedAttributes = new ArrayList<>(0);

    if (StringUtils.isNotBlank(faSamlIdentityProviderConfig.getFaRequestedAttributes())) {
      requestedAttributes = Arrays.asList(
          SerializationConverter.INSTANCE.getStringSerializedAs(faSamlIdentityProviderConfig.getFaRequestedAttributes(),
              RequestedAttribute[].class));

      requestedAttributes.forEach(ra -> {
        if (StringUtils.isBlank(ra.getXmlns())) {
          ra.setXmlns(PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaXmlNs());
        }
        if (StringUtils.isBlank(ra.getFormat())) {
          ra.setFormat(PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaFormatDefaultValue());
        }
        if (StringUtils.isBlank(ra.getRequired())) {
          ra.setRequired(PropertyReader.INSTANCE.getFaSamlIdentityProviderExtensionFaRequiredDefaultValue());
        }
      });
    }

    return requestedAttributes;
  }
}
