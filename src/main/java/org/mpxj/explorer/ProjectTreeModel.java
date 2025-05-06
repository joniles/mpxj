
package org.mpxj.explorer;

import javax.swing.tree.DefaultTreeModel;

/**
 * Implements the model component of the ProjectTree MVC.
 */
public class ProjectTreeModel extends DefaultTreeModel
{
   /**
    * Constructor.
    *
    * @param writeOptions writer options
    */
   public ProjectTreeModel(WriteOptions writeOptions)
   {
      super(new MpxjTreeNode());
      m_writeOptions = writeOptions;
   }

   /**
    * Retrieve the options used if this project is written to a file.
    *
    * @return write options
    */
   public WriteOptions getWriteOptions()
   {
      return m_writeOptions;
   }

   private final WriteOptions m_writeOptions;
}
