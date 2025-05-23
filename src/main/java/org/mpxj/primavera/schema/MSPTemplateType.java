//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2025.01.02 at 04:27:10 PM GMT
//

package org.mpxj.primavera.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>Java class for MSPTemplateType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MSPTemplateType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MSPTemplateType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ViewData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "MSPTemplateType", propOrder =
{
   "mspTemplateType",
   "name",
   "objectId",
   "viewData"
}) public class MSPTemplateType
{

   @XmlElement(name = "MSPTemplateType") @XmlJavaTypeAdapter(Adapter1.class) protected String mspTemplateType;
   @XmlElement(name = "Name") @XmlJavaTypeAdapter(Adapter1.class) protected String name;
   @XmlElement(name = "ObjectId") protected Integer objectId;
   @XmlElement(name = "ViewData") @XmlJavaTypeAdapter(Adapter1.class) protected String viewData;

   /**
    * Gets the value of the mspTemplateType property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getMSPTemplateType()
   {
      return mspTemplateType;
   }

   /**
    * Sets the value of the mspTemplateType property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setMSPTemplateType(String value)
   {
      this.mspTemplateType = value;
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
    * Gets the value of the objectId property.
    *
    * @return
    *     possible object is
    *     {@link Integer }
    *
    */
   public Integer getObjectId()
   {
      return objectId;
   }

   /**
    * Sets the value of the objectId property.
    *
    * @param value
    *     allowed object is
    *     {@link Integer }
    *
    */
   public void setObjectId(Integer value)
   {
      this.objectId = value;
   }

   /**
    * Gets the value of the viewData property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getViewData()
   {
      return viewData;
   }

   /**
    * Sets the value of the viewData property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setViewData(String value)
   {
      this.viewData = value;
   }

}
