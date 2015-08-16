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
import org.openide.nodes.Node;

/**
 *
 * @author Leo
 */
public abstract class Channel {

    public abstract int getDevice();

    public abstract int getChannel();

    public abstract String getName();

    public abstract void update(double t);

    public abstract Node createNodeDelegate(final Channel.Renderer renderer);

    interface Renderer {

        public void setName(Channel channel, String name);

        public void setColor(Channel channel, Color color);

        public Color getColor(Channel channel);
    }
}
