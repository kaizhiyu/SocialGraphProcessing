<%-- 
Document   : Data
Created on : 23 Sep 2016, 12:34:27 PM
Author     : kryli
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <div id="successfulDataMine" class="alert alert-dismissible alert-success" hidden="true" style="font-size: medium">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Success!</strong> Completed data mining. <a href="javascript:" onclick="location.reload();">Click here to refresh.</a>
    </div>

    <div id="unsuccessfulDataMine" class="alert alert-dismissible alert-danger" hidden="true" style="font-size: medium">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Error!</strong> Data mining failed.
    </div>

    <div id="miningData" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">
                        <span class="glyphicon glyphicon-exclamation-sign"></span>
                        <label id="miningPlatform"></label> Data Mining
                    </h4>
                </div>
                <div class="modal-body">
                    <strong>Data mining</strong> in progress. Website functionality has been temporarily disabled.
                    <br>
                    Please be patient.

                    <br><br>
                    <p style="font-size: x-small">This notification will automatically clear upon completion</p>
                </div>
            </div>
        </div>
    </div>

    <h3>Data</h3>

    <script type="text/javascript">
        function dataMineSuccess() {
            $('#successfulDataMine').fadeIn(1000);
        }

        function dataMineError() {
            $('#unsuccessfulDataMine').fadeIn(1000);
        }
    </script>
</head>

<body>
<div class="jumbotron" style="padding: 10px">
    <div class="panel panel-primary">
        <div class="panel-heading" style="font-size: large">
            Current
            <label>${currentCompany}</label>
            Data
        </div>
        <div class="panel-body">
            <ul class="list-group">
                <li class="list-group-item">
                    <span class="badge">${facebookPostCount}</span>
                    Current Facebook Post Count
                </li>
                <li class="list-group-item">
                    <span class="badge">${facebookFeedPostCount}</span>
                    Current Facebook Feed Count
                </li>
                <li class="list-group-item">
                    <span class="badge">${tweetCount}</span>
                    Current Tweet Count
                </li>
                <li class="list-group-item">
                    <span class="badge">${lastDateMined}</span>
                    Last Updated
                </li>
            </ul>
        </div>
    </div>
    <input class="btn btn-primary" type="button" value="Mine Facebook" data-toggle="modal" data-backdrop="static"
           data-keyboard="false" data-target="#miningData" onclick="mineFacebook()"/>
    <input class="btn btn-primary" type="button" value="Mine Twitter" data-toggle="modal" data-backdrop="static"
           data-keyboard="false" data-target="#miningData" onclick="mineTwitter()"/>
    <input class="btn btn-primary" type="button" value="Mine Facebook and Twitter" data-toggle="modal" data-backdrop="static"
           data-keyboard="false" data-target="#miningData" onclick="mineData()"/>
</div>
</body>
</html>
