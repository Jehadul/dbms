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


<link href="resource/dataTable/buttons.dataTables.min.css" rel="stylesheet">
<script src="resource/dataTable/jquery-1.12.3.js"></script>
<script src="resource/dataTable/jquery.dataTables.min.js"></script>
<script src="resource/dataTable/dataTables.buttons.min.js"></script> 
<script src="resource/dataTable/buttons.flash.min.js"></script>
<script src="resource/dataTable/jszip.min.js"></script>
<script src="resource/dataTable/pdfmake.min.js"></script>
<script src="resource/dataTable/vfs_fonts.js"></script>
<script src="resource/dataTable/buttons.html5.min.js"></script>
<script src="resource/dataTable/buttons.print.min.js"></script> 



<style type="text/css">
td img {
	display: block;
	margin-left: auto;
	margin-right: auto;
}

.centered {
	width: 50px;
	margin: 0px, auto, 0px, auto;
}
</style>



<script type="text/javascript">
	function goBack() {
		window.history.back();
	}
	
	/* $(document).ready(function() {
	    $('#table1').DataTable( {
	        dom: 'Bfrtip',
	        buttons: [
	            'copy', 'csv', 'excel', 'print', {
	                extend: 'pdfHtml5',
	                orientation: 'landscape',
	                pageSize: 'LEGAL'
	            }
	        ]
	    } );
	} ); */
	
	
	
</script>
<%--  <script>
      $(function () {
          $('#nav a').on('click', function (e) {
              e.preventDefault();
              var page = $(this).attr('href');
              $('#content').load(page);
          });
      });
  </script> --%>


</head>

<body>

	<div class="page-head">
		<h2 class="pull-left" style="color: #428bca;">Manual Database
			Backup Form</h2>
		<div class="clearfix"></div>
	</div>

	<!--   <div class="matter"> -->
	<div class="container">

		<div class="row">

			<div class="col-md-12">

				<!-- Table -->

				<div class="row">

					<div class="col-md-12">

						<div class="widget">
						<form> 
						<input type="hidden" value="${pageContext.request.contextPath}" id="contextPath">
						<input type="hidden" value="${databaseConnection.id}" id="id">
						<%-- <input type="hidden" value="${databaseConnection.ipAddress}" id="ipAddress">
						<input type="hidden" value="${databaseConnection.dbPort}" id="dbPort">
						<input type="hidden" value="${databaseConnection.dbUserName}" id="dbUserName">
						<input type="hidden" value="${databaseConnection.dbPassword}" id="dbPassword">
						<input type="hidden" value="${databaseConnection.operatingSystem}" id="operatingSystem"> --%>
						
						
							<div class="widget-head">
								<div class="pull-left">Database Details</div>
								<div class="widget-icons pull-right">
									<span>Server/IP : ${databaseConnection.ipAddress}</span> &nbsp;&nbsp;&nbsp; | &nbsp;
									<a href="#" class="wminimize"><i class="fa fa-chevron-up"></i></a>
									<a href="#" class="wclose"><i class="fa fa-times"></i></a>
								</div>
								<div class="clearfix"></div>
							</div>

							<div class="widget-content">

								<div class="table-responsive">

									<c:if test="${!empty dbList}">
										<table class="table table-bordered table-hover" id="table1">
											<thead>
												<tr style="background-color: #428bca; color: white">
													<th class="text-center" style="background-color: #e8e8e8 !important;"> <input type="checkbox" id="selectAll" name="selectAll"> </th>
													<th>Sl. No.</th>
													<th>DB. Name</th>													
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${dbList}" var="db" varStatus="dbCount">
													<tr>
														<td class="text-center"><input type="checkbox" class="selectChkBox" name="record"></td>
														<td>${dbCount.count}</td>
														<td><input type="hidden" name="dbname" value="${db}"> ${db}</td>
														
														
													</tr>
												</c:forEach>

											</tbody>
										</table>
									</c:if>
								</div>


							</div>
						</div>
						<div class="row"> 
							<div class="col-xs-6"> 
								<select name="location" class="form-control" id="location">										
										<option value="ld"> Local Drive </option>
										<!-- <option value="gd"> Google Drive </option> -->
										<option value="db"> Dropbox </option>
								</select>
							</div>
						</div>
						
						<br/>
						
						<div class="form-group">
									
							<div class="col-lg-1 col-xs-4" id="">
								<input type="button" id="backupSubmit" value="Backup"
									class="btn btn-sm btn-primary" />
							</div>
							<div class="col-lg-1 col-xs-4">
								<button type="reset" id="backupReset" class="btn btn-sm btn-danger ">Reset</button>
							</div>
						</div>
						</form>

					</div>

				</div>

			</div>

		</div>

	</div>
	<!-- table ends -->
	<!-- Mainbar ends -->
	
	<script type="text/javascript">
	 	$(document).ready(function(){
	 		$('#selectAll').click(function(e){
	 		    var table= $(e.target).closest('table');
	 		    $('td input:checkbox',table).prop('checked',this.checked);
	 		});
	 		
	 		
	 		$("#backupSubmit").click(function(e){
	 			var dbNames = [];
	 			$("table tbody").find('input[name="record"]').each(function(){
	            	if($(this).is(":checked")){
	                   var row =  $(this).parents("tr");
	                   var rowdata = row[0].cells[2].innerText;
	                   dbNames.push(rowdata); 
	                }
	            });
	 			
	 			var contextPath = $('#contextPath').val()
	 			var path = contextPath + "/backupDatabse";
	 			var location = $('#location').val();
	 			var id = $('#id').val();
	 			/* var ipAddress = $('#ipAddress').val();
				var dbPort = $('#dbPort').val();
				var dbUserName = $('#dbUserName').val();
				var dbPassword = $('#dbPassword').val();
				var operatingSystem = $('#operatingSystem').val(); */
	 			
	 			var param = {
	 				dbNames : dbNames,
	 				 location : location,
	 				/*ipAddress : ipAddress,
	 				dbPort : dbPort,
	 				dbUserName : dbUserName,
	 				dbPassword : dbPassword,
	 				operatingSystem : operatingSystem */
	 				id : id
	 			}
	 			
	 			if(dbNames.length > 0){
	 				$("#backupSubmit").prop('disabled', true);
	 				$("#backupReset").prop('disabled', true);
	 				postSubmit(path, param, 'POST');
	 			} else {
	 				alert("Please Select a Database...");
	 			}
	 			
	 		});
	 	
		});
	 	
	 // submit form by JavaScript
	 	function postSubmit(path, params, method) {
	 		method = method || "POST";
	 		var form = document.createElement("form");
	 		form.setAttribute("method", method);
	 		form.setAttribute("action", path); 
	 		form.setAttribute("id", "nazdaq415");

	 		for ( var key in params) {
	 			if (params.hasOwnProperty(key)) {
	 				var hiddenField = document.createElement("input");
	 				hiddenField.setAttribute("type", "hidden");
	 				hiddenField.setAttribute("name", key);
	 				hiddenField.setAttribute("value", params[key]);

	 				form.appendChild(hiddenField);
	 			}
	 		}
	 		document.body.appendChild(form);
	 		form.submit();
	 		$("#nazdaq415").remove();
	 	}
	</script>
	<div class="clearfix"></div>
</body>
</html>