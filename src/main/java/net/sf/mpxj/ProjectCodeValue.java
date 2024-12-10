package net.sf.mpxj;

public class ProjectCodeValue extends AbstractCodeValue<ProjectCodeValue.Builder, ProjectCodeValue, ProjectCode>
{
   private ProjectCodeValue(Builder builder)
   {
      super(builder);
   }

   public static class Builder extends AbstractCodeValue.Builder<ProjectCodeValue.Builder, ProjectCodeValue, ProjectCode>
   {
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         super(sequenceProvider);
      }

      public ProjectCodeValue build()
      {
         return new ProjectCodeValue(this);
      }

      @Override protected Builder self()
      {
         return this;
      }
   }
}
