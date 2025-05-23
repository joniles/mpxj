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

/**
 * <p>Java class for DisplayCurrencyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DisplayCurrencyType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Currency" type="{http://xmlns.oracle.com/Primavera/P6/V24.12/API/BusinessObjects}CurrencyType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "DisplayCurrencyType", propOrder =
{
   "currency"
}) public class DisplayCurrencyType
{

   @XmlElement(name = "Currency", required = true) protected CurrencyType currency;

   /**
    * Gets the value of the currency property.
    *
    * @return
    *     possible object is
    *     {@link CurrencyType }
    *
    */
   public CurrencyType getCurrency()
   {
      return currency;
   }

   /**
    * Sets the value of the currency property.
    *
    * @param value
    *     allowed object is
    *     {@link CurrencyType }
    *
    */
   public void setCurrency(CurrencyType value)
   {
      this.currency = value;
   }

}
