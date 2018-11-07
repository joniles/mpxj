
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
   public ProjectTreeModel()
   {
      super(new MpxjTreeNode());
   }
}
