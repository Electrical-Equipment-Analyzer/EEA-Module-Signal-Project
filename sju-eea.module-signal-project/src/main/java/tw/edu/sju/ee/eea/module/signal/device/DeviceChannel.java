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
package tw.edu.sju.ee.eea.module.signal.device;

import tw.edu.sju.ee.eea.utils.io.tools.DeviceInfo;
import java.awt.Color;
import java.lang.reflect.Method;
import tw.edu.sju.ee.eea.module.signal.io.Channel;
import tw.edu.sju.ee.eea.utils.io.tools.InputChannel;

/**
 *
 * @author D10307009
 */
public class DeviceChannel implements Channel {

    private InputChannel ic;
    private DeviceInfo device;
    private String name;
    private int channel;

    public DeviceChannel(InputChannel ic, DeviceInfo device, int channel) {
        this.ic = ic;
        this.device = device;
        this.channel = channel;
        setDefaultName();
    }

    public void setDefaultName() {
        this.name = this.device.getDeviceName() + "/" + this.channel;
    }

    @Override
    public DeviceInfo getDeviceInfo() {
        return device;
    }

    public InputChannel getInput() {
        return ic;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDevice() {
        return this.device.getDeviceName();
    }

    @Override
    public int getChannel() {
        return this.channel;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void setColor(Color color) {
        return;
    }

}
