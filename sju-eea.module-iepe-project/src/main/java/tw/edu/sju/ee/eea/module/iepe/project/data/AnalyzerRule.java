/*
 * Copyright (C) 2014 Leo
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
package tw.edu.sju.ee.eea.module.iepe.project.data;

import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import org.dom4j.Element;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import tw.edu.sju.ee.eea.module.iepe.project.IepeProject;

/**
 *
 * @author Leo
 */
public class AnalyzerRule {

    private IepeProject project;
    private Element element;

    public AnalyzerRule(IepeProject project, Element element) {
        this.project = project;
        this.element = element;
    }

    public void initValue() {
        element.addElement("name").setText("new rule");
        element.addElement("channel").setText("-1");
        element.addElement("minimum").setText("-1");
        element.addElement("maximum").setText("-1");
        element.addElement("magnitude").setText("0");
        project.save();
    }

    private void setName(String name) {
        element.element("name").setText(name);
        project.save();
    }

    private void setChannel(int channel) {
        element.element("channel").setText(String.valueOf(channel));
        project.save();
    }

    private void setMaximum(int maximum) {
        element.element("maximum").setText(String.valueOf(maximum));
        project.save();
    }

    private void setMinimum(int minimum) {
        element.element("minimum").setText(String.valueOf(minimum));
        project.save();
    }

    private void setMagnitude(double magnitude) {
        element.element("magnitude").setText(String.valueOf(magnitude));
        project.save();
    }

    public String getName() {
        return element.elementText("name");
    }

    public Integer getChannel() {
        String text = element.elementText("channel");
        return text == null ? null : Integer.parseInt(text);
    }

    public Integer getMaximum() {
        String text = element.elementText("maximum");
        return text == null ? null : Integer.parseInt(text);
    }

    public Integer getMinimum() {
        String text = element.elementText("minimum");
        return text == null ? null : Integer.parseInt(text);
    }

    public Double getMagnitude() {
        String text = element.elementText("magnitude");
        return text == null ? null : Double.parseDouble(text);
    }

    @Override
    public String toString() {
        return "AnalyzerRule{" + "name=" + getName() + ", channel=" + getChannel() + ", minimum=" + getMinimum() + ", maximum=" + getMaximum() + ", magnitude=" + getMagnitude() + '}';
    }

    @NbBundle.Messages({
        "LBL_AR_title=Properties",
        "LBL_AR_name=Name",
        "DCT_AR_name=Rule Name",
        "LBL_AR_channel=Channel",
        "DCT_AR_channel=Input Channel",
        "LBL_AR_maximum=Maximum",
        "DCT_AR_maximum=Frequency Maximum",
        "LBL_AR_minimum=Minimul",
        "DCT_AR_minimum=Frequency Minimul",
        "LBL_AR_magnitude=Magnitude",
        "DCT_AR_magnitude=Magnitude"
    })
    public Node createNodeDelegate() {
        return new AbstractNode(Children.LEAF) {

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return AnalyzerRule.this.getName();
            }

            @Override
            protected Sheet createSheet() {
                Sheet.Set set = new Sheet.Set();
                set.setName(Bundle.LBL_AR_title());
                set.put(new PropertySupport.ReadWrite<String>(
                        "AR_name",
                        String.class,
                        Bundle.LBL_AR_name(),
                        Bundle.DCT_AR_name()) {
                            @Override
                            public String getValue() throws IllegalAccessException, InvocationTargetException {
                                return AnalyzerRule.this.getName();
                            }

                            @Override
                            public void setValue(String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                AnalyzerRule.this.setName(name);
                            }
                        });
                set.put(new PropertySupport.ReadWrite<Integer>(
                        "AR_channel",
                        Integer.class,
                        Bundle.LBL_AR_channel(),
                        Bundle.DCT_AR_channel()) {
                            @Override
                            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                                return AnalyzerRule.this.getChannel();
                            }

                            @Override
                            public void setValue(Integer channel) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                AnalyzerRule.this.setChannel(channel);
                            }
                        });
                set.put(new PropertySupport.ReadWrite<Integer>(
                        "AR_minimum",
                        Integer.class,
                        Bundle.LBL_AR_minimum(),
                        Bundle.DCT_AR_minimum()) {
                            @Override
                            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                                return AnalyzerRule.this.getMinimum();
                            }

                            @Override
                            public void setValue(Integer minimum) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                AnalyzerRule.this.setMinimum(minimum);
                            }
                        });
                set.put(new PropertySupport.ReadWrite<Integer>(
                        "AR_maximum",
                        Integer.class,
                        Bundle.LBL_AR_maximum(),
                        Bundle.DCT_AR_maximum()) {
                            @Override
                            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                                return AnalyzerRule.this.getMaximum();
                            }

                            @Override
                            public void setValue(Integer maximum) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                AnalyzerRule.this.setMaximum(maximum);
                            }
                        });
                set.put(new PropertySupport.ReadWrite<Double>(
                        "AR_magnitude",
                        Double.class,
                        Bundle.LBL_AR_magnitude(),
                        Bundle.DCT_AR_magnitude()) {
                            @Override
                            public Double getValue() throws IllegalAccessException, InvocationTargetException {
                                return AnalyzerRule.this.getMagnitude();
                            }

                            @Override
                            public void setValue(Double magnitude) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                AnalyzerRule.this.setMagnitude(magnitude);
                            }
                        });
                Sheet sheet = super.createSheet();
                sheet.put(set);
                return sheet;
            }
        };
    }
}
