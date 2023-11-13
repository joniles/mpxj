/*
 * file:       ExpenseItem.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       12/10/2020
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj;

/**
 * Expense item definition.
 */
public final class ExpenseItem
{
   /**
    * Constructor.
    *
    * @param task parent task
    * @deprecated use builder class
    */
   @Deprecated public ExpenseItem(Task task)
   {
      m_task = task;
   }

   /**
    * Constructor.
    *
    * @param builder Builder instance
    */
   private ExpenseItem(Builder builder)
   {
      m_task = builder.m_task;
      m_uniqueID = m_task.getParentFile().getUniqueIdObjectSequence(ExpenseItem.class).syncOrGetNext(builder.m_uniqueID);
      m_name = builder.m_name;
      m_account = builder.m_account;
      m_category = builder.m_category;
      m_description = builder.m_description;
      m_documentNumber = builder.m_documentNumber;
      m_vendor = builder.m_vendor;
      m_atCompletionCost = builder.m_atCompletionCost;
      m_atCompletionUnits = builder.m_atCompletionUnits;
      m_actualCost = builder.m_actualCost;
      m_actualUnits = builder.m_actualUnits;
      m_pricePerUnit = builder.m_pricePerUnit;
      m_remainingCost = builder.m_remainingCost;
      m_remainingUnits = builder.m_remainingUnits;
      m_plannedCost = builder.m_plannedCost;
      m_plannedUnits = builder.m_plannedUnits;
      m_accrueType = builder.m_accrueType;
      m_autoComputeActuals = builder.m_autoComputeActuals;
      m_unitOfMeasure = builder.m_unitOfMeasure;
   }

   /**
    * Retrieve the expense item's unique ID.
    *
    * @return expense item unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Set the expense item's unique ID.
    *
    * @param uniqueID expense item unique ID
    */
   @Deprecated public void setUniqueID(Integer uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   /**
    * Retrieve the expense item's name.
    *
    * @return  expense item name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Set the expense item's name.
    *
    * @param name expense item name
    * @deprecated use builder class
    */
   @Deprecated public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Retrieve the unique ID of the cost account associated with this expense item.
    *
    * @return cost account unique ID for this expense item
    */
   public Integer getAccountUniqueID()
   {
      return m_account == null ? null : m_account.getUniqueID();
   }

   /**
    * Retrieve the cost account associated with this expense item.
    *
    * @return cost account for this expense item
    */
   public CostAccount getAccount()
   {
      return m_account;
   }

   /**
    * Set the cost account associated with this expense item.
    *
    * @param account cost account for this expense item
    * @deprecated use builder class
    */
   @Deprecated public void setAccount(CostAccount account)
   {
      m_account = account;
   }

   /**
    * Retrieve the unique ID of the expense category associated with this expense item.
    *
    * @return expense category unique ID for this expense item
    */
   public Integer getCategoryUniqueID()
   {
      return m_category == null ? null : m_category.getUniqueID();
   }

   /**
    * Retrieve the expense category associated with this expense item.
    *
    * @return expense category for this expense item
    */
   public ExpenseCategory getCategory()
   {
      return m_category;
   }

   /**
    * Set the expense category associated with this expense item.
    *
    * @param category expense category for this expense item
    * @deprecated use builder class
    */
   @Deprecated public void setCategory(ExpenseCategory category)
   {
      m_category = category;
   }

   /**
    * Retrieve the parent task for this expense item.
    *
    * @return parent task
    */
   public Task getTask()
   {
      return m_task;
   }

   /**
    * Retrieve the description for this expense item.
    *
    * @return expense item description
    */
   public String getDescription()
   {
      return m_description;
   }

   /**
    * Set the description for this expense item.
    *
    * @param description expense item description
    * @deprecated use builder class
    */
   @Deprecated public void setDescription(String description)
   {
      m_description = description;
   }

   /**
    * Retrieve the document number for this expense item.
    *
    * @return document number
    */
   public String getDocumentNumber()
   {
      return m_documentNumber;
   }

   /**
    * Set the document number for this expense item.
    *
    * @param documentNumber document number
    * @deprecated use builder class
    */
   @Deprecated public void setDocumentNumber(String documentNumber)
   {
      m_documentNumber = documentNumber;
   }

   /**
    * Retrieve the vendor for this expense item.
    *
    * @return vendor
    */
   public String getVendor()
   {
      return m_vendor;
   }

   /**
    * Set the vendor for this expense item.
    *
    * @param vendor vendor
    * @deprecated use builder class
    */
   @Deprecated public void setVendor(String vendor)
   {
      m_vendor = vendor;
   }

   /**
    * Retrieve the at completion cost for this expense item.
    *
    * @return at completion cost
    */
   public Double getAtCompletionCost()
   {
      return m_atCompletionCost;
   }

   /**
    * Set the at completion cost for this expense item.
    *
    * @param atCompletionCost at completion cost
    * @deprecated use builder class
    */
   @Deprecated public void setAtCompletionCost(Double atCompletionCost)
   {
      m_atCompletionCost = atCompletionCost;
   }

   /**
    * Retrieve the at completion units for this expense item.
    *
    * @return at completion units
    */
   public Double getAtCompletionUnits()
   {
      return m_atCompletionUnits;
   }

   /**
    * Set the at completion units for this expense item.
    *
    * @param atCompletionUnits at completion units
    * @deprecated use builder class
    */
   @Deprecated public void setAtCompletionUnits(Double atCompletionUnits)
   {
      m_atCompletionUnits = atCompletionUnits;
   }

   /**
    * Retrieve the actual cost for this expense item.
    *
    * @return actual cost
    */
   public Double getActualCost()
   {
      return m_actualCost;
   }

   /**
    * Set the actual cost for this expense item.
    *
    * @param actualCost actual cost
    * @deprecated use builder class
    */
   @Deprecated public void setActualCost(Double actualCost)
   {
      m_actualCost = actualCost;
   }

   /**
    * Retrieve the actual units for this expense item.
    *
    * @return actual units
    */
   public Double getActualUnits()
   {
      return m_actualUnits;
   }

   /**
    * Set the actual units for this expense item.
    *
    * @param actualUnits actual units
    * @deprecated use builder class
    */
   @Deprecated public void setActualUnits(Double actualUnits)
   {
      m_actualUnits = actualUnits;
   }

   /**
    * Retrieve the price per unit for this expense item.
    *
    * @return price per unit
    */
   public Double getPricePerUnit()
   {
      return m_pricePerUnit;
   }

   /**
    * Set the price per unit for this expense item.
    *
    * @param pricePerUnit price per unit
    * @deprecated use builder class
    */
   @Deprecated public void setPricePerUnit(Double pricePerUnit)
   {
      m_pricePerUnit = pricePerUnit;
   }

   /**
    * Retrieve the remaining cost for this expense item.
    *
    * @return remaining cost
    */
   public Double getRemainingCost()
   {
      return m_remainingCost;
   }

   /**
    * Set the remaining cost for this expense item.
    *
    * @param remainingCost remaining cost
    * @deprecated use builder class
    */
   @Deprecated public void setRemainingCost(Double remainingCost)
   {
      m_remainingCost = remainingCost;
   }

   /**
    * Retrieve the remaining units for this expense item.
    *
    * @return remaining units
    */
   public Double getRemainingUnits()
   {
      return m_remainingUnits;
   }

   /**
    * Set the remaining units for this expense item.
    *
    * @param remainingUnits remaining units
    * @deprecated use builder class
    */
   @Deprecated public void setRemainingUnits(Double remainingUnits)
   {
      m_remainingUnits = remainingUnits;
   }

   /**
    * Retrieve the planned cost for this expense item.
    *
    * @return planned cost
    */
   public Double getPlannedCost()
   {
      return m_plannedCost;
   }

   /**
    * Set the planned cost for this expense item.
    *
    * @param plannedCost planned cost
    * @deprecated use builder class
    */
   @Deprecated public void setPlannedCost(Double plannedCost)
   {
      m_plannedCost = plannedCost;
   }

   /**
    * Retrieve the planned units for this expense item.
    *
    * @return planned units
    */
   public Double getPlannedUnits()
   {
      return m_plannedUnits;
   }

   /**
    * Set the planned units for this expense item.
    *
    * @param plannedUnits planned units
    * @deprecated use builder class
    */
   @Deprecated public void setPlannedUnits(Double plannedUnits)
   {
      m_plannedUnits = plannedUnits;
   }

   /**
    * Retrieve the accrue type for this expense item.
    *
    * @return accrue type
    */
   public AccrueType getAccrueType()
   {
      return m_accrueType;
   }

   /**
    * Set the accrue type for this expense item.
    *
    * @param accrueType accrue type
    * @deprecated use builder class
    */
   @Deprecated public void setAccrueType(AccrueType accrueType)
   {
      m_accrueType = accrueType;
   }

   /**
    * Retrieve the auto complete actuals flag for this expense item.
    *
    * @return auto complete actuals flag
    */
   public boolean getAutoComputeActuals()
   {
      return m_autoComputeActuals;
   }

   /**
    * Set the auto complete actuals flag for this expense item.
    *
    * @param autoComputeActuals auto complete actuals flag
    * @deprecated use builder class
    */
   @Deprecated public void setAutoComputeActuals(boolean autoComputeActuals)
   {
      m_autoComputeActuals = autoComputeActuals;
   }

   /**
    * Retrieve the unit of measure for this expense item.
    *
    * @return unit of measure
    */
   public String getUnitOfMeasure()
   {
      return m_unitOfMeasure;
   }

   /**
    * Set the unit of measure for this expense item.
    *
    * @param unitOfMeasure unit of measure
    * @deprecated use builder class
    */
   @Deprecated public void setUnitOfMeasure(String unitOfMeasure)
   {
      m_unitOfMeasure = unitOfMeasure;
   }

   @Override public String toString()
   {
      return "[ExpenseItem uniqueID=" + m_uniqueID + " name=" + m_name + "]";
   }

   private final Task m_task;
   private Integer m_uniqueID;
   private String m_name;
   private CostAccount m_account;
   private ExpenseCategory m_category;
   private String m_description;
   private String m_documentNumber;
   private String m_vendor;
   private Double m_atCompletionCost;
   private Double m_atCompletionUnits;
   private Double m_actualCost;
   private Double m_actualUnits;
   private Double m_pricePerUnit;
   private Double m_remainingCost;
   private Double m_remainingUnits;
   private Double m_plannedCost;
   private Double m_plannedUnits;
   private AccrueType m_accrueType;
   private boolean m_autoComputeActuals;
   private String m_unitOfMeasure;

   /**
    * Expense item builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param task parent task
       */
      public Builder(Task task)
      {
         m_task = task;
      }

      /**
       * Add the unique ID.
       *
       * @param value unique ID
       * @return builder
       */
      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      /**
       * Add the name.
       *
       * @param value name
       * @return builder
       */
      public Builder name(String value)
      {
         m_name = value;
         return this;
      }

      /**
       * Add the cost account.
       *
       * @param value cost account
       * @return builder
       */
      public Builder account(CostAccount value)
      {
         m_account = value;
         return this;
      }

      /**
       * Add the expense category.
       *
       * @param value expense category
       * @return builder
       */
      public Builder category(ExpenseCategory value)
      {
         m_category = value;
         return this;
      }

      /**
       * Add the description.
       *
       * @param value description
       * @return builder
       */
      public Builder description(String value)
      {
         m_description = value;
         return this;
      }

      /**
       * Add the document number.
       *
       * @param value document number
       * @return builder
       */
      public Builder documentNumber(String value)
      {
         m_documentNumber = value;
         return this;
      }

      /**
       * Add the vendor.
       *
       * @param value vendor
       * @return builder
       */
      public Builder vendor(String value)
      {
         m_vendor = value;
         return this;
      }

      /**
       * Add the at completion cost.
       *
       * @param value at completion cost
       * @return builder
       */
      public Builder atCompletionCost(Double value)
      {
         m_atCompletionCost = value;
         return this;
      }

      /**
       * Add the at completion units.
       *
       * @param value at completion units
       * @return builder
       */
      public Builder atCompletionUnits(Double value)
      {
         m_atCompletionUnits = value;
         return this;
      }

      /**
       * Add the actual cost.
       *
       * @param value actual cost
       * @return builder
       */
      public Builder actualCost(Double value)
      {
         m_actualCost = value;
         return this;
      }

      /**
       * Add the actual units.
       *
       * @param value actual units
       * @return builder
       */
      public Builder actualUnits(Double value)
      {
         m_actualUnits = value;
         return this;
      }

      /**
       * Add the price per unit.
       *
       * @param value price per unit
       * @return builder
       */
      public Builder pricePerUnit(Double value)
      {
         m_pricePerUnit = value;
         return this;
      }

      /**
       * Add the remaining cost.
       *
       * @param value remaining cost
       * @return builder
       */
      public Builder remainingCost(Double value)
      {
         m_remainingCost = value;
         return this;
      }

      /**
       * Add the remaining units.
       *
       * @param value remaining units
       * @return builder
       */
      public Builder remainingUnits(Double value)
      {
         m_remainingUnits = value;
         return this;
      }

      /**
       * Add the planned cost.
       *
       * @param value planned cost
       * @return builder
       */
      public Builder plannedCost(Double value)
      {
         m_plannedCost = value;
         return this;
      }

      /**
       * Add the planned units.
       *
       * @param value planned units
       * @return builder
       */
      public Builder plannedUnits(Double value)
      {
         m_plannedUnits = value;
         return this;
      }

      /**
       * Add the accrue type.
       *
       * @param value accrue type
       * @return builder
       */
      public Builder accrueType(AccrueType value)
      {
         m_accrueType = value;
         return this;
      }

      /**
       * Add the auto compute actuals flag.
       *
       * @param value auto compute actuals flag
       * @return builder
       */
      public Builder autoComputeActuals(boolean value)
      {
         m_autoComputeActuals = value;
         return this;
      }

      /**
       * Add the unit of measure.
       *
       * @param value unit of measure
       * @return builder
       */
      public Builder unitOfMeasure(String value)
      {
         m_unitOfMeasure = value;
         return this;
      }

      /**
       * Build an ExpenseItem instance.
       *
       * @return ExpenseItem instance.
       */
      public ExpenseItem build()
      {
         return new ExpenseItem(this);
      }

      private final Task m_task;
      private Integer m_uniqueID;
      private String m_name;
      private CostAccount m_account;
      private ExpenseCategory m_category;
      private String m_description;
      private String m_documentNumber;
      private String m_vendor;
      private Double m_atCompletionCost;
      private Double m_atCompletionUnits;
      private Double m_actualCost;
      private Double m_actualUnits;
      private Double m_pricePerUnit;
      private Double m_remainingCost;
      private Double m_remainingUnits;
      private Double m_plannedCost;
      private Double m_plannedUnits;
      private AccrueType m_accrueType;
      private boolean m_autoComputeActuals;
      private String m_unitOfMeasure;
   }
}
