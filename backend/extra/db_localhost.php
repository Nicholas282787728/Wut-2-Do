<?php	
	$connection = mysqli_connect('localhost', 'root', '', 'location_of_activity');
	//directory, username, password, database

	if(!$connection){
		die("Database connection failed");
	}
?>