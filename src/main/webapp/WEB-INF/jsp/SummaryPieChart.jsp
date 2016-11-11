<%-- 
    Document   : SummaryPieChart
    Created on : 04 Oct 2016, 9:32:11 AM
    Author     : kryli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <script src="https://code.highcharts.com/highcharts.js"></script>
    <script src="https://code.highcharts.com/modules/exporting.js"></script>

    <script type="text/javascript">
        function loadSummaryPieChart() {
            $(function () {
                // Radialize the colors
                Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function (color) {
                    return {
                        radialGradient: {
                            cx: 0.5,
                            cy: 0.3,
                            r: 0.7
                        },
                        stops: [
                            [0, color],
                            [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
                        ]
                    };
                });

                $('#SummaryPieChartContainer').highcharts({
                    chart: {
                        plotBackgroundColor: null,
                        plotBorderWidth: null,
                        plotShadow: false,
                        type: 'pie'
                    },
                    title: {
                        text: ''
                    },
                    tooltip: {
                        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: {
                                enabled: false
                            },
                            showInLegend: true
                        }
                    },
                    series: [{
                        type: 'pie',
                        name: 'Posts',
                        data: [
                            ['Neutral Posts', ${totalNeutralPostsAndTweetsPercentage}],
                            {
                                name: 'Negative Posts',
                                y: ${totalNegativePostsAndTweetsPercentage},
                                sliced: true,
                                selected: true
                            },
                            ['Positive Posts', ${totalPositivePostsAndTweetsPercentage}],
                        ]
                    }]
                });
            });
        }
    </script>
</head>
<body>
<div id="SummaryPieChartContainer"></div>
</body>
</html>
