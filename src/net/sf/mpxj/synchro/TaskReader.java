
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.mpp.MPPUtility;

public class TaskReader extends TableReader
{
   public TaskReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      Map<String, Object> map = new HashMap<String, Object>();

      int taskRecordHeader = SynchroUtility.getInt(m_stream);
      if (taskRecordHeader != 0x04EC2576)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      //      byte[] block1 = new byte[33];
      //      m_stream.read(block1);
      //      System.out.println("BLOCK1");
      //      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      byte[] block1 = new byte[16];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      UUID uuid = SynchroUtility.getUUID(m_stream);
      System.out.println("UUID: " + uuid);

      byte[] blockx = new byte[1];
      m_stream.read(blockx);
      System.out.println("BLOCKX");
      System.out.println(MPPUtility.hexdump(blockx, true, 16, ""));

      UnknownTableReader unknownReader = new UnknownTableReader(m_stream);
      unknownReader.read();

      byte[] block1a = new byte[4];
      m_stream.read(block1a);
      System.out.println("BLOCK1A");
      System.out.println(MPPUtility.hexdump(block1a, true, 16, ""));

      RelationReader relationReader = new RelationReader(m_stream);
      relationReader.read();

      byte[] block1b = new byte[16];
      m_stream.read(block1b);
      System.out.println("BLOCK1B");
      System.out.println(MPPUtility.hexdump(block1b, true, 16, ""));

      String taskName = SynchroUtility.getString(m_stream);
      System.out.println("Task name: " + taskName);
      map.put("NAME", taskName);

      byte[] block2 = new byte[17];
      m_stream.read(block2);
      System.out.println("BLOCK2");
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));

      if (SynchroUtility.getBoolean(m_stream))
      {
         CostReader costReader = new CostReader(m_stream);
         costReader.read();
      }

      byte[] block2a = new byte[35];
      m_stream.read(block2a);
      System.out.println("BLOCK2A");
      System.out.println(MPPUtility.hexdump(block2a, true, 16, ""));

      if (SynchroUtility.getBoolean(m_stream))
      {
         CommentaryReader commentaryReader = new CommentaryReader(m_stream);
         commentaryReader.read();
      }

      //      byte[] block3 = new byte[140];
      //      m_stream.read(block3);
      //      System.out.println("BLOCK3");
      //      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      byte[] block3 = new byte[114];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      String url = SynchroUtility.getString(m_stream);
      System.out.println("URL: " + url);

      byte[] block3a = new byte[24];
      m_stream.read(block3a);
      System.out.println("BLOCK3A");
      System.out.println(MPPUtility.hexdump(block3a, true, 16, ""));

      String activityID = SynchroUtility.getString(m_stream);
      System.out.println("Activity ID: " + activityID);
      map.put("ID", activityID);

      byte[] block4 = new byte[121];
      m_stream.read(block4);
      System.out.println("BLOCK4");
      System.out.println(MPPUtility.hexdump(block4, true, 16, ""));

      m_rows.add(new MapRow(map));
   }
}
