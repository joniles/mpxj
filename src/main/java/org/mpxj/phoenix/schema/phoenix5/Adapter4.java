//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2025.04.09 at 09:56:52 AM BST
//

package org.mpxj.phoenix.schema.phoenix5;

import java.util.UUID;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.mpxj.phoenix.DatatypeConverter;

public class Adapter4
         extends
            XmlAdapter<String, UUID>
{

   @Override public UUID unmarshal(String value)
   {
      return (DatatypeConverter.parseUUID(value));
   }

   @Override public String marshal(UUID value)
   {
      return (DatatypeConverter.printUUID(value));
   }

}
