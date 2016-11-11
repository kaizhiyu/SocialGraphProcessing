<%-- 
    Document   : home
    Created on : 23 Sep 2016, 9:26:12 AM
    Author     : kryli
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Social Media Graph Processor</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/bootstrap/css/bootstrap.min.css" />"/>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/favicon.png" />"/>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <script type="text/javascript">

        var popupShown = 0;
        var isAdmin = '${isAdmin}';

        function displayDashboard() {
            $('#dashboard').fadeIn(500);
            $('#data').hide();
            $('#settings').hide();
            $('#about').hide();
        }

        function displayData() {
            $('#dashboard').hide();
            $('#data').fadeIn(500);
            $('#settings').hide();
            $('#about').hide();

            changeURL("/data");
        }

        function displaySettings() {
            $('#dashboard').hide();
            $('#data').hide();
            $('#settings').fadeIn(500);
            $('#about').hide();

            changeURL("/settings");
        }

        function displaySummary() {
            displayDashboard();
            $('#dashboardTabs a[href="#summary"]').tab('show');

            changeURL("");
        }

        function displayFacebook() {
            displayDashboard();
            $('#dashboardTabs a[href="#facebook"]').tab('show');

            changeURL("/facebook");
        }

        function displayTwitter() {
            displayDashboard();
            $('#dashboardTabs a[href="#twitter"]').tab('show');

            changeURL("/twitter");
        }

        function displaySocialGraph() {
            displayDashboard();
            $('#dashboardTabs a[href="#socialGraph"]').tab('show');

            changeURL("/socialgraph");
        }

        function displayLogin(redirect) {
            popupShown = 0;
            $('#loginModal').modal({
                        backdrop: 'static',
                        keyboard: false
                    }
            );

            //display successful registration if redirecting from registration popup
            if (redirect == 1) {
                registrationSuccess();
            }
        }

        function displayRegister() {
            popupShown = 1;
            $('#registerModal').modal({
                        backdrop: 'static',
                        keyboard: false
                    }
            );
        }

        function changeSettingsModalCompanyName(companyName) {
            document.getElementById('removeCompanyName_Header').innerHTML = companyName;
            document.getElementById('removeCompanyName_Body').innerHTML = companyName;
            document.getElementById('editCompanyName_Header').innerHTML = companyName;
            document.getElementById('txtEditCompany').value = companyName;
        }

        function changeSelectedCompany(selectedCompany) {
            $('#selectedCompany').button('loading');

            var companyModel = {"companyName": selectedCompany};
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '${home}/changeCompany',
                data: JSON.stringify(companyModel),
                success: function (data) {
                    location.reload();
                },
                error: function (e) {
                    $('#selectedCompany').button('reset');
                    console.log("error changing company:" + e);
                }
            });
        }

        function mineData() {
            document.getElementById('miningPlatform').innerText = 'Facebook and Twitter';

            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '${home}/dataMine',
                success: function (data) {
                    $("#miningData").modal('hide');
                    dataMineSuccess();
                },
                error: function (e) {
                    $("#miningData").modal('hide');
                    dataMineError();
                    console.log("error mining all data:" + e);
                }
            });
        }

        function mineFacebook() {
            document.getElementById('miningPlatform').innerText = 'Facebook';

            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '${home}/facebookMine',
                success: function (data) {
                    $("#miningData").modal('hide');
                    dataMineSuccess();
                },
                error: function (e) {
                    $("#miningData").modal('hide');
                    dataMineError();
                    console.log("error mining facebook:" + e);
                }
            });
        }

        function mineTwitter() {
            document.getElementById('miningPlatform').innerText = 'Twitter';

            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '${home}/twitterMine',
                success: function (data) {
                    $("#miningData").modal('hide');
                    dataMineSuccess();
                },
                error: function (e) {
                    $("#miningData").modal('hide');
                    dataMineError();
                    console.log("error mining twitting:" + e);
                }
            });
        }

        function removeCompany() {
            var companyModel = {"companyName": document.getElementById('removeCompanyName_Header').innerText};
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '${home}/removeCompany',
                data: JSON.stringify(companyModel),
                success: function (data) {
                    removeCompanySuccess();
                    setTimeout(function () {
                        location.reload();
                    }, 5000);
                },
                error: function (e) {
                    removeCompanyError();
                    console.log("error removing company:" + e);
                }
            });
        }

        function addCompany() {
            $('#btnAddCompany').button('loading');

            var companyName = document.getElementById('txtNewCompany').value;

            if (companyName) {

                var companyModel = {"companyName": companyName};
                document.getElementById('txtNewCompany').value = '';
                $.ajax({
                    type: 'POST',
                    contentType: 'application/json',
                    url: '${home}/addCompany',
                    data: JSON.stringify(companyModel),
                    success: function (data) {
                        $('#btnAddCompany').button('reset');
                        addCompanySuccess();
                        setTimeout(function () {
                            location.reload();
                        }, 5000);
                    },
                    error: function (e) {
                        $('#btnAddCompany').button('reset');
                        addCompanyError();
                        console.log("error adding:" + e);
                    }
                });
            } else {
                $('#btnAddCompany').button('reset');
                blankCompanyInputError();
            }
        }

        function login() {
            var username = document.getElementById('txtLoginUsername').value;
            var password = document.getElementById('txtLoginPassword').value;

            if (username && password) {
                $('#btnLogin').button('loading');

                var loginUser = {
                    "username": username,
                    "password": password
                };
                document.getElementById('txtLoginUsername').value = '';
                document.getElementById('txtLoginPassword').value = '';
                processingLogin();

                $.ajax({
                    type: 'POST',
                    contentType: 'application/json',
                    url: '${home}/login',
                    data: JSON.stringify(loginUser),
                    success: function (data) {
                        $('#loginModal').modal('hide');
                        $('#btnLogin').button('reset');
                        location.reload();
                        popupShown = -1;
                    },
                    error: function (e) {
                        loginFailed();
                        console.log("error logging in:" + e);
                    }
                });

            } else {
                loginFailed();
            }
        }

        function register() {
            var username = document.getElementById('txtRegisterUsername').value;
            var password = document.getElementById('txtRegisterPassword1').value;
            var password2 = document.getElementById('txtRegisterPassword2').value;
            var firstname = document.getElementById('txtRegisterFirstname').value;
            var surname = document.getElementById('txtRegisterSurname').value;
            var companies = [];
            <c:forEach var="companyName" items="${companies}">
            if (document.getElementById("cb + ${companyName}").checked) {
                companies.push('${companyName}');
            }
            </c:forEach>

            if (username && password && firstname && surname) {

                if (password == password2) {

                    if (companies.length > 0) {
                        $('#btnRegister').button('loading');

                        var registerUser = {
                            "username": username,
                            "password": password,
                            "firstname": firstname,
                            "surname": surname,
                            "companyList": companies
                        };
                        document.getElementById('txtRegisterUsername').value = '';
                        document.getElementById('txtRegisterPassword1').value = '';
                        document.getElementById('txtRegisterPassword2').value = '';
                        document.getElementById('txtRegisterFirstname').value = '';
                        document.getElementById('txtRegisterSurname').value = '';

                        $.ajax({
                            type: 'POST',
                            contentType: 'application/json',
                            url: '${home}/register',
                            data: JSON.stringify(registerUser),
                            success: function (data) {
                                if (data) {
                                    $('#registerModal').modal('hide');
                                    $('#btnRegister').button('reset');
                                    displayLogin(1);
                                } else {
                                    failedRegister(0);
                                }
                            },
                            error: function (e) {
                                failedRegister(0);
                                console.log("error registering:" + e);
                            }
                        });
                    } else {
                        failedRegister(2);
                    }
                }
                else {
                    failedRegister(1);
                }

            } else {
                failedRegister(0);
            }
        }

        function signOut() {
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '${home}/signout',
                success: function (data) {
                    location.reload();
                    $("#txtLoginUsername").prop('disabled', false);
                    $("#txtLoginPassword").prop('disabled', false);
                },
                error: function (e) {
                    console.log("error signing out:" + e);
                }
            });
        }

        function changeURL(page) {
            var URL = window.location.href;
            var indexOfHome = URL.indexOf("${home}");
            var lastIndexOfSlash = URL.lastIndexOf("/");
            if (lastIndexOfSlash >= indexOfHome)
                URL = URL.substr(0, lastIndexOfSlash);
            if (URL.indexOf(page) === -1)
                window.history.pushState("", "", URL + page);
            else
                window.history.pushState("", "", URL);
        }

        function checkURL() {
            var URL = window.location.href;
            var lastIndexOfSlash = URL.lastIndexOf("/");
            var requestedPage = URL.substr(lastIndexOfSlash).toLowerCase();

            if (requestedPage == "/facebook") {
                displayFacebook();
            } else if (requestedPage == "/twitter") {
                displayTwitter();
            } else if (requestedPage == '/socialgraph') {
                displaySocialGraph();
            } else if (requestedPage == "/data") {
                if (isAdmin === 'true') {
                    displayData();
                } else {
                    displaySummary();
                }
            } else if (requestedPage == "/settings") {
                displaySettings();
            } else {
                displaySummary();
            }
        }

        function checkSessionID() {
            var sessionID = '${sessionID}';

            if (!sessionID) {
                displayLogin(0);
            } else {
                //Load graphs
                loadSummaryPieChart();
                loadFacebookPieChart();
                loadTwitterPieChart();
                loadSocialGraph();
            }
        }

        function checkIfUserIsAdmin() {
            showOrHideDataButton();
        }

        window.onload = function () {
            //Establish login
            checkSessionID();

            //URL listener
            checkURL();

            //Check if a user is admin and apply rules appropriately
            checkIfUserIsAdmin();

            //Load tooltips
            $('[data-toggle="tooltip"]').tooltip();
        };

        window.onkeyup = function (e) {
            if (popupShown == 0 || popupShown == 1) {
                var key = e.keyCode ? e.keyCode : e.which;

                if (key == 13 && popupShown == 0) {
                    login();
                } else if (key == 13 && popupShown == 1) {
                    register();
                }
            }
        }
    </script>

    <div id="registerModal" class="modal fade">
        <jsp:include page="Register.jsp"></jsp:include>
    </div>

    <div id="loginModal" class="modal fade">
        <jsp:include page="Login.jsp"></jsp:include>
    </div>
    </div>

    <jsp:include page="Navbar.jsp"></jsp:include>
</head>

<body onresize="loadSocialGraph()">
<div id="bodyContent" style="margin-left: 100px; margin-right: 100px">
    <div class="tab-content">
        <div id="dashboard" class="tab-pane active in">
            <jsp:include page="Dashboard.jsp"></jsp:include>
        </div>

        <div id="data" class="tab-pane">
            <jsp:include page="Data.jsp"></jsp:include>
        </div>

        <div id="settings" class="tab-pane">
            <jsp:include page="Settings.jsp"></jsp:include>
        </div>
    </div>
</div>
</body>

<footer>
    <div style="margin-left: 100px; margin-right: 100px">
        <p>&copy; 2016 - Social Media Graph Processor | Simon Headley, Stuart Crichton, Dawood Kamdar</p>
    </div>
</footer>
</html>
