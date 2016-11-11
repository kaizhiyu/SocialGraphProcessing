<%-- 
    Document   : Settings
    Created on : 23 Sep 2016, 12:34:37 PM
    Author     : kryli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <div id="blankInputError" class="alert alert-dismissible alert-danger" hidden="true"
         style="font-size: medium">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Error!</strong> You have not entered a company name. Input fields cannot be left blank.
    </div>

    <div id="successfulCompanyAdd" class="alert alert-dismissible alert-success" hidden="true"
         style="font-size: medium">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Success!</strong> Adding company completed.
    </div>

    <div id="unsuccessfulCompanyAdd" class="alert alert-dismissible alert-danger" hidden="true"
         style="font-size: medium">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Error!</strong> Adding company failed.
    </div>

    <div id="successfulCompanyRemove" class="alert alert-dismissible alert-success" hidden="true"
         style="font-size: medium">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Success!</strong> Removing company completed.
    </div>

    <div id="unsuccessfulCompanyRemove" class="alert alert-dismissible alert-danger" hidden="true"
         style="font-size: medium">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Error!</strong> Removing company failed.
    </div>

    <div id="removeCompanyModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button class="close" type="button" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h3 class="modal-title">
                        Delete
                        <label id="removeCompanyName_Header">${currentCompany}</label>
                        ?
                    </h3>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete
                    <label id="removeCompanyName_Body">${currentCompany}</label>
                    ?
                </div>
                <div class="modal-footer">
                    <button class="btn btn-default" type="button" data-dismiss="modal">Close</button>
                    <button class="btn btn-danger" type="button" data-dismiss="modal" onclick="removeCompany()">
                        Delete
                    </button>
                </div>
            </div>
        </div>
    </div>

    <h3>Settings</h3>

    <script type="text/javascript">
        function blankCompanyInputError() {
            $('#blankInputError').fadeIn(1000);
            $('#blankInputError').fadeOut(3000).delay(2000);

            document.getElementById("addCompanyDiv").className = "form-group has-error";
        }

        function addCompanySuccess() {
            $('#successfulCompanyAdd').fadeIn(1000);
            $('#successfulCompanyAdd').fadeOut(3000).delay(2000);

            document.getElementById("addCompanyDiv").className = "form-group";
        }

        function addCompanyError() {
            $('#unsuccessfulCompanyAdd').fadeIn(1000);
            $('#unsuccessfulCompanyAdd').fadeOut(3000).delay(2000);
        }

        function removeCompanySuccess() {
            $('#successfulCompanyRemove').fadeIn(1000);
            $('#successfulCompanyRemove').fadeOut(3000).delay(2000);
        }

        function removeCompanyError() {
            $('#unsuccessfulCompanyRemove').fadeIn(1000);
            $('#unsuccessfulCompanyRemove').fadeOut(3000).delay(2000);
        }
    </script>
</head>

<body>
<div class="jumbotron" style="padding: 10px">
    <div class="row">
        <table id="companyList" class="table table-hover">
            <thead>
            <tr>
                <th style="width: 10%; font-size: large">Operations</th>
                <th style="width: 95%; font-size: large">Company Name</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="companyName" items="${userCompanyList}">
                <tr>
                    <td style="padding-top: 10px">
                        <button class="btn btn-danger btn-sm" type="button" data-toggle="modal"
                                data-target="#removeCompanyModal"
                                onclick="changeSettingsModalCompanyName('${companyName}')"> Remove
                        </button>
                    </td>
                    <td style="padding-left: 20px">
                        <h5>${companyName}</h5>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <br>

    <div class="row">
        <table class="table">
            <tbody>
            <tr>
                <td style="width: 10%">
                    <button id="btnAddCompany" class="btn btn-primary"
                            data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Adding company"
                            type="button" onclick="addCompany()">Add Company
                    </button>
                </td>

                <td style="width: 90%">
                    <div id="addCompanyDiv" class="form-group">
                        <input id="txtNewCompany" class="form-control" type="text" placeholder="Company name"/>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
