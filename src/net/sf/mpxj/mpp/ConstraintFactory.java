
package net.sf.mpxj.mpp;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * Common implementation detail to extract task constraint data from
 * MPP9, MPP12, and MPP14 files.
 */
public class ConstraintFactory
{
   /**
    * Main entry point when called to process constraint data.
    *
    * @param projectDir project directory
    * @param file parent project file
    * @param inputStreamFactory factory to create input stream
    */
   public void process(DirectoryEntry projectDir, ProjectFile file, DocumentInputStreamFactory inputStreamFactory) throws IOException
   {
      DirectoryEntry consDir;
      try
      {
         consDir = (DirectoryEntry) projectDir.getEntry("TBkndCons");
      }

      catch (FileNotFoundException ex)
      {
         consDir = null;
      }

      if (consDir != null)
      {
         FixedMeta consFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) consDir.getEntry("FixedMeta"))), 10);
         FixedData consFixedData = new FixedData(consFixedMeta, 20, inputStreamFactory.getInstance(consDir, "FixedData"));
         //         FixedMeta consFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) consDir.getEntry("Fixed2Meta"))), 9);
         //         FixedData consFixed2Data = new FixedData(consFixed2Meta, 48, getEncryptableInputStream(consDir, "Fixed2Data"));

         int count = consFixedMeta.getAdjustedItemCount();
         int lastConstraintID = -1;

         ProjectProperties properties = file.getProjectProperties();
         EventManager eventManager = file.getEventManager();

         boolean project15 = NumberHelper.getInt(properties.getMppFileType()) == 14 && NumberHelper.getInt(properties.getApplicationVersion()) > ApplicationVersion.PROJECT_2010;
         int durationUnitsOffset = project15 ? 18 : 14;
         int durationOffset = project15 ? 14 : 16;

         for (int loop = 0; loop < count; loop++)
         {
            byte[] metaData = consFixedMeta.getByteArrayValue(loop);

            //
            // SourceForge bug 2209477: we were reading an int here, but
            // it looks like the deleted flag is just a short.
            //
            if (MPPUtility.getShort(metaData, 0) == 0)
            {
               int index = consFixedData.getIndexFromOffset(MPPUtility.getInt(metaData, 4));
               if (index != -1)
               {
                  //                  byte[] metaData2 = consFixed2Meta.getByteArrayValue(loop);
                  //                  int index2 = consFixed2Data.getIndexFromOffset(MPPUtility.getInt(metaData2, 4));
                  //                  byte[] data2 = consFixed2Data.getByteArrayValue(index2);

                  byte[] data = consFixedData.getByteArrayValue(index);
                  int constraintID = MPPUtility.getInt(data, 0);
                  if (constraintID > lastConstraintID)
                  {
                     lastConstraintID = constraintID;
                     int taskID1 = MPPUtility.getInt(data, 4);
                     int taskID2 = MPPUtility.getInt(data, 8);

                     if (taskID1 != taskID2)
                     {
                        Task task1 = file.getTaskByUniqueID(Integer.valueOf(taskID1));
                        Task task2 = file.getTaskByUniqueID(Integer.valueOf(taskID2));

                        if (task1 != null && task2 != null)
                        {
                           RelationType type = RelationType.getInstance(MPPUtility.getShort(data, 12));
                           TimeUnit durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, durationUnitsOffset));
                           Duration lag = MPPUtility.getAdjustedDuration(properties, MPPUtility.getInt(data, durationOffset), durationUnits);
                           Relation relation = task2.addPredecessor(task1, type, lag);
                           eventManager.fireRelationReadEvent(relation);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
