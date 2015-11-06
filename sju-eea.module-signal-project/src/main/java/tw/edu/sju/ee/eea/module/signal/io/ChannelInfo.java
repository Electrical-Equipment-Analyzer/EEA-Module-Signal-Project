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
package tw.edu.sju.ee.eea.module.signal.io;

import tw.edu.sju.ee.eea.utils.io.tools.DeviceInfo;

/**
 *
 * @author D10307009
 */
public interface ChannelInfo {
    
    public String getName();

    public void setName(String name);

    public String getDevice();

    public int getChannel();
    
    public DeviceInfo getDeviceInfo();
}
