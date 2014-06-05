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
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Leo
 */
public class AnalyzerRule {

    private String name;
    private int channel;
    private int maximum;
    private int minimum;
    private double magnitude;
    private List<Warning> warming = new ArrayList<Warning>();

    public AnalyzerRule(Element element) {
        this.name = element.elementText("name");
        this.channel = Integer.parseInt(element.elementText("channel"));
        this.maximum = Integer.parseInt(element.elementText("maximum"));
        this.minimum = Integer.parseInt(element.elementText("minimum"));
        this.magnitude = Double.parseDouble(element.elementText("magnitude"));
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setChannel(int channel) {
        this.channel = channel;
    }

    private void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    private void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    private void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getName() {
        return name;
    }

    public int getChannel() {
        return channel;
    }

    public int getMaximum() {
        return maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void addWarning(Warning warning) {
        System.out.println(this.warming.size());
        if (this.warming.size() < 1 || !this.warming.get(this.warming.size() - 1).equals(warning, 5000)) {
            this.warming.add(warning);
            System.out.println("*************add************");
        }
    }

    @Override
    public String toString() {
        return "AnalyzerRule{" + "name=" + name + ", channel=" + channel + ", maximum=" + maximum + ", minimum=" + minimum + ", magnitude=" + magnitude + '}';
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
        return new AbstractNode(new RuleChildren()) {

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

    public class RuleChildren extends Children.Keys<Warning> {

        @Override
        protected Node[] createNodes(Warning key) {
            return new Node[]{key.createNodeDelegate()};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(warming);
        }

    }
}
