<%--
  Created by IntelliJ IDEA.
  User: kryli
  Date: 2016/10/24
  Time: 5:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <script type="text/javascript">
        function failedRegister(reason) {
            $('#btnRegister').button('reset');

            if (reason == 0) {
                $('#registerError').fadeIn(1000);
                $('#registerError').fadeOut(10000).delay(2000);

                document.getElementById("registerUsernameDiv").className = "row form-group has-error";
                document.getElementById("registerPasswordDiv1").className = "row form-group has-error";
                document.getElementById("registerPasswordDiv2").className = "row form-group has-error";
                document.getElementById("registerFirstnameDiv").className = "row form-group has-error";
                document.getElementById("registerSurnameDiv").className = "row form-group has-error";
                document.getElementById("registerCompanyListDiv").className = "row form-group has-error";
            } else if (reason == 1) {
                $('#passwordRegisterError').fadeIn(1000);
                $('#passwordRegisterError').fadeOut(10000).delay(2000);

                document.getElementById("registerUsernameDiv").className = "row form-group";
                document.getElementById("registerPasswordDiv1").className = "row form-group has-error";
                document.getElementById("registerPasswordDiv2").className = "row form-group has-error";
                document.getElementById("registerFirstnameDiv").className = "row form-group";
                document.getElementById("registerSurnameDiv").className = "row form-group";
                document.getElementById("registerCompanyListDiv").className = "row form-group";
            } else if (reason == 2) {
                $('#companyRegisterError').fadeIn(1000);
                $('#companyRegisterError').fadeOut(10000).delay(2000);

                document.getElementById("registerUsernameDiv").className = "row form-group";
                document.getElementById("registerPasswordDiv1").className = "row form-group";
                document.getElementById("registerPasswordDiv2").className = "row form-group";
                document.getElementById("registerFirstnameDiv").className = "row form-group";
                document.getElementById("registerSurnameDiv").className = "row form-group";
                document.getElementById("registerCompanyListDiv").className = "row form-group has-error";
            }
        }

        var hiddenRegistrationInputs = false;
        function MinimizeRegistrationDetails() {
            hiddenRegistrationInputs = !hiddenRegistrationInputs;
            if (hiddenRegistrationInputs) {
                $('#registrationBody').hide(1000);
                document.getElementById('btnMinimizeRegistrationDetails').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#registrationBody').show(1000);
                document.getElementById('btnMinimizeRegistrationDetails').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }
    </script>
</head>

<body>
<div class="modal-dialog" style="overflow-y: scroll; max-height:85%">
    <div class="modal-content">
        <div class="modal-header">
            <h2 class="modal-title">Register</h2>
        </div>
        <div class="modal-body form-horizontal">

            <div id="registerError" class="alert alert-dismissible alert-danger" hidden="true">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Error!</strong> Registration failed. Please ensure all input fields are correctly.
                <p style="font-size: x-small">User account may exist already. Have you tried logging in?</p>
            </div>

            <div id="passwordRegisterError" class="alert alert-dismissible alert-danger" hidden="true">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Error!</strong> Registration failed. Passwords do not match.
            </div>

            <div id="companyRegisterError" class="alert alert-dismissible alert-danger" hidden="true">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Error!</strong> Registration failed. You must be linked to at least one company.
            </div>

            <div style="padding-left: 100px; padding-right: 100px; padding-bottom: 20px; padding-top: 20px">

                <div id="registerUsernameDiv" class="row form-group">
                    <div class="input-group">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-user"></span>
                                </span>
                        <input id="txtRegisterUsername" class="form-control" type="text"
                               placeholder="Username"/>
                    </div>
                </div>

                <div id="registerPasswordDiv1" class="row form-group">
                    <div class="input-group">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-lock"></span>
                                </span>
                        <input id="txtRegisterPassword1" class="form-control" type="password"
                               placeholder="Password"/>
                    </div>
                </div>

                <div id="registerPasswordDiv2" class="row form-group">
                    <div class="input-group">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-lock"></span>
                                </span>
                        <input id="txtRegisterPassword2" class="form-control" type="password"
                               placeholder="Repeat Password"/>
                    </div>
                </div>

                <div id="registerFirstnameDiv" class="row form-group">
                    <div class="input-group">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-menu-right"></span>
                                </span>
                        <input id="txtRegisterFirstname" class="form-control" type="text"
                               placeholder="First name"/>
                    </div>
                </div>

                <div id="registerSurnameDiv" class="row form-group">
                    <div class="input-group">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-menu-right"></span>
                                </span>
                        <input id="txtRegisterSurname" class="form-control" type="text"
                               placeholder="Surname"/>
                    </div>
                </div>

                <div id="registerCompanyListDiv" class="row form-group">
                    <table>
                        <thead>
                        <tr>
                            <th style="font-size: medium">Company Name</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="companyName" items="${companies}">
                            <tr>
                                <td>
                                    <div class="checkbox">
                                        <label>
                                            <input id="cb + ${companyName}" type="checkbox"> ${companyName}
                                        </label>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="row form-group">
                    <button id="btnRegister" class="btn btn-primary btn-block"
                            data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing registration"
                            type="button" onclick="register()">
                        Register
                    </button>
                </div>

            </div>

        </div>
        <div class="modal-footer">
            <button class="btn btn-default" type="button" data-dismiss="modal" onclick="displayLogin()">
                Login
            </button>
        </div>
    </div>
</div>
</body>
</html>
