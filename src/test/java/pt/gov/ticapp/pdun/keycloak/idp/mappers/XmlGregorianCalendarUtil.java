package pt.gov.ticapp.pdun.keycloak.idp.mappers;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XmlGregorianCalendarUtil {

  static XMLGregorianCalendar createXmlGregorianCalendar() throws DatatypeConfigurationException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

    XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance()
        .newXMLGregorianCalendar(dateFormat.format(new Date()));

    xmlGregorianCalendar.setTimezone(0);

    return xmlGregorianCalendar;
  }
}
