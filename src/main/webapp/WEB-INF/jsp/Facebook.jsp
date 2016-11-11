<%-- 
    Document   : Facebook
    Created on : 23 Sep 2016, 2:00:29 PM
    Author     : kryli
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <div id="facebookPostDetails" class="modal">
        <div class="modal-dialog">
            <div class="modal-content" style="padding: 10px">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h3 class="modal-title"><b>Facebook post details</b></h3>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <h3 class="panel-title">
                                    <span class="glyphicon glyphicon-comment"></span>
                                    Post text -
                                    <label id="facebookPostDetailsShareCount"></label>
                                    Shares
                                </h3>
                            </div>
                            <div id="facebookPostDetailsPostText" class="panel-body">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title">
                                    <span class="glyphicon glyphicon-comment"></span>
                                    <label id="facebookPostDetailsCommentCount"></label>
                                    Post comments
                                </h3>
                            </div>
                            <div class="panel-body">
                                <ul id="facebookPostDetailsCommentList" class="list-group">
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
        var facebookHiddenPieChart = false;
        function MinimizeClick_FacebookPieChart() {
            facebookHiddenPieChart = !facebookHiddenPieChart;
            if (facebookHiddenPieChart) {
                $('#facebookPieChartDiv').hide(1000);
                document.getElementById('btnMinimize_FacebookPieChart').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#facebookPieChartDiv').show(1000);
                document.getElementById('btnMinimize_FacebookPieChart').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }

        var facebookHiddenSummaryData = false;
        function MinimizeClick_FacebookSummaryData() {
            facebookHiddenSummaryData = !facebookHiddenSummaryData;
            if (facebookHiddenSummaryData) {
                $('#facebookSummaryDiv').hide(1000);
                document.getElementById('btnMinimize_FacebookSummaryData').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#facebookSummaryDiv').show(1000);
                document.getElementById('btnMinimize_FacebookSummaryData').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }

        function facebookPostSelected(facebookPostText, id) {

            document.getElementById('facebookPostDetailsPostText').innerText = facebookPostText;
            var facebookPostText = {
                "facebookPostText": facebookPostText,
                "id": id
            };
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: 'getFacebookPostShares',
                data: JSON.stringify(facebookPostText),
                success: function (data) {
                    document.getElementById('facebookPostDetailsShareCount').innerHTML = data;
                },
                error: function (e) {
                    console.log("error 10: " + e);
                }
            });
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: 'getFacebookPostComments',
                data: JSON.stringify(facebookPostText),
                success: function (data) {

                    document.getElementById('facebookPostDetailsCommentCount').innerHTML = data.length;

                    document.getElementById('facebookPostDetailsCommentList').innerHTML = "";
                    for (var i = 0; i < data.length; i++) {
                        $("#facebookPostDetailsCommentList").append(
                                "<li class='list-group-item'>" +
                                data[i] +
                                "</li>");
                    }

                },
                error: function (e) {
                    console.log("error selecting facebook post:" + e);
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
                    <button id="btnMinimize_FacebookPieChart" class="btn btn-primary btn-sm"
                            onclick="MinimizeClick_FacebookPieChart()">
                        <span class="glyphicon glyphicon-menu-up"></span>
                    </button>

                    <label>${currentCompany}</label>
                    Overall Facebook impact
                </h3>
            </div>
            <div id="facebookPieChartDiv" class="panel-body">
                <jsp:include page="FacebookPieChart.jsp"></jsp:include>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <button id="btnMinimize_FacebookSummaryData" class="btn btn-primary btn-sm"
                            onclick="MinimizeClick_FacebookSummaryData()">
                        <span class="glyphicon glyphicon-menu-up"></span>
                    </button>

                    <label>${currentCompany}</label>
                    Summarized Facebook data
                </h3>
            </div>
            <div id="facebookSummaryDiv" class="panel-body">
                <ul class="list-group">
                    <li class="list-group-item">
                        <span class="badge">${facebookPostCount}</span>
                        Total post count
                    </li>

                    <li class="list-group-item">
                        <span class="badge">${facebookFeedPostCount}</span>
                        Total feed post count
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="panel panel-success">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="glyphicon glyphicon-plus"></span>
                    <label>${facebookPositivePostCount}</label>
                    Positive Posts and Feed Posts
                </h3>
            </div>
            <div class="panel-body">
                <table class="table table-hover">
                    <tbody>
                    <c:forEach var="positivePost" items="${positiveFacebookPostsAndFeedPosts}">
                        <tr>
                            <td style="word-wrap: break-word">
                                <a href="javascript:" data-toggle="modal" data-target="#facebookPostDetails"
                                   onclick="facebookPostSelected('${positivePost}', 0)">
                                        ${positivePost}
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="panel panel-danger">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="glyphicon glyphicon-minus"></span>
                    <label>${facebookNegativePostCount}</label>
                    Negative Posts and Feed Posts
                </h3>
            </div>
            <div class="panel-body form-group">
                <table class="table table-hover">
                    <tbody>
                    <c:forEach var="negativePost" items="${negativeFacebookPostsAndFeedPosts}">
                        <tr>
                            <td style="word-wrap: break-word">
                                <a href="javascript:" data-toggle="modal" data-target="#facebookPostDetails"
                                   onclick="facebookPostSelected('${negativePost}', 1)">
                                        ${negativePost}
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

<div class="row">
    <div class="col-md-6">
        <div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="glyphicon glyphicon-tag"></span>
                    Top Posts
                </h3>
            </div>
            <div class="panel-body form-group">
                <table class="table table-hover">
                    <tbody>
                    <c:forEach var="topPost" items="${topFacebookPosts}">
                        <tr>
                            <td style="word-wrap: break-word">
                                <a href="javascript:" data-toggle="modal" data-target="#facebookPostDetails"
                                   onclick="facebookPostSelected('${topPost}', 2)">
                                        ${topPost}
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="glyphicon glyphicon-tags"></span>
                    Top Feed Posts
                </h3>
            </div>
            <div class="panel-body form-group">
                <table class="table table-hover">
                    <tbody>
                    <c:forEach var="topFeedPost" items="${topFacebookFeedPosts}">
                        <tr>
                            <td style="word-wrap: break-word">
                                <a href="javascript:" data-toggle="modal" data-target="#facebookPostDetails"
                                   onclick="facebookPostSelected('${topFeedPost}', 3)">
                                        ${topFeedPost}
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
