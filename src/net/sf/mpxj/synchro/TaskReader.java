
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class TaskReader extends TableReader
{
   public TaskReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      byte[] blockx = new byte[1];
      m_stream.read(blockx);
      System.out.println("BLOCKX");
      System.out.println(MPPUtility.hexdump(blockx, true, 16, ""));

      ResourceAssignmentReader resourceAssignmentReader = new ResourceAssignmentReader(m_stream);
      resourceAssignmentReader.read();
      map.put("RESOURCE_ASSIGNMENTS", resourceAssignmentReader.getRows());

      byte[] block1a = new byte[4];
      m_stream.read(block1a);
      System.out.println("BLOCK1A");
      System.out.println(MPPUtility.hexdump(block1a, true, 16, ""));

      RelationReader relationReader = new RelationReader(m_stream);
      relationReader.read();
      map.put("RELATIONS", relationReader.getRows());

      //      byte[] block1b = new byte[16];
      //      m_stream.read(block1b);
      //      System.out.println("BLOCK1B");
      //      System.out.println(MPPUtility.hexdump(block1b, true, 16, ""));

      map.put("CALENDAR_UUID", SynchroUtility.getUUID(m_stream));
      System.out.println("Calendar UUID: " + map.get("CALENDAR_UUID"));

      map.put("NAME", SynchroUtility.getString(m_stream));
      System.out.println("Task name: " + map.get("NAME"));

      map.put("START", SynchroUtility.getDate(m_stream));
      System.out.println("Task start: " + map.get("START"));

      byte[] block2 = new byte[4];
      m_stream.read(block2);
      System.out.println("BLOCK2");
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));

      map.put("DURATION", SynchroUtility.getDuration(m_stream));
      System.out.println("Task duration: " + map.get("DURATION"));

      byte[] block21 = new byte[4];
      m_stream.read(block21);
      System.out.println("BLOCK2.1");
      System.out.println(MPPUtility.hexdump(block21, true, 16, ""));

      if (SynchroUtility.getBoolean(m_stream))
      {
         TaskReader taskReader = new TaskReader(m_stream);
         taskReader.read();
         map.put("TASKS", taskReader.getRows());
      }

      if (SynchroUtility.getBoolean(m_stream))
      {
         CostReader costReader = new CostReader(m_stream);
         costReader.read();
         map.put("COSTS", costReader.getRows());
      }

      //      byte[] block2a = new byte[35];
      //      m_stream.read(block2a);
      //      System.out.println("BLOCK2A");
      //      System.out.println(MPPUtility.hexdump(block2a, true, 16, ""));

      readDatePair(map, "UNKNOWN_DATE1");
      readDatePair(map, "UNKNOWN_DATE2");
      readDatePair(map, "UNKNOWN_DATE3");
      readDatePair(map, "UNKNOWN_DATE4");

      byte[] block2a5 = new byte[3];
      m_stream.read(block2a5);
      System.out.println(MPPUtility.hexdump(block2a5, true, 16, ""));

      if (SynchroUtility.getBoolean(m_stream))
      {
         CommentaryReader commentaryReader = new CommentaryReader(m_stream);
         commentaryReader.read();
         map.put("COMMENTARY", commentaryReader.getRows());
      }

      //      byte[] block3 = new byte[140];
      //      m_stream.read(block3);
      //      System.out.println("BLOCK3");
      //      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      byte[] block2b = new byte[4];
      m_stream.read(block2b);
      System.out.println("BLOCK2B");
      System.out.println(MPPUtility.hexdump(block2b, true, 16, ""));

      int fileCount = SynchroUtility.getInt(m_stream);
      System.out.println("File count: " + fileCount);

      if (fileCount != 0)
      {
         // 20 byte block per file?
         byte[] fileData = new byte[20];
         for (int index = 0; index < fileCount; index++)
         {
            m_stream.read(fileData);
            System.out.println("FILE DATA");
            System.out.println(MPPUtility.hexdump(fileData, true, 16, ""));
         }
      }

      //      byte[] block3 = new byte[106];
      //      m_stream.read(block3);
      //      System.out.println("BLOCK3");
      //      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      byte[] block31 = new byte[8];
      m_stream.read(block31);
      System.out.println("BLOCK31");
      System.out.println(MPPUtility.hexdump(block31, true, 16, ""));

      map.put("CONSTRAINT_TYPE", SynchroUtility.getInteger(m_stream));
      System.out.println("Constraint Type: " + map.get("CONSTRAINT_TYPE"));

      readDatePair(map, "CONSTRAINT_EARLY_DATE");
      readDatePair(map, "CONSTRAINT_LATE_DATE");

      byte[] block32 = new byte[78];
      m_stream.read(block32);
      System.out.println("BLOCK32");
      System.out.println(MPPUtility.hexdump(block32, true, 16, ""));

      map.put("URL", SynchroUtility.getString(m_stream));
      System.out.println("URL: " + map.get("URL"));

      // was 24
      byte[] block3a = new byte[4];
      m_stream.read(block3a);
      System.out.println("BLOCK3A");
      System.out.println(MPPUtility.hexdump(block3a, true, 16, ""));

      // Not sure if  this is 2 bytes or 1
      String progressType;
      switch (block3a[0])
      {
         case 1:
         {
            progressType = "Automatic";
            break;
         }

         case 2:
         {
            progressType = "Manual";
            break;
         }

         case 3:
         {
            progressType = "Duration";
            break;
         }

         case 4:
         {
            progressType = "Physical";
            break;
         }

         case 5:
         {
            progressType = "Unit";
            break;
         }

         default:
         {
            progressType = null;
            break;
         }
      }

      map.put("PROGRESS_TYPE", progressType);
      System.out.println("Progress Type: " + map.get("PROGRESS_TYPE"));

      map.put("PERCENT_COMPLETE", SynchroUtility.getDouble(m_stream));
      System.out.println("Task Percent Complete: " + map.get("PERCENT_COMPLETE"));

      byte[] block3b = new byte[12];
      m_stream.read(block3b);
      System.out.println("BLOCK3B");
      System.out.println(MPPUtility.hexdump(block3b, true, 16, ""));

      map.put("ID", SynchroUtility.getString(m_stream));
      System.out.println("Activity ID: " + map.get("ID"));

      if (SynchroUtility.getBoolean(m_stream))
      {
         UserFieldReader reader = new UserFieldReader(m_stream);
         reader.read();
         map.put("USER_FIELDS", reader.getRows());
      }

      //      map.put("REMAINING_DURATION", SynchroUtility.getDuration(m_stream));
      //      System.out.println("Task Remaining Duration: " + map.get("REMAINING_DURATION"));

      //      byte[] block4 = new byte[120];
      //      m_stream.read(block4);
      //      System.out.println("BLOCK4");
      //      System.out.println(MPPUtility.hexdump(block4, true, 16, ""));

      byte[] block41 = new byte[28];
      m_stream.read(block41);
      System.out.println("BLOCK41");
      System.out.println(MPPUtility.hexdump(block41, true, 16, ""));

      readDatePair(map, "UNKNOWN_DATE7");
      readDatePair(map, "UNKNOWN_DATE8");
      readDatePair(map, "UNKNOWN_DATE9");
      readDatePair(map, "UNKNOWN_DATE10");

      byte[] block42 = new byte[4];
      m_stream.read(block42);
      System.out.println("BLOCK42");
      System.out.println(MPPUtility.hexdump(block42, true, 16, ""));

      map.put("PHYSICAL_QUANTITY", SynchroUtility.getDouble(m_stream));
      System.out.println("Task Physical Quantity: " + map.get("PHYSICAL_QUANTITY"));

      byte[] block43 = new byte[8];
      m_stream.read(block43);
      System.out.println("BLOCK43");
      System.out.println(MPPUtility.hexdump(block43, true, 16, ""));

      map.put("PHYSICAL_QUANTITY_UNIT", SynchroUtility.getInteger(m_stream));
      System.out.println("Task Physical Quantity Unit: " + map.get("PHYSICAL_QUANTITY_UNIT"));

      byte[] block44 = new byte[8];
      m_stream.read(block44);
      System.out.println("BLOCK44");
      System.out.println(MPPUtility.hexdump(block44, true, 16, ""));

      readDatePair(map, "EXPECTED_FINISH");

      new UnknownTableReader(m_stream).read();

      byte[] block45 = new byte[8];
      m_stream.read(block45);
      System.out.println("BLOCK45");
      System.out.println(MPPUtility.hexdump(block45, true, 16, ""));
   }

   private void readDatePair(Map<String, Object> map, String name) throws IOException
   {
      map.put(name, SynchroUtility.getDate(m_stream));
      System.out.println(name + ": " + map.get(name));
      byte[] extra = new byte[4];
      m_stream.read(extra);
      System.out.println(name + " extra: " + MPPUtility.hexdump(extra, true, 16, ""));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04EC2576;
   }
}
