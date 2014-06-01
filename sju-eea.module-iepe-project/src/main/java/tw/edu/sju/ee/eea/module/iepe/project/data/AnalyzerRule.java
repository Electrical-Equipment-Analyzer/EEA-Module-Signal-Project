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

import org.dom4j.Element;

/**
 *
 * @author Leo
 */
public class AnalyzerRule {

    private String name;
    private int channel;
    private int maximum;
    private int minimum;
    private int magnitude;

    public AnalyzerRule(Element element) {
        this.name = element.elementText("name");
        this.channel = Integer.parseInt(element.elementText("channel"));
        this.maximum = Integer.parseInt(element.elementText("maximum"));
        this.minimum = Integer.parseInt(element.elementText("minimum"));
        this.magnitude = Integer.parseInt(element.elementText("magnitude"));
    }

    @Override
    public String toString() {
        return "AnalyzerRule{" + "name=" + name + ", channel=" + channel + ", maximum=" + maximum + ", minimum=" + minimum + ", magnitude=" + magnitude + '}';
    }


}
