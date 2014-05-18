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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import tw.edu.sju.ee.eea.module.iepe.project.ui.SampledManager;
import tw.edu.sju.ee.eea.module.iepe.project.ui.SampledSeries;
import tw.edu.sju.ee.eea.util.iepe.IEPEInput;

/**
 *
 * @author Leo
 */
public class ChannelList extends ArrayList<Channel> implements Channel.Renderer {

    private Lookup lkp;
    private List<SampledManager> list = new ArrayList<SampledManager>();

    public ChannelList() {
        this.lkp = Lookups.singleton(this);
    }

    public SampledManager createSampledManager(IEPEInput iepe, XYItemRenderer renderer, Class<? extends SampledSeries> c) {
        SampledManager manager = new SampledManager(renderer);
        for (int i = 0; i < this.size(); i++) {
            try {
                Channel channel = this.get(i);
                SampledSeries series = channel.createSampledSeries(c);
                iepe.addStream(channel.getChannel(), series.getStream());
                manager.getCollection().addSeries(series);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
//            } catch (InstantiationException ex) {
//                Exceptions.printStackTrace(ex);
//            } catch (IllegalAccessException ex) {
//                Exceptions.printStackTrace(ex);
//            } catch (NoSuchMethodException ex) {
//                Exceptions.printStackTrace(ex);
//            } catch (IllegalArgumentException ex) {
//                Exceptions.printStackTrace(ex);
            } catch (ReflectiveOperationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        this.list.add(manager);
        return manager;
    }

    public boolean remove(SampledManager manager) {
        return this.list.remove(manager);
    }

    @Override
    public Color getColor(Channel channel) {
        int index = this.indexOf(channel);
        Iterator<SampledManager> iterator = this.list.iterator();
        while (iterator.hasNext()) {
            SampledManager manager = iterator.next();
            return (Color) manager.getRenderer().getSeriesPaint(index);
        }
        return null;
    }

    @Override
    public void set(Channel channel, Class c, Object o) {
        int index = this.indexOf(channel);
        Iterator<SampledManager> iterator = this.list.iterator();
        while (iterator.hasNext()) {
            SampledManager manager = iterator.next();
            try {
                Method method = manager.getClass().getMethod("set", int.class, c);
                method.invoke(manager, index, o);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SecurityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
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
            return new Node[]{key.createNodeDelegate(ChannelList.this)};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(ChannelList.this.toArray(new Channel[]{}));
        }

    }
}
