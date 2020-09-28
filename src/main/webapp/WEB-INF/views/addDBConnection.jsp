<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<!-- Title and other stuffs -->
<title>Dashboard - Lexicon Merchandise</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="keywords" content="">
<meta name="author" content="">
<style type="text/css">
.table td.fit, .table th.fit {
	white-space: nowrap;
	width: 1%;
}
</style>

</head>

<body>
	<!-- Page heading start -->
	<div class="page-head">
		<h2 class="pull-left" style="color: #428bca;">Database Connection
			Form</h2>

		<div class="clearfix"></div>

	</div>
	<!-- Page heading ends -->

	<!-- Matter -->

	<!--   <div class="matter"> -->
	<div class="container">

		<div class="row">

			<div class="col-md-12">


				<div class="widget wgreen">

					<div class="widget-head">
						<div class="pull-left"></div>
						<div class="widget-icons pull-right">
							<a href="#" class="wminimize"><i class="fa fa-chevron-up"></i></a>
							<a href="#" class="wclose"><i class="fa fa-times"></i></a>
						</div>
						<div class="clearfix"></div>
					</div>

					<div class="widget-content">
						<div class="padd">

							<br />

							<!-- Form starts.  -->
							<form:form cssClass="form-horizontal" method="POST"
								class="form-horizontal"
								action="${pageContext.request.contextPath}/saveDbConnect">

								<div class="form-group">
									<label class="col-lg-2 control-label">ID:</label>
									<div class="col-lg-5">
										<form:input id="id" path="id" value="${databaseConnection.id}"
											class="form-control" readonly="true" />
									</div>
								</div>


								<div class="form-group">
									<label class="col-lg-2 control-label">IP/Server
										Address:</label>
									<div class="col-lg-5">
										<form:input id="ipAddress" path="ipAddress"
											value="${databaseConnection.ipAddress}" class="form-control" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-lg-2 control-label">DB Port:</label>
									<div class="col-lg-5">
										<form:input id="dbPort" path="dbPort"
											value="${databaseConnection.dbPort}" class="form-control" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-lg-2 control-label">DB User Name:</label>
									<div class="col-lg-5">
										<form:input id="dbUserName" path="dbUserName"
											value="${databaseConnection.dbUserName}" class="form-control" />
									</div>
								</div>


								<div class="form-group">
									<label class="col-lg-2 control-label">DB Password:</label>
									<div class="col-lg-5">
										<form:input id="dbPassword" path="dbPassword"
											value="${databaseConnection.dbPassword}" class="form-control" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-lg-2 control-label">Environment:</label>
									<div class="col-lg-5">
										<form:select path="operatingSystem" id = "operatingSystem"  class="form-control">
											<form:option value="1"> Windows</form:option>
											<form:option value="2"> Linux</form:option>
										</form:select>
									</div>
								</div>
								
								<div class="form-group" id="linUserDiv" hidden="true">
									<label class="col-lg-2 control-label">Linux User Name:</label>
									<div class="col-lg-5">
										<form:input id="linUserName" path="linUserName" 
											value="${databaseConnection.linUserName}" class="form-control" />
									</div>
								</div>


								<div class="form-group" id="linPassDiv" hidden="true">
									<label class="col-lg-2 control-label">Linux Password:</label>
									<div class="col-lg-5">
										<form:input id="linPassword" path="linPassword"
											value="${databaseConnection.linPassword}" class="form-control" />
									</div>
								</div>
								
								<div class="form-group" id="linPortDiv" hidden="true">
									<label class="col-lg-2 control-label">Linux Port:</label>
									<div class="col-lg-5">
										<form:input id="linPort" path="linPort"
											value="${databaseConnection.linPort}" class="form-control" />
									</div>
								</div>

								

								<div class="form-group">
									<label class="col-lg-2 control-label">Remarks:</label>
									<div class="col-lg-5">
										<textarea id="remarks" class="form-control" name="remarks"
											rows="4" type="textarea"></textarea>
									</div>
								</div>

								<div class="form-group">
									<c:choose>
										<c:when test="${edit}">
											<div class="col-lg-offset-2 col-lg-1 col-xs-4" id="">
												<input type="submit" value="Update"
													class="btn btn-sm btn-primary btn-success" />
											</div>
											<div class="col-lg-1 col-xs-4">

												<a class="btn btn-sm btn-danger" href="addLve">Cancel</a>
											</div>
										</c:when>
										<c:otherwise>
											<div class="col-lg-offset-2 col-lg-1 col-xs-4" id="">
												<input type="submit" value="Connect"
													class="btn btn-sm btn-primary" />
											</div>
											<div class="col-lg-1 col-xs-4">
												<button type="reset" class="btn btn-sm btn-danger ">Reset</button>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
							</form:form>
						</div>

					</div>
					<div class="widget-foot">
						<!-- Footer goes here -->
					</div>
				</div>

				<!-- Table -->

			</div>

		</div>

	</div>




	<!-- table ends -->




<script>

$(document).ready(function(){
    $("#operatingSystem").change(function(){
        
        if($(this).val() == 2){
        	$('#linUserDiv').show();
        	$('#linPassDiv').show();
        	$('#linPortDiv').show();
        }else{
        	$('#linUserDiv').hide();
        	$('#linPassDiv').hide();
        	$('#linPortDiv').hide();
        }
    });
});
</script>








	<!-- Matter ends -->



	<!-- Mainbar ends -->
	<div class="clearfix"></div>




</body>
</html>