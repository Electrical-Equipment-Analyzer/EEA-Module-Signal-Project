/*
 * Copyright (C) 2015 D10307009
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package tw.edu.sju.ee.eea.module.temp;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ListProperty;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.PropertyEditorRegistration;
import tw.edu.sju.ee.eea.jni.modinst.NIModinstUtils;

/**
 *
 * @author D10307009
 */
public class ListPropertyEditor<E> extends PropertyEditorSupport
        implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    private E[] list;

    public ListPropertyEditor(E[] list) {
        this.list = list;
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
        super.addPropertyChangeListener(listener); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void attachEnv(PropertyEnv propertyEnv) {
        propertyEnv.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new Inplace<E>(list);
        }
        ed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e);
                System.out.println("fifififififififififi");
                ed.setValue(0);
//                firePropertyChange();
            }
        });
        return ed;
    }

    private static class Inplace<E> implements InplaceEditor {

        private PropertyEditor editor = null;
        private PropertyModel model = null;
        private JComboBox comboBox = new JComboBox();

        public Inplace(E[] list) {
            for (E item : list) {
                comboBox.addItem(item.toString());
            }
        }

        public JComponent getComponent() {
            return this.comboBox;
        }

        public Object getValue() {
            System.out.println("get");
            return this.comboBox.getSelectedIndex();
        }

        public void setValue(Object object) {
            System.out.println("set");
            System.out.println(object);
//            this.comboBox.setSelectedIndex((Integer) object);
//            ListPropertyEditor.this.firePropertyChange();
        }

        public void reset() {
            System.out.println("reset");
            System.out.println(editor.getValue());
            this.comboBox.setSelectedIndex(0);
//            this.comboBox.setSelectedIndex((Integer) editor.getValue());
        }

        @Override
        public void connect(PropertyEditor pe, PropertyEnv pe1) {
            System.out.println("connect");
            System.out.println(pe);
            System.out.println(pe1);
            editor = pe;
            reset();
        }

        @Override
        public void clear() {
            System.out.println("clear");
            this.editor = null;
            this.model = null;
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void addActionListener(ActionListener al) {
            comboBox.addActionListener(al);
        }

        @Override
        public void removeActionListener(ActionListener al) {
            comboBox.remove(comboBox);
        }

        @Override
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        @Override
        public PropertyModel getPropertyModel() {
            return model;
        }

        @Override
        public void setPropertyModel(PropertyModel pm) {
            this.model = pm;
        }

        @Override
        public boolean isKnownComponent(Component cmpnt) {
            return cmpnt == comboBox || comboBox.isAncestorOf(cmpnt);
        }
    }
}
