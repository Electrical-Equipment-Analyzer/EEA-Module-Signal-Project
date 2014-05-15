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
package tw.edu.sju.ee.eea.module.iepe.channel;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import tw.edu.sju.ee.eea.module.iepe.project.object.IepeRealtimeObject;
import tw.edu.sju.ee.eea.ui.chart.SampledChart;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;

/**
 *
 * @author Leo
 */
public class ChannelList extends ArrayList<Channel> implements Runnable {

    private Lookup lkp;
    private IEPEInput manager;
    private XYSeriesCollection collection;
    private XYItemRenderer renderer;

    public ChannelList(IEPEInput manager) {
        this.lkp = Lookups.singleton(this);
        this.manager = manager;

        collection = new XYSeriesCollection();
        renderer = SampledChart.creatrRenderer();
    }

    public XYSeriesCollection getCollection() {
        return collection;
    }

    public XYItemRenderer getRenderer() {
        return renderer;
    }
    
    public void addChannel(Channel channel) {
        manager.addStream(channel.getChannel(), channel.getStream());
        collection.addSeries(channel.getSeries());
        add(channel);
    }
    
    private void setColor(Channel channel, Color color) {
        renderer.setSeriesPaint(this.indexOf(channel), color);
    }
    
    private Paint getColor(Channel channel) {
        return renderer.getSeriesPaint(this.indexOf(channel));
    }

    @Override
    public void run() {
        long time = Calendar.getInstance().getTimeInMillis();
        while (true) {
            Iterator<Channel> iterator = this.iterator();
            while (iterator.hasNext()) {
                Channel next = iterator.next();
                try {
                    next.proc(time);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            time += 100;
        }
    }

    public Node createNodeDelegate() {
        return new AbstractNode(new ChannelChildren()) {

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
                return "Real-time::ipt";
            }

        };
    }

    public class ChannelChildren extends Children.Keys<Channel> {

        public ChannelChildren() {
        }

        @Override
        protected Node[] createNodes(Channel key) {
            return new Node[]{key.createNodeDelegate()};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(ChannelList.this.toArray(new Channel[]{}));
        }

    }
}
