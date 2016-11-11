<%--
  Created by IntelliJ IDEA.
  User: kryli
  Date: 2016/10/19
  Time: 7:44 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <script type="text/javascript">
        function showOrHideDataButton() {
            if (isAdmin === 'false') {
                document.getElementById("btnData").className = "btn btn-default disabled";
            }
        }
    </script>

    <nav class="navbar navbar-inverse">
        <div class="container-fluid" style="margin-left: 100px; margin-right: 100px">
            <div class="navbar-header">
                <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target="#navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <div class="btn-group">
                    <a id="btnDashboard" class="navbar-brand" href="javascript:"
                       onclick="displaySummary()">
                        Dashboard
                    </a>
                    <a id="btnDashboardDropdown" class="navbar-brand dropdown-toggle" style="font-size: x-large"
                       href="javascript:"
                       data-toggle="dropdown"><span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a href="javascript:" onclick="displaySummary()">
                                Summary
                            </a>
                        </li>
                        <li>
                            <a href="javascript:" onclick="displayFacebook()">
                                Facebook
                            </a>
                        </li>
                        <li>
                            <a href="javascript:" onclick="displayTwitter()">
                                Twitter
                            </a>
                        </li>
                        <li>
                            <a href="javascript:" onclick="displaySocialGraph()">
                                Social Graph
                            </a>
                        </li>
                    </ul>
                </div>
            </div>

            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li style="font-size: large">
                        <a id="btnData" href="javascript:" data-toggle="tab"
                           aria-expanded="false" onclick="displayData()">
                            Data
                        </a>
                    </li>

                    <li class="dropdown" style="font-size: large">
                        <a id="selectedCompany" href="javascript:"
                           data-toggle="dropdown"
                           data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Changing company"
                           aria-expanded="false">
                            ${currentCompany}
                            <span class='caret'></span>
                        </a>
                        <ul id="companyNamesDropDown" class="dropdown-menu">
                            <c:forEach var="companyName" items="${userCompanyList}">
                                <li>
                                    <a href="javascript:"
                                       onclick="changeSelectedCompany('${companyName}')">
                                            ${companyName}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </li>

                </ul>

                <ul class="nav navbar-nav navbar-right">
                    <li style="font-size: large">
                        <a href="javascript:" data-toggle="tab"
                           aria-expanded="false" onclick="displaySettings()">
                            <span class="glyphicon glyphicon-cog"></span>
                        </a>
                    </li>

                    <li class="dropdown" style="font-size: large">
                        <a class="dropdown-toggle" href="javascript:"
                           data-toggle="dropdown"
                           aria-expanded="false">
                            Welcome ${username}
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a href="javascript:" onclick="signOut()">Sign out</a></li>
                        </ul>
                    </li>

                </ul>
            </div>
        </div>
    </nav>
</head>
<body>

</body>
</html>
