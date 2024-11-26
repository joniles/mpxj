
package net.sf.mpxj.explorer;

import javax.swing.tree.DefaultTreeModel;

/**
 * Implements the model component of the ProjectTree MVC.
 */
public class ProjectTreeModel extends DefaultTreeModel
{
   /**
    * Constructor.
    */
   public ProjectTreeModel(WriteOptions writeOptions)
   {
      super(new MpxjTreeNode());
      m_writeOptions = writeOptions;
   }

   public WriteOptions getWriteOptions()
   {
      return m_writeOptions;
   }

   private final WriteOptions m_writeOptions;
}
