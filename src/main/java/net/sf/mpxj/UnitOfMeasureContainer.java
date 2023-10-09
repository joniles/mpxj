package net.sf.mpxj;
public class UnitOfMeasureContainer extends ProjectEntityContainer<UnitOfMeasure>
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public UnitOfMeasureContainer(ProjectFile projectFile)
   {
      super(projectFile);
   }

   public UnitOfMeasure getOrCreateByAbbreviation(String abbreviation)
   {
      if (abbreviation == null || abbreviation.isEmpty())
      {
         return null;
      }
      return stream().filter(u -> abbreviation.equals(u.getAbbreviation())).findFirst().orElseGet(() -> buildUnitOfMeasure(abbreviation));
   }

   private UnitOfMeasure buildUnitOfMeasure(String name)
   {
      UnitOfMeasure uom = new UnitOfMeasure.Builder()
         .name(name)
         .abbreviation(name)
         .uniqueID(stream().mapToInt(u -> u.getUniqueID()).max().orElse(0) + 1)
         .sequenceNumber(stream().mapToInt(u -> u.getSequenceNumber()).max().orElse(0) + 1)
         .build();
      add(uom);
      return uom;
   }
}