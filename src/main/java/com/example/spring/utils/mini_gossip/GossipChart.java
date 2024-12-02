package com.example.spring.utils.mini_gossip;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.ArrayList;

public class GossipChart extends JFrame {

    public GossipChart(String title, ArrayList<Integer> times, ArrayList<Integer> nodeCounts, ArrayList<Double> errors, boolean b) {
        super(title);
        // Create the chart for errors
        JFreeChart errorChart = createLineChartWithLogScale("NodeCounts vs Errors", "Node Count", "Error", createErrorDataset(errors, nodeCounts));
        // Create the chart for times
        JFreeChart timesChart = createLineChart("NodeCounts vs Times", "Node Count", "Times", createTimesDataset(times, nodeCounts));

        // Create Panels for each chart
        ChartPanel errorChartPanel = new ChartPanel(errorChart);
        ChartPanel timesChartPanel = new ChartPanel(timesChart);

        errorChartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        timesChartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // Create a layout for both charts
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(errorChartPanel);
        panel.add(timesChartPanel);

        setContentPane(panel);
    }
    public GossipChart(String title, ArrayList<Integer> times, ArrayList<Double> ks, ArrayList<Double> errors) {
        super(title);
        // Create the chart for errors
        JFreeChart errorChart = createLineChartWithLogScale("k vs Errors", "k", "Error", createErrorDataset2(errors, ks));
        // Create the chart for times
        JFreeChart timesChart = createLineChart("k vs Times", "k", "Times", createTimesDataset2(times, ks));

        // Create Panels for each chart
        ChartPanel errorChartPanel = new ChartPanel(errorChart);
        ChartPanel timesChartPanel = new ChartPanel(timesChart);

        errorChartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        timesChartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // Create a layout for both charts
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(errorChartPanel);
        panel.add(timesChartPanel);

        setContentPane(panel);
    }

    // Method to create the dataset for the first chart (nodeCounts vs errors)
    public static DefaultCategoryDataset createErrorDataset( ArrayList<Double> errors,ArrayList<Integer> nodeCounts) {
//        ArrayList<Double> errors = new ArrayList<>(); // Replace with actual data
//        ArrayList<Integer> nodeCounts = new ArrayList<>(); // Replace with actual data
//
//        // Example data, replace with real data
//        errors.add(1.2);
//        errors.add(0.9);
//        errors.add(0.5);
//        errors.add(0.3);
//        nodeCounts.add(10);
//        nodeCounts.add(20);
//        nodeCounts.add(30);
//        nodeCounts.add(40);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < nodeCounts.size(); i++) {
            dataset.addValue(errors.get(i), "Error", nodeCounts.get(i));
        }
        return dataset;
    }
    public static DefaultCategoryDataset createErrorDataset2( ArrayList<Double> errors,ArrayList<Double> ks) {
//        ArrayList<Double> errors = new ArrayList<>(); // Replace with actual data
//        ArrayList<Integer> nodeCounts = new ArrayList<>(); // Replace with actual data
//
//        // Example data, replace with real data
//        errors.add(1.2);
//        errors.add(0.9);
//        errors.add(0.5);
//        errors.add(0.3);
//        nodeCounts.add(10);
//        nodeCounts.add(20);
//        nodeCounts.add(30);
//        nodeCounts.add(40);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < ks.size(); i++) {
            dataset.addValue(errors.get(i), "Error", ks.get(i));
        }
        return dataset;
    }

    // Method to create the dataset for the second chart (nodeCounts vs times)
    public static DefaultCategoryDataset createTimesDataset( ArrayList<Integer> times, ArrayList<Integer> nodeCounts) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < nodeCounts.size(); i++) {
            dataset.addValue(times.get(i), "Times", nodeCounts.get(i));
        }
        return dataset;
    }
    public static DefaultCategoryDataset createTimesDataset2( ArrayList<Integer> times, ArrayList<Double> ks) {
//        ArrayList<Integer> times = new ArrayList<>(); // Replace with actual data
//        ArrayList<Integer> nodeCounts = new ArrayList<>(); // Replace with actual data
//

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < ks.size(); i++) {
            dataset.addValue(times.get(i), "Times", ks.get(i));
        }
        return dataset;
    }

    // Method to create a line chart
    private JFreeChart createLineChart(String title, String xAxisLabel, String yAxisLabel, DefaultCategoryDataset dataset) {
        return ChartFactory.createLineChart(
                title,       // chart title
                xAxisLabel,  // x axis label
                yAxisLabel,  // y axis label
                dataset,     // data
                PlotOrientation.VERTICAL,
                true,        // include legend
                true,        // tooltips
                false        // urls
        );
    }
    // Method to create a line chart with log scale on the y-axis
    private JFreeChart createLineChartWithLogScale(String title, String xAxisLabel, String yAxisLabel, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                title,       // chart title
                xAxisLabel,  // x axis label
                yAxisLabel,  // y axis label
                dataset,     // data
                PlotOrientation.VERTICAL,
                true,        // include legend
                true,        // tooltips
                false        // urls
        );

        // Set log scale for Y axis
        CategoryPlot plot = chart.getCategoryPlot();
        LogarithmicAxis logAxis = new LogarithmicAxis(yAxisLabel);
        plot.setRangeAxis(logAxis);

        return chart;
    }

    public static void picture1(ArrayList<Integer> times, ArrayList<Integer> nodeCounts, ArrayList<Double> errors){
        GossipChart chart = new GossipChart("Gossip Algorithm Charts", times, nodeCounts, errors, true);
        chart.pack();
        chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chart.setVisible(true);
    }

    public static void picture2(ArrayList<Integer> times, ArrayList<Double> ks, ArrayList<Double> errors){
        GossipChart chart = new GossipChart("Gossip Algorithm Charts", times, ks, errors);
        chart.pack();
        chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chart.setVisible(true);
    }
}
