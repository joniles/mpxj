//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2024.04.25 at 10:03:49 AM BST
//

package net.sf.mpxj.conceptdraw.schema;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import net.sf.mpxj.TimeUnit;

public class Adapter8 extends XmlAdapter<String, TimeUnit>
{

   @Override public TimeUnit unmarshal(String value)
   {
      return (net.sf.mpxj.conceptdraw.DatatypeConverter.parseTimeUnit(value));
   }

   @Override public String marshal(TimeUnit value)
   {
      return (net.sf.mpxj.conceptdraw.DatatypeConverter.printTimeUnit(value));
   }

}
