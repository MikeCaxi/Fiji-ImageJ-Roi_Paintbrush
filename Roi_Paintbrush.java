import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.*;
import ij.plugin.tool.PlugInTool;
import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


@Plugin(name = "Roi Paintbrush", type = Command.class, menuPath = "Plugins > Roi Paintbrush", headless = true)
public class Roi_Paintbrush extends JFrame implements Command {
    ArrayList<ImageCanvas> canvasList = new ArrayList<>();
    ImageCanvas ic;
    Boolean isAlive = true;
    JSpinner changeRad;
    SpinnerNumberModel spinnerModel;
    JCheckBox isOn;
    @Override
    public void run() {
        this.setBounds(200, 200, 200, 75);
        this.setLayout(new GridBagLayout());
        this.setTitle("Roi Paintbrush");
        this.addWindowListener(exitListener);

        isOn = new JCheckBox("Enable");
        spinnerModel = new SpinnerNumberModel();
        changeRad = new JSpinner(spinnerModel);
        spinnerModel.setValue(16.0);
        spinnerModel.setStepSize(2.0);

        this.add(isOn);
        this.add(changeRad);

        (new GetCanvasThread()).start();

        this.setVisible(true);
    }

    private class GetCanvasThread extends Thread{
        @Override
        public void run() {
            while(isAlive){
                ic = WindowManager.getCurrentWindow().getCanvas();
                if(!canvasList.contains(ic)){
                    canvasList.add(ic);
                    ic.addMouseListener(canvasListener);
                }
                try{sleep(50);} catch (InterruptedException e){}
            }
        }
    }

    MouseListener canvasListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if(isOn.isSelected()) {
                ic.addMouseListener(pressedListener);
                ic.addMouseMotionListener(motionListener);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ic.removeMouseListener(pressedListener);
            ic.removeMouseMotionListener(motionListener);
            ic.getImage().setOverlay(null);
        }
    };

    MouseMotionListener motionListener = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
            Thread t = new Thread(){
                public void run() {
                    ShapeRoi roi;
                    ImagePlus imp = ic.getImage();
                    if (imp.getRoi() != null) roi = new ShapeRoi(imp.getRoi());
                    else roi = new ShapeRoi(new Rectangle(0, 0, 0, 0));
                    double r = Double.parseDouble(changeRad.getValue().toString());
                    imp.setOverlay(new Overlay(new OvalRoi(ic.getCursorLoc().x - r / 2, ic.getCursorLoc().y - r / 2, r, r)));
                    roi.or(new ShapeRoi(new OvalRoi(ic.getCursorLoc().x - r / 2, ic.getCursorLoc().y - r / 2, r, r)));
                    imp.setRoi(roi);
                }
            };
            t.start();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Thread t = new Thread(){
                public void run() {
                    ImagePlus imp = ic.getImage();
                    double r = Double.parseDouble(changeRad.getValue().toString());
                    imp.setOverlay(new Overlay(new OvalRoi(ic.getCursorLoc().x - r / 2, ic.getCursorLoc().y - r / 2, r, r)));
                }
            };
            t.start();
        }
    };

    MouseListener pressedListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            Thread t = new Thread(){
                public void run() {
                    ShapeRoi roi;
                    ImagePlus imp = ic.getImage();
                    if (imp.getRoi() != null) roi = new ShapeRoi(imp.getRoi());
                    else roi = new ShapeRoi(new Rectangle(0, 0, 0, 0));
                    double r = Double.parseDouble(changeRad.getValue().toString());
                    imp.setOverlay(new Overlay(new OvalRoi(ic.getCursorLoc().x - r / 2, ic.getCursorLoc().y - r / 2, r, r)));
                    roi.or(new ShapeRoi(new OvalRoi(ic.getCursorLoc().x - r / 2, ic.getCursorLoc().y - r / 2, r, r)));
                    imp.setRoi(roi);
                }
            };
            t.start();
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    };

    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            isAlive = false;
            ic = null;
            for(ImageCanvas ic : canvasList){
                ic.removeMouseListener(canvasListener);
                ic.removeMouseListener(pressedListener);
                ic.removeMouseMotionListener(motionListener);
            }
            Roi_Paintbrush.super.dispose();
        }
    };

    public static void main(String[] args){
        ImageJ ij = new ImageJ();
        ij.launch();
    }
}

