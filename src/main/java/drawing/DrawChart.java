package drawing;

import drawing.enums.AxisType;
import drawing.utils.ChartUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


@NoArgsConstructor
@AllArgsConstructor
public class DrawChart {

    private static final String yLabel = "Temperature [ºC]";
    private static final String xLabel = "Time";
    private static final String title = "Cell survival curve from MC methon";
    private XYSeriesCollection collection;
    private XYSeries clinicalData;

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
