/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.edu.sju.ee.eea.module.signal.oscillogram.surface;

import java.util.ArrayList;
import org.sf.surfaceplot.ISurfacePlotModel;

/**
 *
 * @author salagarsamy
 */
public class ExampleSurfaceModel implements ISurfacePlotModel {

    private float x;
    private float y;
    private float time;
    private int resolution;
    ArrayList<double[]> data = new ArrayList();

    public ExampleSurfaceModel() {
        this.x = 10;
        this.y = 5000;
        this.time = 10;

        this.resolution = 100;

    }

    public void add(double[] d) {
        this.data.add(d);
        if (this.data.size() > 10) {
            this.data.remove(0);
        }
    }

//    private double[] dd() {
//        double[] d = new double[1024];
//    }
    public float calculateZ(float x, float y) {
        int xx = (int) x;
        int yy = (int) (y / 10);
        if (data.size() <= xx) {
            return Float.NaN;
        }
        double d;
        try {
            d = data.get(xx)[yy];
        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
            return Float.NaN;
        }
        System.out.println(xx + ", " + yy + ", " + d);
        return (float) d;
    }

    public int getPlotMode() {
        return ISurfacePlotModel.PLOT_MODE_SPECTRUM;
    }

    public boolean isBoxed() {
        return true;
    }

    public boolean isMesh() {
        return true;
    }

    public boolean isScaleBox() {
        return false;
    }

    public boolean isDisplayXY() {
        return true;
    }

    public boolean isDisplayZ() {
        return true;
    }

    public boolean isDisplayGrids() {
        return true;
    }

    public int getCalcDivisions() {
        return resolution;
    }

    public int getDispDivisions() {
        return resolution;
    }

    public float getXMin() {
        return x - time;
    }

    public float getXMax() {
        return x;
    }

    public float getYMin() {
        return 0;
    }

    public float getYMax() {
        return y;
    }

    public float getZMin() {
        return 0;
    }

    public float getZMax() {
        return 10000;
    }

    public String getXAxisLabel() {
        return "Time";
    }

    public String getYAxisLabel() {
        return "Frequency";
    }

    public String getZAxisLabel() {
        return "Amp";
    }
}
