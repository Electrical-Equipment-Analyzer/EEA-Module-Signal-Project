/*
 * Copyright (c) 2015, National Taiwan University of Science and Technology,
 * Department of Electrical Engineering EE-305. All rights reserved.
 */
package tw.edu.sju.ee.eea.module.signal.device;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
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
public class SignalDeviceObject extends AbstractNode implements ChannelList<DeviceChannel>, NavigatorLookupHint, PropertyChangeListener, Serializable {

    public static final String MIMETYPE = "application/device";
    private TopComponent tc;
    private ArrayList<DeviceChannel> channels = new ArrayList<DeviceChannel>();
    private EEAInput input;
    private EEADevice device;
    private List list;

    public SignalDeviceObject(EEADevice device, List list) {
        super(Children.LEAF);
        this.device = device;
        this.list = list;
        String showInputDialog = JOptionPane.showInputDialog(tc, "Device Name", "Message", JOptionPane.QUESTION_MESSAGE);
        setName(showInputDialog);
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
        input = new EEAInput(device);
        for (int i = 0; i < input.getIOChannel().length; i++) {
            channels.add(new DeviceChannel(input.getIOChannel(i), this.device, i));
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
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new AbstractAction("Delete") {

            @Override
            public void actionPerformed(ActionEvent e) {
                list.remove(SignalDeviceObject.this);
            }
        });
        return actions.toArray(new AbstractAction[actions.size()]);
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                if (tc == null) {
//                    tc = MultiViews.createMultiView("application/function", SignalDeviceObject.this);
//                }
//                tc.open();
//                tc.requestActive();
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
        Sheet sheet = super.createSheet();
        for (Sheet.Set property : device.getProperties(this)) {
            sheet.put(property);
        }
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("update..........");
        System.out.println(evt);
        if (evt.getPropertyName() == null) {
            return ;
        }
        setSheet(createSheet());
    }
    
  
}
