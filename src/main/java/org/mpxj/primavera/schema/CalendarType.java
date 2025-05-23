//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2025.01.02 at 04:27:10 PM GMT
//

package org.mpxj.primavera.schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>Java class for CalendarType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CalendarType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BaseCalendarObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="CreateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="CreateUser" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="HoursPerDay" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *               &lt;minInclusive value="1.0"/&gt;
 *               &lt;maxInclusive value="24.0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="HoursPerMonth" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *               &lt;minInclusive value="1.0"/&gt;
 *               &lt;maxInclusive value="744.0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="HoursPerWeek" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *               &lt;minInclusive value="1.0"/&gt;
 *               &lt;maxInclusive value="168.0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="HoursPerYear" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *               &lt;minInclusive value="1.0"/&gt;
 *               &lt;maxInclusive value="8784.0"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IsBaseline" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsPersonal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsTemplate" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="LastUpdateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="LastUpdateUser" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Name" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ProjectId" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ProjectObjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Type" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="Global"/&gt;
 *               &lt;enumeration value="Project"/&gt;
 *               &lt;enumeration value="Resource"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="StandardWorkWeek" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="StandardWorkHours" maxOccurs="7" minOccurs="7"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="DayOfWeek"&gt;
 *                               &lt;simpleType&gt;
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                   &lt;enumeration value="Sunday"/&gt;
 *                                   &lt;enumeration value="Monday"/&gt;
 *                                   &lt;enumeration value="Tuesday"/&gt;
 *                                   &lt;enumeration value="Wednesday"/&gt;
 *                                   &lt;enumeration value="Thursday"/&gt;
 *                                   &lt;enumeration value="Friday"/&gt;
 *                                   &lt;enumeration value="Saturday"/&gt;
 *                                 &lt;/restriction&gt;
 *                               &lt;/simpleType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="WorkTime" type="{http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects}WorkTimeType" maxOccurs="24"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="HolidayOrExceptions" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="HolidayOrException" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *                             &lt;element name="WorkTime" type="{http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects}WorkTimeType" maxOccurs="24"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "CalendarType", propOrder =
{
   "baseCalendarObjectId",
   "createDate",
   "createUser",
   "hoursPerDay",
   "hoursPerMonth",
   "hoursPerWeek",
   "hoursPerYear",
   "isBaseline",
   "isDefault",
   "isPersonal",
   "isTemplate",
   "lastUpdateDate",
   "lastUpdateUser",
   "name",
   "objectId",
   "projectId",
   "projectObjectId",
   "type",
   "standardWorkWeek",
   "holidayOrExceptions"
}) public class CalendarType
{

   @XmlElement(name = "BaseCalendarObjectId", nillable = true) protected Integer baseCalendarObjectId;
   @XmlElement(name = "CreateDate", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter4.class) @XmlSchemaType(name = "dateTime") protected LocalDateTime createDate;
   @XmlElement(name = "CreateUser") @XmlJavaTypeAdapter(Adapter1.class) protected String createUser;
   @XmlElement(name = "HoursPerDay", type = String.class) @XmlJavaTypeAdapter(Adapter3.class) protected Double hoursPerDay;
   @XmlElement(name = "HoursPerMonth", type = String.class) @XmlJavaTypeAdapter(Adapter3.class) protected Double hoursPerMonth;
   @XmlElement(name = "HoursPerWeek", type = String.class) @XmlJavaTypeAdapter(Adapter3.class) protected Double hoursPerWeek;
   @XmlElement(name = "HoursPerYear", type = String.class) @XmlJavaTypeAdapter(Adapter3.class) protected Double hoursPerYear;
   @XmlElement(name = "IsBaseline", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean isBaseline;
   @XmlElement(name = "IsDefault", type = String.class) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean isDefault;
   @XmlElement(name = "IsPersonal", type = String.class) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean isPersonal;
   @XmlElement(name = "IsTemplate", type = String.class) @XmlJavaTypeAdapter(Adapter2.class) @XmlSchemaType(name = "boolean") protected Boolean isTemplate;
   @XmlElement(name = "LastUpdateDate", type = String.class, nillable = true) @XmlJavaTypeAdapter(Adapter4.class) @XmlSchemaType(name = "dateTime") protected LocalDateTime lastUpdateDate;
   @XmlElement(name = "LastUpdateUser") @XmlJavaTypeAdapter(Adapter1.class) protected String lastUpdateUser;
   @XmlElement(name = "Name") @XmlJavaTypeAdapter(Adapter1.class) protected String name;
   @XmlElement(name = "ObjectId") protected Integer objectId;
   @XmlElement(name = "ProjectId") @XmlJavaTypeAdapter(Adapter1.class) protected String projectId;
   @XmlElement(name = "ProjectObjectId", nillable = true) protected Integer projectObjectId;
   @XmlElement(name = "Type") @XmlJavaTypeAdapter(Adapter1.class) protected String type;
   @XmlElement(name = "StandardWorkWeek") protected CalendarType.StandardWorkWeek standardWorkWeek;
   @XmlElement(name = "HolidayOrExceptions") protected CalendarType.HolidayOrExceptions holidayOrExceptions;

   /**
    * Gets the value of the baseCalendarObjectId property.
    *
    * @return
    *     possible object is
    *     {@link Integer }
    *
    */
   public Integer getBaseCalendarObjectId()
   {
      return baseCalendarObjectId;
   }

   /**
    * Sets the value of the baseCalendarObjectId property.
    *
    * @param value
    *     allowed object is
    *     {@link Integer }
    *
    */
   public void setBaseCalendarObjectId(Integer value)
   {
      this.baseCalendarObjectId = value;
   }

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
    * Gets the value of the hoursPerDay property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Double getHoursPerDay()
   {
      return hoursPerDay;
   }

   /**
    * Sets the value of the hoursPerDay property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setHoursPerDay(Double value)
   {
      this.hoursPerDay = value;
   }

   /**
    * Gets the value of the hoursPerMonth property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Double getHoursPerMonth()
   {
      return hoursPerMonth;
   }

   /**
    * Sets the value of the hoursPerMonth property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setHoursPerMonth(Double value)
   {
      this.hoursPerMonth = value;
   }

   /**
    * Gets the value of the hoursPerWeek property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Double getHoursPerWeek()
   {
      return hoursPerWeek;
   }

   /**
    * Sets the value of the hoursPerWeek property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setHoursPerWeek(Double value)
   {
      this.hoursPerWeek = value;
   }

   /**
    * Gets the value of the hoursPerYear property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Double getHoursPerYear()
   {
      return hoursPerYear;
   }

   /**
    * Sets the value of the hoursPerYear property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setHoursPerYear(Double value)
   {
      this.hoursPerYear = value;
   }

   /**
    * Gets the value of the isBaseline property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isIsBaseline()
   {
      return isBaseline;
   }

   /**
    * Sets the value of the isBaseline property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setIsBaseline(Boolean value)
   {
      this.isBaseline = value;
   }

   /**
    * Gets the value of the isDefault property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isIsDefault()
   {
      return isDefault;
   }

   /**
    * Sets the value of the isDefault property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setIsDefault(Boolean value)
   {
      this.isDefault = value;
   }

   /**
    * Gets the value of the isPersonal property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isIsPersonal()
   {
      return isPersonal;
   }

   /**
    * Sets the value of the isPersonal property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setIsPersonal(Boolean value)
   {
      this.isPersonal = value;
   }

   /**
    * Gets the value of the isTemplate property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public Boolean isIsTemplate()
   {
      return isTemplate;
   }

   /**
    * Sets the value of the isTemplate property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setIsTemplate(Boolean value)
   {
      this.isTemplate = value;
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
    * Gets the value of the projectId property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getProjectId()
   {
      return projectId;
   }

   /**
    * Sets the value of the projectId property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setProjectId(String value)
   {
      this.projectId = value;
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
    * Gets the value of the standardWorkWeek property.
    *
    * @return
    *     possible object is
    *     {@link CalendarType.StandardWorkWeek }
    *
    */
   public CalendarType.StandardWorkWeek getStandardWorkWeek()
   {
      return standardWorkWeek;
   }

   /**
    * Sets the value of the standardWorkWeek property.
    *
    * @param value
    *     allowed object is
    *     {@link CalendarType.StandardWorkWeek }
    *
    */
   public void setStandardWorkWeek(CalendarType.StandardWorkWeek value)
   {
      this.standardWorkWeek = value;
   }

   /**
    * Gets the value of the holidayOrExceptions property.
    *
    * @return
    *     possible object is
    *     {@link CalendarType.HolidayOrExceptions }
    *
    */
   public CalendarType.HolidayOrExceptions getHolidayOrExceptions()
   {
      return holidayOrExceptions;
   }

   /**
    * Sets the value of the holidayOrExceptions property.
    *
    * @param value
    *     allowed object is
    *     {@link CalendarType.HolidayOrExceptions }
    *
    */
   public void setHolidayOrExceptions(CalendarType.HolidayOrExceptions value)
   {
      this.holidayOrExceptions = value;
   }

   /**
    * <p>Java class for anonymous complex type.
    *
    * <p>The following schema fragment specifies the expected content contained within this class.
    *
    * <pre>
    * &lt;complexType&gt;
    *   &lt;complexContent&gt;
    *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
    *       &lt;sequence&gt;
    *         &lt;element name="HolidayOrException" maxOccurs="unbounded"&gt;
    *           &lt;complexType&gt;
    *             &lt;complexContent&gt;
    *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
    *                 &lt;sequence&gt;
    *                   &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
    *                   &lt;element name="WorkTime" type="{http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects}WorkTimeType" maxOccurs="24"/&gt;
    *                 &lt;/sequence&gt;
    *               &lt;/restriction&gt;
    *             &lt;/complexContent&gt;
    *           &lt;/complexType&gt;
    *         &lt;/element&gt;
    *       &lt;/sequence&gt;
    *     &lt;/restriction&gt;
    *   &lt;/complexContent&gt;
    * &lt;/complexType&gt;
    * </pre>
    *
    *
    */
   @XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "", propOrder =
   {
      "holidayOrException"
   }) public static class HolidayOrExceptions
   {

      @XmlElement(name = "HolidayOrException", required = true) protected List<CalendarType.HolidayOrExceptions.HolidayOrException> holidayOrException;

      /**
       * Gets the value of the holidayOrException property.
       *
       * <p>
       * This accessor method returns a reference to the live list,
       * not a snapshot. Therefore any modification you make to the
       * returned list will be present inside the Jakarta XML Binding object.
       * This is why there is not a <CODE>set</CODE> method for the holidayOrException property.
       *
       * <p>
       * For example, to add a new item, do as follows:
       * <pre>
       *    getHolidayOrException().add(newItem);
       * </pre>
       *
       *
       * <p>
       * Objects of the following type(s) are allowed in the list
       * {@link CalendarType.HolidayOrExceptions.HolidayOrException }
       *
       *
       */
      public List<CalendarType.HolidayOrExceptions.HolidayOrException> getHolidayOrException()
      {
         if (holidayOrException == null)
         {
            holidayOrException = new ArrayList<>();
         }
         return this.holidayOrException;
      }

      /**
       * <p>Java class for anonymous complex type.
       *
       * <p>The following schema fragment specifies the expected content contained within this class.
       *
       * <pre>
       * &lt;complexType&gt;
       *   &lt;complexContent&gt;
       *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
       *       &lt;sequence&gt;
       *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
       *         &lt;element name="WorkTime" type="{http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects}WorkTimeType" maxOccurs="24"/&gt;
       *       &lt;/sequence&gt;
       *     &lt;/restriction&gt;
       *   &lt;/complexContent&gt;
       * &lt;/complexType&gt;
       * </pre>
       *
       *
       */
      @XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "", propOrder =
      {
         "date",
         "workTime"
      }) public static class HolidayOrException
      {

         @XmlElement(name = "Date", required = true, type = String.class) @XmlJavaTypeAdapter(Adapter4.class) @XmlSchemaType(name = "dateTime") protected LocalDateTime date;
         @XmlElement(name = "WorkTime", required = true, nillable = true) protected List<WorkTimeType> workTime;

         /**
          * Gets the value of the date property.
          *
          * @return
          *     possible object is
          *     {@link String }
          *
          */
         public LocalDateTime getDate()
         {
            return date;
         }

         /**
          * Sets the value of the date property.
          *
          * @param value
          *     allowed object is
          *     {@link String }
          *
          */
         public void setDate(LocalDateTime value)
         {
            this.date = value;
         }

         /**
          * Gets the value of the workTime property.
          *
          * <p>
          * This accessor method returns a reference to the live list,
          * not a snapshot. Therefore any modification you make to the
          * returned list will be present inside the Jakarta XML Binding object.
          * This is why there is not a <CODE>set</CODE> method for the workTime property.
          *
          * <p>
          * For example, to add a new item, do as follows:
          * <pre>
          *    getWorkTime().add(newItem);
          * </pre>
          *
          *
          * <p>
          * Objects of the following type(s) are allowed in the list
          * {@link WorkTimeType }
          *
          *
          */
         public List<WorkTimeType> getWorkTime()
         {
            if (workTime == null)
            {
               workTime = new ArrayList<>();
            }
            return this.workTime;
         }

      }

   }

   /**
    * <p>Java class for anonymous complex type.
    *
    * <p>The following schema fragment specifies the expected content contained within this class.
    *
    * <pre>
    * &lt;complexType&gt;
    *   &lt;complexContent&gt;
    *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
    *       &lt;sequence&gt;
    *         &lt;element name="StandardWorkHours" maxOccurs="7" minOccurs="7"&gt;
    *           &lt;complexType&gt;
    *             &lt;complexContent&gt;
    *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
    *                 &lt;sequence&gt;
    *                   &lt;element name="DayOfWeek"&gt;
    *                     &lt;simpleType&gt;
    *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
    *                         &lt;enumeration value="Sunday"/&gt;
    *                         &lt;enumeration value="Monday"/&gt;
    *                         &lt;enumeration value="Tuesday"/&gt;
    *                         &lt;enumeration value="Wednesday"/&gt;
    *                         &lt;enumeration value="Thursday"/&gt;
    *                         &lt;enumeration value="Friday"/&gt;
    *                         &lt;enumeration value="Saturday"/&gt;
    *                       &lt;/restriction&gt;
    *                     &lt;/simpleType&gt;
    *                   &lt;/element&gt;
    *                   &lt;element name="WorkTime" type="{http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects}WorkTimeType" maxOccurs="24"/&gt;
    *                 &lt;/sequence&gt;
    *               &lt;/restriction&gt;
    *             &lt;/complexContent&gt;
    *           &lt;/complexType&gt;
    *         &lt;/element&gt;
    *       &lt;/sequence&gt;
    *     &lt;/restriction&gt;
    *   &lt;/complexContent&gt;
    * &lt;/complexType&gt;
    * </pre>
    *
    *
    */
   @XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "", propOrder =
   {
      "standardWorkHours"
   }) public static class StandardWorkWeek
   {

      @XmlElement(name = "StandardWorkHours", required = true) protected List<CalendarType.StandardWorkWeek.StandardWorkHours> standardWorkHours;

      /**
       * Gets the value of the standardWorkHours property.
       *
       * <p>
       * This accessor method returns a reference to the live list,
       * not a snapshot. Therefore any modification you make to the
       * returned list will be present inside the Jakarta XML Binding object.
       * This is why there is not a <CODE>set</CODE> method for the standardWorkHours property.
       *
       * <p>
       * For example, to add a new item, do as follows:
       * <pre>
       *    getStandardWorkHours().add(newItem);
       * </pre>
       *
       *
       * <p>
       * Objects of the following type(s) are allowed in the list
       * {@link CalendarType.StandardWorkWeek.StandardWorkHours }
       *
       *
       */
      public List<CalendarType.StandardWorkWeek.StandardWorkHours> getStandardWorkHours()
      {
         if (standardWorkHours == null)
         {
            standardWorkHours = new ArrayList<>();
         }
         return this.standardWorkHours;
      }

      /**
       * <p>Java class for anonymous complex type.
       *
       * <p>The following schema fragment specifies the expected content contained within this class.
       *
       * <pre>
       * &lt;complexType&gt;
       *   &lt;complexContent&gt;
       *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
       *       &lt;sequence&gt;
       *         &lt;element name="DayOfWeek"&gt;
       *           &lt;simpleType&gt;
       *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
       *               &lt;enumeration value="Sunday"/&gt;
       *               &lt;enumeration value="Monday"/&gt;
       *               &lt;enumeration value="Tuesday"/&gt;
       *               &lt;enumeration value="Wednesday"/&gt;
       *               &lt;enumeration value="Thursday"/&gt;
       *               &lt;enumeration value="Friday"/&gt;
       *               &lt;enumeration value="Saturday"/&gt;
       *             &lt;/restriction&gt;
       *           &lt;/simpleType&gt;
       *         &lt;/element&gt;
       *         &lt;element name="WorkTime" type="{http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects}WorkTimeType" maxOccurs="24"/&gt;
       *       &lt;/sequence&gt;
       *     &lt;/restriction&gt;
       *   &lt;/complexContent&gt;
       * &lt;/complexType&gt;
       * </pre>
       *
       *
       */
      @XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "", propOrder =
      {
         "dayOfWeek",
         "workTime"
      }) public static class StandardWorkHours
      {

         @XmlElement(name = "DayOfWeek", required = true) @XmlJavaTypeAdapter(Adapter1.class) protected String dayOfWeek;
         @XmlElement(name = "WorkTime", required = true, nillable = true) protected List<WorkTimeType> workTime;

         /**
          * Gets the value of the dayOfWeek property.
          *
          * @return
          *     possible object is
          *     {@link String }
          *
          */
         public String getDayOfWeek()
         {
            return dayOfWeek;
         }

         /**
          * Sets the value of the dayOfWeek property.
          *
          * @param value
          *     allowed object is
          *     {@link String }
          *
          */
         public void setDayOfWeek(String value)
         {
            this.dayOfWeek = value;
         }

         /**
          * Gets the value of the workTime property.
          *
          * <p>
          * This accessor method returns a reference to the live list,
          * not a snapshot. Therefore any modification you make to the
          * returned list will be present inside the Jakarta XML Binding object.
          * This is why there is not a <CODE>set</CODE> method for the workTime property.
          *
          * <p>
          * For example, to add a new item, do as follows:
          * <pre>
          *    getWorkTime().add(newItem);
          * </pre>
          *
          *
          * <p>
          * Objects of the following type(s) are allowed in the list
          * {@link WorkTimeType }
          *
          *
          */
         public List<WorkTimeType> getWorkTime()
         {
            if (workTime == null)
            {
               workTime = new ArrayList<>();
            }
            return this.workTime;
         }

      }

   }

}
