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
package tw.edu.sju.ee.eea.module.iepe.project.ui;

import java.awt.Paint;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import tw.edu.sju.ee.eea.ui.chart.SampledChart;

/**
 *
 * @author Leo
 */
public class SampledManager {

    private XYSeriesCollection collection;
    private XYItemRenderer renderer;

    public SampledManager() {
        this.collection = new XYSeriesCollection();
        this.renderer = SampledChart.creatrRenderer();
    }

    public XYSeriesCollection getCollection() {
        return collection;
    }

    public XYItemRenderer getRenderer() {
        return renderer;
    }

    public void set(int index, Comparable key) {
        collection.getSeries(index).setKey(key);
    }

    public void set(int index, Paint paint) {
        renderer.setSeriesPaint(index, paint);
    }

}
