//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2025.01.02 at 04:27:10 PM GMT
//

package org.mpxj.primavera.schema;

import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>Java class for ResourceAssignmentCodeAssignmentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ResourceAssignmentCodeAssignmentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CreateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="CreateUser" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LastUpdateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="LastUpdateUser" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ProjectObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ResourceAssignmentCodeDescription" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ResourceAssignmentCodeObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ResourceAssignmentCodeTypeName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ResourceAssignmentCodeTypeObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ResourceAssignmentCodeValue" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ResourceAssignmentObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "ResourceAssignmentCodeAssignmentType", propOrder =
{
   "createDate",
   "createUser",
   "lastUpdateDate",
   "lastUpdateUser",
   "projectObjectId",
   "resourceAssignmentCodeDescription",
   "resourceAssignmentCodeObjectId",
   "resourceAssignmentCodeTypeName",
   "resourceAssignmentCodeTypeObjectId",
   "resourceAssignmentCodeValue",
   "resourceAssignmentObjectId"
}) public class ResourceAssignmentCodeAssignmentType
{

   @XmlElement(name = "CreateDate", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter4.class) @XmlSchemaType(name = "dateTime") protected LocalDateTime createDate;
   @XmlElement(name = "CreateUser") @XmlJavaTypeAdapter(Adapter1.class) protected String createUser;
   @XmlElement(name = "LastUpdateDate", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter4.class) @XmlSchemaType(name = "dateTime") protected LocalDateTime lastUpdateDate;
   @XmlElement(name = "LastUpdateUser") @XmlJavaTypeAdapter(Adapter1.class) protected String lastUpdateUser;
   @XmlElement(name = "ProjectObjectId") protected Integer projectObjectId;
   @XmlElement(name = "ResourceAssignmentCodeDescription") @XmlJavaTypeAdapter(Adapter1.class) protected String resourceAssignmentCodeDescription;
   @XmlElement(name = "ResourceAssignmentCodeObjectId") protected Integer resourceAssignmentCodeObjectId;
   @XmlElement(name = "ResourceAssignmentCodeTypeName") @XmlJavaTypeAdapter(Adapter1.class) protected String resourceAssignmentCodeTypeName;
   @XmlElement(name = "ResourceAssignmentCodeTypeObjectId") protected Integer resourceAssignmentCodeTypeObjectId;
   @XmlElement(name = "ResourceAssignmentCodeValue") @XmlJavaTypeAdapter(Adapter1.class) protected String resourceAssignmentCodeValue;
   @XmlElement(name = "ResourceAssignmentObjectId") protected Integer resourceAssignmentObjectId;

   /**
    * Gets the value of the createDate property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public LocalDateTime getCreateDate()
   {
      return createDate;
   }

   /**
    * Sets the value of the createDate property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setCreateDate(LocalDateTime value)
   {
      this.createDate = value;
   }

   /**
    * Gets the value of the createUser property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getCreateUser()
   {
      return createUser;
   }

   /**
    * Sets the value of the createUser property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setCreateUser(String value)
   {
      this.createUser = value;
   }

   /**
    * Gets the value of the lastUpdateDate property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public LocalDateTime getLastUpdateDate()
   {
      return lastUpdateDate;
   }

   /**
    * Sets the value of the lastUpdateDate property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setLastUpdateDate(LocalDateTime value)
   {
      this.lastUpdateDate = value;
   }

   /**
    * Gets the value of the lastUpdateUser property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getLastUpdateUser()
   {
      return lastUpdateUser;
   }

   /**
    * Sets the value of the lastUpdateUser property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setLastUpdateUser(String value)
   {
      this.lastUpdateUser = value;
   }

   /**
    * Gets the value of the projectObjectId property.
    *
    * @return
    *     possible object is
    *     {@link Integer }
    *
    */
   public Integer getProjectObjectId()
   {
      return projectObjectId;
   }

   /**
    * Sets the value of the projectObjectId property.
    *
    * @param value
    *     allowed object is
    *     {@link Integer }
    *
    */
   public void setProjectObjectId(Integer value)
   {
      this.projectObjectId = value;
   }

   /**
    * Gets the value of the resourceAssignmentCodeDescription property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getResourceAssignmentCodeDescription()
   {
      return resourceAssignmentCodeDescription;
   }

   /**
    * Sets the value of the resourceAssignmentCodeDescription property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setResourceAssignmentCodeDescription(String value)
   {
      this.resourceAssignmentCodeDescription = value;
   }

   /**
    * Gets the value of the resourceAssignmentCodeObjectId property.
    *
    * @return
    *     possible object is
    *     {@link Integer }
    *
    */
   public Integer getResourceAssignmentCodeObjectId()
   {
      return resourceAssignmentCodeObjectId;
   }

   /**
    * Sets the value of the resourceAssignmentCodeObjectId property.
    *
    * @param value
    *     allowed object is
    *     {@link Integer }
    *
    */
   public void setResourceAssignmentCodeObjectId(Integer value)
   {
      this.resourceAssignmentCodeObjectId = value;
   }

   /**
    * Gets the value of the resourceAssignmentCodeTypeName property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getResourceAssignmentCodeTypeName()
   {
      return resourceAssignmentCodeTypeName;
   }

   /**
    * Sets the value of the resourceAssignmentCodeTypeName property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setResourceAssignmentCodeTypeName(String value)
   {
      this.resourceAssignmentCodeTypeName = value;
   }

   /**
    * Gets the value of the resourceAssignmentCodeTypeObjectId property.
    *
    * @return
    *     possible object is
    *     {@link Integer }
    *
    */
   public Integer getResourceAssignmentCodeTypeObjectId()
   {
      return resourceAssignmentCodeTypeObjectId;
   }

   /**
    * Sets the value of the resourceAssignmentCodeTypeObjectId property.
    *
    * @param value
    *     allowed object is
    *     {@link Integer }
    *
    */
   public void setResourceAssignmentCodeTypeObjectId(Integer value)
   {
      this.resourceAssignmentCodeTypeObjectId = value;
   }

   /**
    * Gets the value of the resourceAssignmentCodeValue property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getResourceAssignmentCodeValue()
   {
      return resourceAssignmentCodeValue;
   }

   /**
    * Sets the value of the resourceAssignmentCodeValue property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setResourceAssignmentCodeValue(String value)
   {
      this.resourceAssignmentCodeValue = value;
   }

   /**
    * Gets the value of the resourceAssignmentObjectId property.
    *
    * @return
    *     possible object is
    *     {@link Integer }
    *
    */
   public Integer getResourceAssignmentObjectId()
   {
      return resourceAssignmentObjectId;
   }

   /**
    * Sets the value of the resourceAssignmentObjectId property.
    *
    * @param value
    *     allowed object is
    *     {@link Integer }
    *
    */
   public void setResourceAssignmentObjectId(Integer value)
   {
      this.resourceAssignmentObjectId = value;
   }

}
