<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
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
td img{
    display: block;
    margin-left: auto;
    margin-right: auto;

}
.centered{width: 50px; margin: 0px, auto, 0px, auto;}

</style>


<script type="text/javascript">
	function goBack() {
		window.history.back();
	}
	
	$(document).ready(function() {
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
	} );	
	
	
</script>

</head>

<body>
	
	<div class="page-head">
		<h2 class="pull-left" style="color:#428bca;">Database Backup History</h2>
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

							<div class="widget-head">
								<div class="pull-left">Database Backup Details</div>
								<div class="widget-icons pull-right">
									<a href="#" class="wminimize"><i class="fa fa-chevron-up"></i></a>
									<a href="#" class="wclose"><i class="fa fa-times"></i></a>
								</div>
								<div class="clearfix"></div>
							</div>

							<div class="widget-content">

								<div class="table-responsive">

									<c:if test="${!empty backupHistory}">
											<table class="table table-bordered table-hover"
								 				id="table1">
											<thead>
												<tr style="background-color: #428bca; color: white">
													<th>Sl. No.</th>
													<th>DB Name</th>
													<th class="text-center">Host</th>
													<th class="text-center">Status</th>
													<th>Type Of Backup</th>
													<th>Location</th>
													<th>File Name</th>
													<th>Backup Date</th>
													<th>Backup Time</th>
													<th>Backup By</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${backupHistory}" var="backup" varStatus="db">
													<tr>
														<td>${db.count}</td>
														<td>${backup.dbName}</td>
														<td>${backup.ipAddress}</td>
														<td>${backup.status}</td>
														<td>${backup.backupType}</td>
														<td>${backup.location}</td>
														<td>${backup.fileName}</td>
														
														<td><fmt:formatDate pattern="dd-MM-yyyy" value="${backup.createdDate}" /></td>
														<td><fmt:formatDate pattern="hh:mm:ss a" value="${backup.createdDate}" /></td>
														
														<td>${backup.createdBy}</td>
													</tr>
												</c:forEach>

											</tbody>
										</table>
									</c:if>
								</div>


							</div>
						</div>





					</div>

				</div>

			</div>

		</div>

	</div>




	<!-- table ends -->













	<!-- Matter ends -->



	<!-- Mainbar ends -->
	<div class="clearfix"></div>




</body>
</html>