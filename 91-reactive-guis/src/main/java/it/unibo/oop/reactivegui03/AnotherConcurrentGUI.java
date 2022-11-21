package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int STOP= 10_000;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");


    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        final Crono crono = new Crono(agent);
        var  t= new Thread(agent);
        var c= new Thread(crono);
        c.start();
        t.start();
        
        up.addActionListener((e)->agent.countup());
        down.addActionListener((e)->agent.countdown());
    }
    private class Crono implements Runnable{
       private Agent a;
        public Crono (Agent agent){
         this.a=agent;
       }
        @Override
        public void run(){
            try {
                Thread.sleep(STOP);
                a.stopCounting();
                SwingUtilities.invokeAndWait(() ->{
                    down.setEnabled(false);  
                    up.setEnabled(false);
                });
            
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean up=true;
        private volatile boolean down=false;
        private int counter;
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    if(up){
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                        this.counter++;
                        Thread.sleep(100);
                    }else{
                        if(down){
                            final var nextText = Integer.toString(this.counter);
                            SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                            this.counter--;
                            Thread.sleep(100);
                        }
                    }
                    
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
        public void stopCounting() {
            this.stop = true;
        }
        public void countdown(){
            this.down =true;
            this.up= false;
        }
        public void countup(){
            this.down =false;
            this.up= true;
        }
    }
}
