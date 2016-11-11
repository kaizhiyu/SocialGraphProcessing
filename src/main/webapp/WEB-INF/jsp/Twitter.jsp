<%-- 
    Document   : Twitter
    Created on : 23 Sep 2016, 2:00:54 PM
    Author     : kryli
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <div id="tweetDetails" class="modal">
        <div class="modal-dialog">
            <div class="modal-content" style="padding: 10px">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h3 class="modal-title"><b>Tweet details</b></h3>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="panel panel-info">
                            <div class="panel-body">
                                <ul class="list-group">
                                    <li class="list-group-item">
                                        <p id="tweetText"></p>
                                    </li>

                                    <li class="list-group-item">
                                        <span class="badge">
                                            <label id="tweetLikeCount"></label>
                                        </span>
                                        Number of likes
                                    </li>
                                    <li class="list-group-item">
                                        <span class="badge">
                                            <label id="tweetShareCount"></label>
                                        </span>
                                        Number of retweets
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">
        var twitterHiddenPieChart = false;
        function MinimizeClick_TwitterPieChart() {
            twitterHiddenPieChart = !twitterHiddenPieChart;
            if (twitterHiddenPieChart) {
                $('#twitterPieChartDiv').hide(1000);
                document.getElementById('btnMinimize_TwitterPieChart').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#twitterPieChartDiv').show(1000);
                document.getElementById('btnMinimize_TwitterPieChart').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }

        var twitterHiddenSummaryData = false;
        function MinimizeClick_TwitterStackedChart() {
            twitterHiddenSummaryData = !twitterHiddenSummaryData;
            if (twitterHiddenSummaryData) {
                $('#twitterSummaryData').hide(1000);
                document.getElementById('btnMinimize_TwitterSummaryData').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#twitterSummaryData').show(1000);
                document.getElementById('btnMinimize_TwitterSummaryData').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }

        function tweetSelected(tweetText, id) {
            document.getElementById('tweetText').innerText = tweetText;
            var tweetText = {
                "tweetText": tweetText,
                "id": id
            };
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: 'getTweetShares',
                data: JSON.stringify(tweetText),
                success: function (data) {
                    document.getElementById('tweetShareCount').innerHTML = data;
                },
                error: function (e) {
                    console.log("error 12:" + e);
                }
            });
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: 'getTweetLikes',
                data: JSON.stringify(tweetText),
                success: function (data) {
                    document.getElementById('tweetLikeCount').innerHTML = data;
                },
                error: function (e) {
                    console.log("error selecting tweet:" + e);
                }
            });
        }
    </script>
</head>

<body>
<div class="row">
    <div class="col-md-6">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <button id="btnMinimize_TwitterPieChart" class="btn btn-primary btn-sm"
                            onclick="MinimizeClick_TwitterPieChart()">
                        <span class="glyphicon glyphicon-menu-up"></span>
                    </button>

                    <label>${currentCompany}</label>
                    Overall Twitter impact
                </h3>
            </div>
            <div class="panel-body" id="twitterPieChartDiv">
                <jsp:include page="TwitterPieChart.jsp"></jsp:include>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <button id="btnMinimize_TwitterSummaryData" class="btn btn-primary btn-sm"
                            onclick="MinimizeClick_TwitterStackedChart()">
                        <span class="glyphicon glyphicon-menu-up"></span>
                    </button>

                    <label>${currentCompany}</label>
                    Summarized Twitter data
                </h3>
            </div>
            <div class="panel-body" id="twitterSummaryData">
                <ul class="list-group">
                    <li class="list-group-item">
                        <span class="badge">${tweetCount}</span>
                        Tweet Count
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-4">
        <div class="panel panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="glyphicon glyphicon-plus"></span>
                    <label>${positiveTweetCount}</label>
                    Positive Tweets
                </h3>
            </div>
            <div class="panel-body">
                <table class="table table-hover">
                    <tbody>
                    <c:forEach var="positiveTweet" items="${positiveTweets}">
                        <tr>
                            <td style="word-wrap: break-word">
                                <a href="javascript:" data-toggle="modal" data-target="#tweetDetails"
                                   onclick="tweetSelected('${positiveTweet}', 0)">
                                        ${positiveTweet}
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="panel panel-danger">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="glyphicon glyphicon-minus"></span>
                    <label>${negativeTweetCount}</label>
                    Negative Tweets
                </h3>
            </div>
            <div class="panel-body">
                <table class="table table-hover">
                    <tbody>
                    <c:forEach var="negativeTweet" items="${negativeTweets}">
                        <tr>
                            <td style="word-wrap: break-word">
                                <a href="javascript:" data-toggle="modal" data-target="#tweetDetails"
                                   onclick="tweetSelected('${negativeTweet}', 1)">
                                        ${negativeTweet}
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="glyphicon glyphicon-tag"></span>
                    Top Tweets
                </h3>
            </div>
            <div class="panel-body">
                <table class="table table-hover">
                    <tbody>
                    <c:forEach var="topTweet" items="${topTweets}">
                        <tr>
                            <td style="word-wrap: break-word">
                                <a href="javascript:" data-toggle="modal" data-target="#tweetDetails"
                                   onclick="tweetSelected('${topTweet}', 2)">
                                        ${topTweet}
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
