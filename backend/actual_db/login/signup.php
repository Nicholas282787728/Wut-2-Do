<?php
include "../db_online.php";

/* pre: retrieve username and password
post: creates an account in database and returns successful message if successful. else, returns unsuccessful message.
algorithm: retrieves username and password
pass username into table to find if exists
if exists, return unsuccessful
else create account, return successful
*/

$username = urldecode($_POST['username']);
$password = urldecode($_POST['password']);

global $connection;

// search whether username exists. 
$query="SELECT * FROM user WHERE username = '$username'";
$result=mysqli_query($connection, $query);
// if exists
if(mysqli_num_rows($result) > 0){
	echo "Username already in use.";
}
else{
	$query="INSERT INTO user(username, password) ";
	$query.="VALUE('$username', '$password')";
	// input the query
	$result=mysqli_query($connection, $query);
	echo "Account successfully created.";
}
?>