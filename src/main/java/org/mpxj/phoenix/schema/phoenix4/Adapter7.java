//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2024.07.22 at 12:22:03 PM BST
//

package org.mpxj.phoenix.schema.phoenix4;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.mpxj.ResourceType;
import org.mpxj.phoenix.DatatypeConverter;

public class Adapter7
         extends
            XmlAdapter<String, ResourceType>
{

   @Override public ResourceType unmarshal(String value)
   {
      return (DatatypeConverter.parseResourceType(value));
   }

   @Override public String marshal(ResourceType value)
   {
      return (DatatypeConverter.printResourceType(value));
   }

}
