<%-- 
    Document   : TwitterPieChart
    Created on : 04 Oct 2016, 9:46:21 PM
    Author     : kryli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>

        <script type="text/javascript">
            function loadTwitterPieChart() {
                $(function () {
                    $('#twitterPieChartContainer').highcharts({
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
                                ['Neutral Posts', ${neutralTweetPercentage}],
                                {
                                    name: 'Negative Posts',
                                    y: ${negativeTweetPercentage},
                                    sliced: true,
                                    selected: true
                                },
                                ['Positive Posts', ${positiveTweetPercentage}],
                            ]
                        }]
                    });
                });
            }
        </script>
    </head>
    <body>
        <div id="twitterPieChartContainer"></div>
    </body>
</html>
