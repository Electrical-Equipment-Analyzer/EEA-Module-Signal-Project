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

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.Date;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.windows.IOColorLines;
import org.openide.windows.InputOutput;

/**
 *
 * @author Leo
 */
public class Warning {

    private Date date;
    private AnalyzerRule rule;
    private double frequency;
    private double value;

    public Warning(AnalyzerRule rule, Date date, double frequency, double value) {
        this.rule = rule;
        this.date = date;
        this.frequency = frequency;
        this.value = value;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getValue() {
        return value;
    }

    public void print(InputOutput io) {
        try {
            IOColorLines.println(io, this.toString(), Color.BLACK);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public String toString() {
        return date + " Warning: " + rule + ", frequency=" + frequency + ", value=" + value;
    }


    public boolean equals(Warning w, long i) {
        return Math.abs(this.date.getTime() - w.date.getTime()) < i;
    }

//    public Node createNodeDelegate() {
//        return new AbstractNode(Children.LEAF) {
//
//            @Override
//            public Image getIcon(int type) {
//                return ImageUtilities.loadImage("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
//            }
//
//            @Override
//            public Image getOpenedIcon(int type) {
//                return getIcon(type);
//            }
//
//            @Override
//            public String getDisplayName() {
//                return Warning.this.getName();
//            }
//        };
//    }
}
