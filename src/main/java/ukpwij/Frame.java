package ukpwij;


import drawing.enums.AxisType;
import drawing.utils.ChartUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Frame extends JFrame {

    private static final String title1 = "Układy kontrolno-pomiarowe w instalacjach jądrowych";
    private static final String yLabel = "Temperature [ºC]";
    private static final String xLabel = "Time";
    private static final String title = "Cell survival curve from MC methon";
    private XYSeriesCollection collection;
    private XYSeries clinicalData;

    private JPanel panel = new JPanel();
    private JPanel panel2 = new JPanel();
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JFreeChart chart;
    private ScheduledExecutorService executor;


    public Frame() {
        super(title1);
        panel.setPreferredSize(new Dimension(900, 400));
        panel.setLayout(new BorderLayout());
        panel2.setLayout(new GridLayout(1, 2));

        panel2.add(startButton);
        panel2.add(stopButton);

        panel.add(panel2, BorderLayout.PAGE_END);
        chart = createChart();
        setChart(chart);
        runAThread();
        getContentPane().add(panel);
        pack();
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void runAThread() {
        executor = Executors.newScheduledThreadPool(1);
        Runnable helloRunnable = () -> {
            System.out.println("Hello world");
            int i = 0;
            addPoint((double) ++i, 2.);
        };
        executor.scheduleAtFixedRate(helloRunnable, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void setChart(JFreeChart chart) {
        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        panel.add(cp, BorderLayout.CENTER);
    }
    public JFreeChart createChart() {
        collection = new XYSeriesCollection();
        clinicalData = ChartUtils.createPublicationData();
        ChartUtils.addSeries(clinicalData, collection);
        return ChartUtils.createPlot(collection, AxisType.LINEAR, AxisType.LINEAR, xLabel, yLabel, title);
    }
    public void addPoint(Double x, Double y) {
        clinicalData.add(x, y);
    }

}
