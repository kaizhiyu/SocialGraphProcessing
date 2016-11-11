<%-- 
    Document   : Summar
    Created on : 23 Sep 2016, 1:58:43 PM
    Author     : kryli
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>

    <script type="text/javascript">
        var hiddenChart = false;
        function MinimizeClick_Chart() {
            hiddenChart = !hiddenChart;
            if (hiddenChart) {
                $('#pieChart').hide(1000);
                document.getElementById('btnMinimize_Chart').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#pieChart').show(1000);
                document.getElementById('btnMinimize_Chart').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }

        var hiddenSummaryData = false;
        function MinimizeClick_SummaryData() {
            hiddenSummaryData = !hiddenSummaryData;
            if (hiddenSummaryData) {
                $('#summaryBody').hide(1000);
                document.getElementById('btnMinimize_SummaryData').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#summaryBody').show(1000);
                document.getElementById('btnMinimize_SummaryData').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }
    </script>

</head>

<body>
<div class="row">
    <div class="col-md-6">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <button id="btnMinimize_Chart" class="btn btn-primary btn-sm" onclick="MinimizeClick_Chart()">
                        <span class="glyphicon glyphicon-menu-up"></span>
                    </button>

                    <label>${currentCompany}</label>
                    Social Media Impact
                </h3>
            </div>
            <div id="pieChart" class="panel-body">
                <jsp:include page="SummaryPieChart.jsp"></jsp:include>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <button id="btnMinimize_SummaryData" class="btn btn-info btn-sm"
                            onclick="MinimizeClick_SummaryData()">
                        <span class="glyphicon glyphicon-menu-up"></span>
                    </button>

                    Posts and Tweets
                </h3>
            </div>
            <div id="summaryBody" class="panel-body">
                There are <label>${totalNumberOfPostsAndTweets}</label> Facebook posts and Tweets.
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="panel panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">
                            <span class="glyphicon glyphicon-plus">
                            </span>
                    <label>${totalPositivePostsAndTweets}</label>
                    Positive Posts
                </h3>
            </div>
            <div class="panel-body">
                <ul class="list-group">
                    <c:forEach var="positivePostOrTweet" items="${positivePostsAndTweets}">
                        <li class="list-group-item">
                                ${positivePostOrTweet}
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="panel panel-danger">
            <div class="panel-heading">
                <h3 class="panel-title">
                            <span class="glyphicon glyphicon-minus">
                            </span>
                    <label>${totalNegativePostsAndTweets}</label>
                    Negative Posts
                </h3>
            </div>
            <div class="panel-body">
                <ul class="list-group">
                    <c:forEach var="negativePostOrTweet" items="${negativePostsAndTweets}">
                        <li class="list-group-item">
                                ${negativePostOrTweet}
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>
