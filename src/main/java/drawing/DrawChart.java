package drawing;

import drawing.enums.AxisType;
import drawing.utils.ChartUtils;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class DrawChart {

    private static final String yLabel = "Temperature [ºC]";
    private static final String xLabel = "Time";
    private static final String title = "Cell survival curve from MC methon";
    private XYSeriesCollection collection;
    private XYSeries clinicalData;

    public void saveContent(String pathname, List<Pair<Double, Double>> pair) {
        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries clinicalData = ChartUtils.createPublicationData();
        XYSeries simulationData = ChartUtils.createSeries(ChartUtils.extractAxis(pair).getKey(), ChartUtils.extractAxis(pair).getValue(), "MC Simulation");
        ChartUtils.addSeries(clinicalData, collection);
        ChartUtils.addSeries(clinicalData, collection);
        JFreeChart chart = ChartUtils.createPlot(collection, AxisType.LINEAR, AxisType.LOG10, xLabel, yLabel, title);
        ChartUtils.saveChart(chart, pathname);
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
