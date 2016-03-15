/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.cn.vearn.checktreetable;

import entity.FieldEntity;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 *
 * @author vearn
 */
public class FiledTreeTableModel extends DefaultTreeTableModel {

    private String[] _names = {" Key ", "Value", "Data Type"," Field name "};
    private Class[] _types = {Object.class,Object.class, Object.class, Object.class};


    public FiledTreeTableModel(TreeTableNode node) {
        super(node);
    }

    /**
     * 列的类型
     */
    @Override
    public Class getColumnClass(int col) {
        return _types[col];
    }

    /**
     * 列的数量
     */
    @Override
    public int getColumnCount() {
        return _names.length;
    }

    /**
     * 表头显示的内容
     */
    @Override
    public String getColumnName(int column) {
        return _names[column];
    }

    /**
     * 返回在单元格中显示的Object
     */
    @Override
    public Object getValueAt(Object node, int column) {
        Object value = null;
        if (node instanceof DefaultMutableTreeTableNode) {
            DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;
            Object o = mutableNode.getUserObject();
            if (o != null && o instanceof FieldEntity) {
                FieldEntity bean = (FieldEntity) o;
                switch (column) {
                    case 0:
                        value = bean.getKey();
                        break;
                    case 1:
                        value = bean.getValue();
                        break;
                    case 2:
                        value = bean.getRealType();
                        break;
                    case 3:
                        value = bean.getFieldName();
                        break;
                }
            }
        }
        return value;
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
        super.setValueAt(value, node, column);

        if (node instanceof DefaultMutableTreeTableNode) {
            DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;
            Object o = mutableNode.getUserObject();
            if (o != null && o instanceof FieldEntity) {
                FieldEntity bean = (FieldEntity) o;
                switch (column) {
                    case 2:
                        bean.checkAndSetType(value.toString());
                        break;
                    case 3:
                        bean.setFieldName(value.toString());
                        break;
                }
            }
        }
    }


    @Override
    public boolean isCellEditable(Object node, int column) {

        if(column == 2){

            return  true;
        }
        if(column == 3){

            return  true;
        }
        return false;
    }
}
