<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!--
	Name: Aaron May
	Date : 06/11/16
-->

<!DOCTYPE html>
<html>
	<head>
		<title>Immoscout24 Coding Challenge</title>
		<meta charset="UTF-8">
        <link rel="stylesheet" type="text/css" href = "css/design.css" />
	</head>
	<body>
		<h1>Immoscout24 Coding Challenge</h1>
		<h3><em>by Aaron May</em></h3>
		<div class="inputField">
			<form action="ProcessURL" method="POST">
       			<input id="url" type="url" = name="url" placeholder="Input URL here." required /> <br>
      			<input id="submit" name="submit" type="submit" value="Submit">
			</form>
		</div>
		<div class="errorMessage"><!-- Output error message that may occur while trying to connect to URL -->
			<%if (request.getAttribute("errorMessage") != null) { %>
			<p><%= request.getAttribute("errorMessage") %></p>
			<% } %>
		</div>

		<!-- only display once valid results have been extracted from URL -->
		<%if (request.getAttribute("processed") != null && request.getAttribute("processed").equals(true)) { %>
		<div class="dataOutput">
			<table>
				<tr class="colourTR">
					<td class="heading">URL</td>
					<td> <%= request.getAttribute("url") %></td>
				</tr>
				<tr>
					<td class="heading">HTML Version</td>
					<td> <%= request.getAttribute("htmlVersionType") %></td>
				</tr>
				<tr class="colourTR">
					<td class="heading">Page title</td>
					<td><%= request.getAttribute("pageTitle") %></td>
				</tr>
				<tr>
					<td class="heading">Headers Count</td><% int[] headerCount = (int[])request.getAttribute("headerCount");%>
					<td><span>H1:<%= headerCount[0]%> | </span>
						<span>H2:<%= headerCount[1]%> | </span>
						<span>H3:<%= headerCount[2]%> | </span>
						<span>H4:<%= headerCount[3]%> | </span>
						<span>H5:<%= headerCount[4]%> | </span>
						<span>H6:<%= headerCount[5]%></span>
					</td>
				</tr>
				<tr class="colourTR">
					<td class="setLeft heading" colspan="2">Hypermedia Links</td><% int[] linkCount = (int[])request.getAttribute("linkCount");%> 
				</tr>
				<tr>
					<td class="heading">Internal</td>
					<td> <%= linkCount[0]%></td>
				</tr>
				<tr>
					<td class="heading">External</td>
					<td> <%= linkCount[1]%></td>
				</tr>
				<tr class="colourTR">
					<td class="heading">Contains Login Form</td>
					<td> <%= request.getAttribute("containsLoginForm") %></td>
				</tr>
			</table>
		<% } %>
	</body>
</html>



