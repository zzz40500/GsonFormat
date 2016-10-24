/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.swingx.ux;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;

/**
 *
 * @author Santhosh Kumr T - santhosh@in.fiorano.com
 */
public class CheckTreeTableManager extends MouseAdapter implements TreeSelectionListener {

    private CheckTreeSelectionModel selectionModel;
    private JXTreeTable treetable;
    private JTree tree;
    int hotspot = new JCheckBox().getPreferredSize().width;

    public CheckTreeTableManager(JXTreeTable treeTable) {
        this.treetable = treeTable;
        this.tree = (JTree) treeTable.getCellRenderer(0, 0);
        selectionModel = new CheckTreeSelectionModel(tree.getModel());
        tree.setCellRenderer(new DefaultTreeRenderer(new CheckTreeCellProvider(selectionModel)));
        treeTable.addMouseListener(this);

        selectionModel.addTreeSelectionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        TreePath path = tree.getPathForLocation(me.getX(), me.getY());
        if (path == null) {
            return;
        }
        if (me.getX() > tree.getPathBounds(path).x + hotspot) {
            return;
        }

        boolean selected = selectionModel.isPathSelected(path, true);
        selectionModel.removeTreeSelectionListener(this);

        try {
            if (selected) {
                selectionModel.removeSelectionPath(path);
            } else {
                selectionModel.addSelectionPath(path);
            }
        } finally {
            selectionModel.addTreeSelectionListener(this);
            treetable.repaint();
        }
    }

    public CheckTreeSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public void valueChanged(TreeSelectionEvent e) {
    }
}