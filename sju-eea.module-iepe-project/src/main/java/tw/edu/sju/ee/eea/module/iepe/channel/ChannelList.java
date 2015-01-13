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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Leo
 */
public class ChannelList extends ArrayList<Channel> implements SourceChannel.Renderer {

    private Lookup lkp;
    private List<ChannelsConfigure> list = new ArrayList<ChannelsConfigure>();

    public ChannelList(Lookup lkp) {
        this.lkp = lkp;
    }

    public void addConfigure(ChannelsConfigure config) {
        this.list.add(config);
    }

    @Override
    public Color getColor(Channel channel) {
        int index = this.indexOf(channel);
        Iterator<ChannelsConfigure> iterator = this.list.iterator();
        while (iterator.hasNext()) {
            return iterator.next().getChannelColor(index);
        }
        return null;
    }

    @Override
    public void setName(Channel channel, String name) {
        int index = this.indexOf(channel);
        Iterator<ChannelsConfigure> iterator = this.list.iterator();
        while (iterator.hasNext()) {
            iterator.next().setChannelName(index, name);
        }
    }

    @Override
    public void setColor(Channel channel, Color color) {
        int index = this.indexOf(channel);
        Iterator<ChannelsConfigure> iterator = this.list.iterator();
        while (iterator.hasNext()) {
            iterator.next().setChannelColor(index, color);
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
                return "Channel List";
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
