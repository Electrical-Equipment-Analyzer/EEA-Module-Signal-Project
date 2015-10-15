/*
 * Copyright (c) 2015, National Taiwan University of Science and Technology,
 * Department of Electrical Engineering EE-305. All rights reserved.
 */
package tw.edu.sju.ee.eea.module.signal.device;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.module.signal.io.ChannelList;
import tw.edu.sju.ee.eea.utils.io.tools.EEADevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEAInput;

/**
 *
 * @author 薛聿明
 */
public class SignalDeviceObject extends AbstractNode implements ChannelList<DeviceChannel>, NavigatorLookupHint, Serializable {

    public static final String MIMETYPE = "application/device";
    private TopComponent tc;
    private ArrayList<DeviceChannel> channels = new ArrayList<DeviceChannel>();
    private EEAInput input;
    private EEADevice device;

    public SignalDeviceObject(EEADevice device) {
        super(Children.LEAF);
        this.device = device;
        setName(this.device.getDeviceName());
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
        input = new EEAInput(device);
        for (int i = 0; i < input.getIOChannel().length; i++) {
            channels.add(new DeviceChannel(input.getIOChannel(i), this.device.getDeviceName(), i));
        }
    }

    public EEAInput getInput() {
        return input;
    }

    @Override
    public List<DeviceChannel> getChannels() {
        return channels;
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tc == null) {
                    tc = MultiViews.createMultiView("application/function", SignalDeviceObject.this);
                }
                tc.open();
                tc.requestActive();
            }
        };
    }

    @Override
    public String getContentType() {
        return MIMETYPE; // NOI18N
    }

    @NbBundle.Messages({
        "LBL_title=Device Properties",
        "LBL_name=Name",
        "LBL_model=Model",
        "LBL_serialnumber=Serial Number",
        "LBL_samplerate=Samplerate"
    })
    @Override
    protected Sheet createSheet() {
        Sheet.Set set = new Sheet.Set();
        set.setName(Bundle.LBL_title());
        set.put(new PropertySupport.ReadOnly(
                Bundle.LBL_name(),
                String.class,
                Bundle.LBL_name(),
                Bundle.LBL_name()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return SignalDeviceObject.this.device.getDeviceName();
                    }
                });
        set.put(new PropertySupport.ReadOnly(
                Bundle.LBL_model(),
                String.class,
                Bundle.LBL_model(),
                Bundle.LBL_model()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return SignalDeviceObject.this.device.getDeviceModel();
                    }
                });
        set.put(new PropertySupport.ReadOnly(
                Bundle.LBL_serialnumber(),
                String.class,
                Bundle.LBL_serialnumber(),
                Bundle.LBL_serialnumber()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return SignalDeviceObject.this.device.getSerialNumber();
                    }
                });
        set.put(new PropertySupport.ReadWrite<Integer>(
                Bundle.LBL_samplerate(),
                Integer.class,
                Bundle.LBL_samplerate(),
                Bundle.LBL_samplerate()) {
                    @Override
                    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                        return 10000;
                    }

                    @Override
                    public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        
                    }

                });
        Sheet sheet = super.createSheet();
        sheet.put(set);
        return sheet;
    }
}
