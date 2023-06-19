package ukpwij;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example to show how we can create a dynamic chart.
 */
public class DynamicLineAndTimeSeriesChart extends ApplicationFrame implements ActionListener {

    private final String ip = "czopowicz.pl";
    private final int port = 65432;

    /** The time series data. */
    private TimeSeries series;

    /** The most recent value added. */
    private double lastValue = 100.0;

    /** Timer to refresh graph after every 1/4th of a second */
    private Timer timer = new Timer(250, this);

    private static final List<String> errors = Arrays.asList("E1", "E2", "E3");
    private static final String yLabel = "Temperature [ºC]";
    private static final String xLabel = "Time";
    private static final String title = "Pobieranie danych z termometru";
    private boolean isActive = true;

    private JPanel panel = new JPanel();
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JButton saveButton = new JButton("Save");
    private Client client;
    /**
     * Constructs a new dynamic chart application.
     *
     * @param title  the frame title.
     */
    public DynamicLineAndTimeSeriesChart(final String title) throws IOException {
        super(title);
        client = new Client(ip, port);
        this.series = new TimeSeries("Random Data", Millisecond.class);
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        final JFreeChart chart = createChart(dataset);
        timer.setInitialDelay(1000);
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
        final JPanel content = new JPanel(new BorderLayout());

        final ChartPanel chartPanel = new ChartPanel(chart);
        content.add(chartPanel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(1, 3));
        panel.add(startButton);
        panel.add(stopButton);
        panel.add(saveButton);
        startButton.addActionListener(e -> isActive = true);
        stopButton.addActionListener(e -> isActive = false);
        saveButton.addActionListener(e -> {
            try {
                saveData();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        content.add(panel, BorderLayout.PAGE_END);

        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        setContentPane(content);
        timer.start();
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                title,
                xLabel,
                yLabel,
                dataset,
                true,
                true,
                false
        );

        final XYPlot plot = result.getXYPlot();

        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);
        xaxis.setFixedAutoRange(60000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);
        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(29.32, 29.35);  // tu zmieniasz zakres na osi Y

        return result;
    }

    public void actionPerformed(final ActionEvent e) {
        String val = "0";
        if (isActive) {
            try {
                val = client.sendMessage("read");
                System.out.println(val);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if(errors.stream().noneMatch(val::equals)) {
                this.lastValue = Double.parseDouble(val.replace("T=", ""));
                this.series.add(new Millisecond(), this.lastValue);
            }

//            System.out.println("Current Time in Milliseconds = " + now.toString() + ", Current Value : " + this.lastValue);
        }
    }

    private void saveData() throws IOException {
        List<TimeSeriesDataItem> list = (List<TimeSeriesDataItem>) this.series.getItems().stream().collect(Collectors.toList());
        File file = new File("C:"+ File.separator +"Users"+ File.separator +"twojlogin"+ File.separator +"Desktop" + File.separator + "file.txt");
        if(!file.exists())
            file.createNewFile();
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getPath()), StandardOpenOption.APPEND);
        for(int i = 0; i < this.series.getItems().size(); i++) {
            writer.write(this.series.getValue(i).toString() + " " + this.series.getDataItem(i).getPeriod().toString());
            writer.newLine();
        }
        writer.flush();
        System.exit(0);
    }
    /**
     * Starting point for the dynamic graph application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) throws IOException {

        final DynamicLineAndTimeSeriesChart demo = new DynamicLineAndTimeSeriesChart("Pobieranie temperatury z serwera");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}