
package org.mpxj.mpp;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import org.mpxj.EventManager;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Task;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.NumberHelper;

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
         file.addIgnoredError(ex);
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
            if (ByteArrayHelper.getShort(metaData, 0) != 0)
            {
               continue;
            }

            int index = consFixedData.getIndexFromOffset(ByteArrayHelper.getInt(metaData, 4));
            if (index == -1)
            {
               continue;
            }

            //
            // Do we have enough data?
            //
            byte[] data = consFixedData.getByteArrayValue(index);
            if (data.length < 14)
            {
               continue;
            }

            int constraintID = ByteArrayHelper.getInt(data, 0);
            if (constraintID <= lastConstraintID)
            {
               continue;
            }

            lastConstraintID = constraintID;
            int taskID1 = ByteArrayHelper.getInt(data, 4);
            int taskID2 = ByteArrayHelper.getInt(data, 8);

            if (taskID1 == taskID2)
            {
               continue;
            }

            // byte[] metaData2 = consFixed2Meta.getByteArrayValue(loop);
            // int index2 = consFixed2Data.getIndexFromOffset(MPPUtility.getInt(metaData2, 4));
            // byte[] data2 = consFixed2Data.getByteArrayValue(index2);

            Task task1 = file.getTaskByUniqueID(Integer.valueOf(taskID1));
            Task task2 = file.getTaskByUniqueID(Integer.valueOf(taskID2));
            if (task1 != null && task2 != null)
            {
               Relation relation = task2.addPredecessor(new Relation.Builder()
                  .predecessorTask(task1)
                  .type(RelationType.getInstance(ByteArrayHelper.getShort(data, 12)))
                  .lag(MPPUtility.getAdjustedDuration(properties, ByteArrayHelper.getInt(data, durationOffset), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, durationUnitsOffset))))
                  .uniqueID(Integer.valueOf(constraintID)));
               eventManager.fireRelationReadEvent(relation);
            }
         }
      }
   }
}
