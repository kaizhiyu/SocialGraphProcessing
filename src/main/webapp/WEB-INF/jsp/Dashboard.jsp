<%-- 
    Document   : home
    Created on : 23 Sep 2016, 9:26:12 AM
    Author     : kryli
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <h3>Dashboard</h3>
</head>

<body>
<div class="jumbotron" style="padding: 10px">
    <ul id="dashboardTabs" class="nav nav-tabs">
        <li style="width: 125px; font-size: x-large" class="active">
            <a href="#summary" data-toggle="tab" aria-expanded="true" onclick="displaySummary()">
                Summary
            </a>
        </li>
        <li style="width: 125px; font-size: x-large">
            <a href="#facebook" data-toggle="tab" aria-expanded="false" onclick="displayFacebook()">
                Facebook
            </a>
        </li>
        <li style="width: 100px; font-size: x-large">
            <a href="#twitter" data-toggle="tab" aria-expanded="false" onclick="displayTwitter()">
                Twitter
            </a>
        </li>

        <li style="width: 175px; font-size: x-large">
            <a href="#socialGraph" data-toggle="tab" aria-expanded="false" onclick="displaySocialGraph()">
                Social Graph
            </a>
        </li>
    </ul>

    <div class="tab-content" style="padding: 10px">
        <div id="summary" class="tab-pane fade in active">
            <jsp:include page="Summary.jsp"></jsp:include>
        </div>

        <div id="facebook" class="tab-pane fade active">
            <jsp:include page="Facebook.jsp"></jsp:include>
        </div>

        <div id="twitter" class="tab-pane fade active">
            <jsp:include page="Twitter.jsp"></jsp:include>
        </div>

        <div id="socialGraph" class="tab-pane fade active">
            <jsp:include page="SocialGraph.jsp"></jsp:include>
        </div>
    </div>
</div>
</body>
</html>
