<%--
  Created by IntelliJ IDEA.
  User: kryli
  Date: 2016/10/24
  Time: 4:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <script type="text/javascript">
        function loginFailed() {
            $('#loginProcessing').hide();
            $('#loginError').fadeIn(1000);
            $('#loginError').fadeOut(10000).delay(2000);

            document.getElementById("loginUsernameDiv").className = "row form-group has-error";
            document.getElementById("loginPasswordDiv").className = "row form-group has-error";
            $("#txtLoginUsername").prop('disabled', false);
            $("#txtLoginPassword").prop('disabled', false);
            $('#btnLogin').button('reset');
        }

        function processingLogin() {
            $('#loginProcessing').fadeIn(1000);

            $("#txtLoginUsername").prop('disabled', true);
            $("#txtLoginPassword").prop('disabled', true);
        }

        function registrationSuccess() {
            $('#registrationSuccess').fadeIn(1000);
            $('#registrationSuccess').fadeOut(10000).delay(2000);

            document.getElementById("loginUsernameDiv").className = "row form-group";
            document.getElementById("loginPasswordDiv").className = "row form-group";

            $("#txtLoginUsername").prop('disabled', false);
            $("#txtLoginPassword").prop('disabled', false);
        }
    </script>
</head>

<body>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <h2 class="modal-title">
                Login
            </h2>
        </div>
        <div class="modal-body form-horizontal">

            <div id="loginError" class="alert alert-dismissible alert-danger" hidden="true">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Error!</strong> Login failed. Please ensure your username and password are correct.
            </div>

            <div id="loginProcessing" class="alert alert-dismissible alert-info" hidden="true">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Processing login.</strong> Please wait while we fetch your personal data.
            </div>

            <div id="registrationSuccess" class="alert alert-dismissible alert-success" hidden="true">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Registration successful!</strong> You may now log in using your login credentials.
            </div>

            <div style="padding: 100px">

                <div id="loginUsernameDiv" class="row form-group">
                    <div class="input-group">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-user"></span>
                                </span>
                        <input id="txtLoginUsername" class="form-control" type="text" placeholder="Username"/>
                    </div>
                </div>

                <div id="loginPasswordDiv" class="row form-group">
                    <div class="input-group">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-lock"></span>
                                </span>
                        <input id="txtLoginPassword" class="form-control" type="password"
                               placeholder="Password"/>
                    </div>
                </div>

                <div class="row form-group">
                    <button id="btnLogin" class="btn btn-primary btn-block"
                            data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing login"
                            type="button" onclick="login()">
                        Login
                    </button>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-default" type="button" data-dismiss="modal" onclick="displayRegister()">
                Register
            </button>
        </div>
    </div>
</body>
</html>
