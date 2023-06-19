package drawing.utils;

import drawing.enums.AxisType;
import javafx.util.Pair;
import lombok.experimental.UtilityClass;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class ChartUtils {

    private final List<Color> colors = Arrays.asList(Color.DARK_GRAY, Color.RED, Color.CYAN, Color.CYAN, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.MAGENTA);
    private final List<Double> y = Arrays.asList(0.957372792, 0.728051603, 0.510632095, 0.270665207, 0.100624261, 0.04005924);
    private final List<Double> x = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
    private final Pair<List<Double>, List<Double>> publicationData = new Pair<>(x, y);

    public void saveChart(JFreeChart jFreeChart, String pathname) {
        BufferedImage objBufferedImage = jFreeChart.createBufferedImage(600, 800);
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        try {
            ImageIO.write(objBufferedImage, "png", bas);
            ImageIO.write(ImageIO.read(new ByteArrayInputStream(bas.toByteArray())), "png", new File(pathname));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public XYSeries createPublicationData() {
        XYSeries series = new XYSeries("Current temperature");
        for (int i = 0; i < x.size(); i++) {
            series.add((Double) x.get(i), (Double) y.get(i));
        }
        return series;
    }

    public <T> XYSeries createSeries(List<T> xSet, List<T> ySet, String key) {
        XYSeries series = new XYSeries(key);
        for (int i = 0; i < xSet.size(); i++) {
            series.add((Double) xSet.get(i), (Double) ySet.get(i));
        }
        return series;
    }

    public void addSeries(XYSeries series, XYSeriesCollection dataset) {
        dataset.addSeries(series);
    }

    public JFreeChart createPlot(XYSeriesCollection dataset, AxisType xType, AxisType yType, String xLabel, String yLabel, String title) {
        Object xAxis = AxisType.LINEAR.equals(xType) ? new NumberAxis(xLabel) : new LogAxis(xLabel);
        Object yAxis = AxisType.LINEAR.equals(yType) ? new NumberAxis(yLabel) : new LogAxis(yLabel);
        ((ValueAxis) xAxis).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ((ValueAxis) yAxis).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        if (AxisType.LOG10.equals(xType)) {
            ((LogAxis) xAxis).setBase(10);
        } else if (AxisType.LOG10.equals(yType)) {
            ((LogAxis) yAxis).setBase(10);
            ((LogAxis) yAxis).setAutoRangeMinimumSize(Math.pow(10, -5));
        }
        XYPlot plot = new XYPlot(dataset,
                AxisType.LOG10.equals(xType) ? ((ValueAxis) xAxis) : ((ValueAxis) xAxis),
                AxisType.LOG10.equals(xType) ? ((ValueAxis) yAxis) : ((ValueAxis) yAxis),
                createShapeRenderer(dataset.getSeriesCount()));
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainMinorGridlinesVisible(true);
        plot.setRangeMinorGridlinesVisible(true);
        plot.getRangeAxis().setStandardTickUnits(LogAxis.createLogTickUnits(Locale.ENGLISH));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle(title,
                        new Font("Serif", Font.BOLD, 18)
                )
        );
        return chart;
    }

    public XYLineAndShapeRenderer createShapeRenderer(int size) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < size; i++) {
            if(i == 0) {
                renderer.setSeriesPaint(0, colors.get(0));
                renderer.setSeriesStroke(0, new BasicStroke(1.0f));
                renderer.setSeriesShapesVisible(0, true);
                renderer.setBaseLinesVisible(true);
                renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));
            } else {
                renderer.setSeriesPaint(i, colors.get(i));
                renderer.setSeriesStroke(i, new BasicStroke(1.0f));
                renderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));
                renderer.setSeriesShapesVisible(i, true);
                renderer.setBaseLinesVisible(true);
            }
        }
        return renderer;
    }

    public Pair<List<Double>, List<Double>> extractAxis(List<Pair<Double, Double>> pair) {
        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();
        System.out.println("\n");
        pair.forEach(a -> {
            xs.add(a.getKey());
            ys.add(a.getValue());
            System.out.println(a.getKey().toString().replace(".", ",") + "\t" + a.getValue().toString().replace(".", ","));
        });
        System.out.println("\n");
        return new Pair<>(xs, ys);
    }
}
