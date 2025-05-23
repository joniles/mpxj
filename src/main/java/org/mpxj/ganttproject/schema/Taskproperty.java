//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2024.04.25 at 10:03:47 AM BST
//

package org.mpxj.ganttproject.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * <p>Java class for taskproperty complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="taskproperty"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="valuetype" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="defaultvalue" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@SuppressWarnings("all") @XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "taskproperty", propOrder =
{
   "value"
}) public class Taskproperty
{

   @XmlValue protected String value;
   @XmlAttribute(name = "id") protected String id;
   @XmlAttribute(name = "name") protected String name;
   @XmlAttribute(name = "type") protected String type;
   @XmlAttribute(name = "valuetype") protected String valuetype;
   @XmlAttribute(name = "defaultvalue") protected String defaultvalue;

   /**
    * Gets the value of the value property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getValue()
   {
      return value;
   }

   /**
    * Sets the value of the value property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * Gets the value of the id property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getId()
   {
      return id;
   }

   /**
    * Sets the value of the id property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setId(String value)
   {
      this.id = value;
   }

   /**
    * Gets the value of the name property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getName()
   {
      return name;
   }

   /**
    * Sets the value of the name property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setName(String value)
   {
      this.name = value;
   }

   /**
    * Gets the value of the type property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getType()
   {
      return type;
   }

   /**
    * Sets the value of the type property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setType(String value)
   {
      this.type = value;
   }

   /**
    * Gets the value of the valuetype property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getValuetype()
   {
      return valuetype;
   }

   /**
    * Sets the value of the valuetype property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setValuetype(String value)
   {
      this.valuetype = value;
   }

   /**
    * Gets the value of the defaultvalue property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getDefaultvalue()
   {
      return defaultvalue;
   }

   /**
    * Sets the value of the defaultvalue property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setDefaultvalue(String value)
   {
      this.defaultvalue = value;
   }

}
