
package net.sf.mpxj.svg;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.writer.AbstractProjectWriter;

public final class SvgWriter extends AbstractProjectWriter
{
   /**
    * {@inheritDoc}
    * @throws
    */
   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         m_projectFile = projectFile;
         setup();
         XMLOutputFactory output = XMLOutputFactory.newInstance();
         m_writer = output.createXMLStreamWriter(stream);
         m_writer.writeStartDocument();
         m_writer.writeStartElement("svg");
         m_writer.writeAttribute("xmlns:svg", "http://www.w3.org/2000/svg");
         m_writer.writeAttribute("xmlns", "http://www.w3.org/2000/svg");
         m_writer.writeAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
         m_writer.writeAttribute("id", "svg8");
         m_writer.writeAttribute("version", "1.1");

         writeDefs();
         writeTasksLayer();

         m_writer.writeEndElement();
         m_writer.flush();
         m_writer = null;
         m_projectFile = null;
      }

      catch (XMLStreamException ex)
      {
         throw new IOException(ex);
      }
   }

   private void setup()
   {
      System.out.println("Project start date: " + m_projectFile.getStartDate());
      System.out.println("Project finish date: " + m_projectFile.getFinishDate());

      long finishTime = m_projectFile.getFinishDate().getTime();
      long startTime = m_projectFile.getStartDate().getTime();

      long maxWidth = 512;
      m_xOffset = startTime;
      m_xScale = (finishTime - startTime) / maxWidth;
   }

   private void writeDefs() throws XMLStreamException
   {
      m_writer.writeStartElement("defs");
      m_writer.writeAttribute("id", "defs1");
      writeBarPattern();
      m_writer.writeEndElement();
   }

   private void writeBarPattern() throws XMLStreamException
   {
      m_writer.writeStartElement("pattern");
      m_writer.writeAttribute("patternUnits", "userSpaceOnUse");
      m_writer.writeAttribute("width", "2");
      m_writer.writeAttribute("height", "2");
      m_writer.writeAttribute("patternTransform", "translate(0,0) scale(1,1)");
      m_writer.writeAttribute("id", "BarPattern");

      m_writer.writeEmptyElement("rect");
      m_writer.writeAttribute("style", "fill:#0000ff;stroke:none");
      m_writer.writeAttribute("x", "0");
      m_writer.writeAttribute("y", "0");
      m_writer.writeAttribute("width", "1");
      m_writer.writeAttribute("height", "1");
      m_writer.writeAttribute("id", "rect1");

      m_writer.writeEmptyElement("rect");
      m_writer.writeAttribute("style", "fill:#0000ff;stroke:none");
      m_writer.writeAttribute("x", "1");
      m_writer.writeAttribute("y", "1");
      m_writer.writeAttribute("width", "1");
      m_writer.writeAttribute("height", "1");
      m_writer.writeAttribute("id", "rect2");

      m_writer.writeEndElement();
   }

   private void writeTasksLayer() throws XMLStreamException
   {
      m_writer.writeStartElement("g");
      for (Task task : m_projectFile.getTasks())
      {
         writeTask(task);
      }
      m_writer.writeEndElement();
   }

   private void writeTask(Task task) throws XMLStreamException
   {
      System.out.println("Task start date: " + task.getStart());
      System.out.println("Task finish date: " + task.getFinish());

      long start = task.getStart().getTime();
      long finish = task.getFinish().getTime();
      long x = (start - m_xOffset) / m_xScale;
      long y = task.getID().intValue() * 12;
      long width = (finish - start) / m_xScale;
      long height = 8;

      m_writer.writeEmptyElement("rect");
      m_writer.writeAttribute("x", Long.toString(x));
      m_writer.writeAttribute("y", Long.toString(y));
      m_writer.writeAttribute("width", Long.toString(width));
      m_writer.writeAttribute("height", Long.toString(height));
      m_writer.writeAttribute("style", "fill:url(#BarPattern);stroke:#0000ff;stroke-width:0.283;stroke-miterlimit:4;stroke-dasharray:none;fill-opacity:1.0");
   }

   private XMLStreamWriter m_writer;
   private ProjectFile m_projectFile;
   private long m_xOffset;
   private long m_xScale;
}
