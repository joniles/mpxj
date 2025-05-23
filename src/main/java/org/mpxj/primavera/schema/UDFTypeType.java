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
 * <p>Java class for UDFTypeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UDFTypeType"&gt;
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
 *         &lt;element name="DataType" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="Text"/&gt;
 *               &lt;enumeration value="Start Date"/&gt;
 *               &lt;enumeration value="Finish Date"/&gt;
 *               &lt;enumeration value="Cost"/&gt;
 *               &lt;enumeration value="Double"/&gt;
 *               &lt;enumeration value="Integer"/&gt;
 *               &lt;enumeration value="Indicator"/&gt;
 *               &lt;enumeration value="Code"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DisplayIndicatorFlag" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Formula" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4000"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IsCalculated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsConditional" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsSecureCode" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="LastUpdateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="LastUpdateUser" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SubjectArea" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="Activity"/&gt;
 *               &lt;enumeration value="Activity Expense"/&gt;
 *               &lt;enumeration value="Activity Step"/&gt;
 *               &lt;enumeration value="Project"/&gt;
 *               &lt;enumeration value="Project Issue"/&gt;
 *               &lt;enumeration value="Project Risk"/&gt;
 *               &lt;enumeration value="Resource"/&gt;
 *               &lt;enumeration value="Resource Assignment"/&gt;
 *               &lt;enumeration value="WBS"/&gt;
 *               &lt;enumeration value="Work Products and Documents"/&gt;
 *               &lt;enumeration value="Activity Step Template Item"/&gt;
 *               &lt;enumeration value="Lean Task"/&gt;
 *               &lt;enumeration value="Roles"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SummaryMethod" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Title" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "UDFTypeType", propOrder =
{
   "createDate",
   "createUser",
   "dataType",
   "displayIndicatorFlag",
   "formula",
   "isCalculated",
   "isConditional",
   "isSecureCode",
   "lastUpdateDate",
   "lastUpdateUser",
   "objectId",
   "subjectArea",
   "summaryMethod",
   "title"
}) public class UDFTypeType
{

   @XmlElement(name = "CreateDate", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter4.class) @XmlSchemaType(name = "dateTime") protected LocalDateTime createDate;
   @XmlElement(name = "CreateUser") @XmlJavaTypeAdapter(Adapter1.class) protected String createUser;
   @XmlElement(name = "DataType") @XmlJavaTypeAdapter(Adapter1.class) protected String dataType;
   @XmlElement(name = "DisplayIndicatorFlag", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean displayIndicatorFlag;
   @XmlElement(name = "Formula") @XmlJavaTypeAdapter(Adapter1.class) protected String formula;
   @XmlElement(name = "IsCalculated", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean isCalculated;
   @XmlElement(name = "IsConditional", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean isConditional;
   @XmlElement(name = "IsSecureCode", type = String.class) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean isSecureCode;
   @XmlElement(name = "LastUpdateDate", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter4.class) @XmlSchemaType(name = "dateTime") protected LocalDateTime lastUpdateDate;
   @XmlElement(name = "LastUpdateUser") @XmlJavaTypeAdapter(Adapter1.class) protected String lastUpdateUser;
   @XmlElement(name = "ObjectId") protected Integer objectId;
   @XmlElement(name = "SubjectArea") @XmlJavaTypeAdapter(Adapter1.class) protected String subjectArea;
   @XmlElement(name = "SummaryMethod") @XmlJavaTypeAdapter(Adapter1.class) protected String summaryMethod;
   @XmlElement(name = "Title") @XmlJavaTypeAdapter(Adapter1.class) protected String title;

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
    * Gets the value of the dataType property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getDataType()
   {
      return dataType;
   }

   /**
    * Sets the value of the dataType property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setDataType(String value)
   {
      this.dataType = value;
   }

   /**
    * Gets the value of the displayIndicatorFlag property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isDisplayIndicatorFlag()
   {
      return displayIndicatorFlag;
   }

   /**
    * Sets the value of the displayIndicatorFlag property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setDisplayIndicatorFlag(Boolean value)
   {
      this.displayIndicatorFlag = value;
   }

   /**
    * Gets the value of the formula property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getFormula()
   {
      return formula;
   }

   /**
    * Sets the value of the formula property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setFormula(String value)
   {
      this.formula = value;
   }

   /**
    * Gets the value of the isCalculated property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isIsCalculated()
   {
      return isCalculated;
   }

   /**
    * Sets the value of the isCalculated property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setIsCalculated(Boolean value)
   {
      this.isCalculated = value;
   }

   /**
    * Gets the value of the isConditional property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isIsConditional()
   {
      return isConditional;
   }

   /**
    * Sets the value of the isConditional property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setIsConditional(Boolean value)
   {
      this.isConditional = value;
   }

   /**
    * Gets the value of the isSecureCode property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isIsSecureCode()
   {
      return isSecureCode;
   }

   /**
    * Sets the value of the isSecureCode property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setIsSecureCode(Boolean value)
   {
      this.isSecureCode = value;
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
    * Gets the value of the subjectArea property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getSubjectArea()
   {
      return subjectArea;
   }

   /**
    * Sets the value of the subjectArea property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setSubjectArea(String value)
   {
      this.subjectArea = value;
   }

   /**
    * Gets the value of the summaryMethod property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getSummaryMethod()
   {
      return summaryMethod;
   }

   /**
    * Sets the value of the summaryMethod property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setSummaryMethod(String value)
   {
      this.summaryMethod = value;
   }

   /**
    * Gets the value of the title property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * Sets the value of the title property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setTitle(String value)
   {
      this.title = value;
   }

}
