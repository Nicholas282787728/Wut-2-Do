<?php
include "../db_online.php";

/*
 * pre: retrieve username and password
 * post: logs in if username and password tallies database result and returns successful message if successful. else, returns unsuccessful message.
 * algorithm: retrieves username and password
 * pass username and password into table to find if exists
 * if exists, return successful
 * else, return successful
 */

global $connection;

if (! isset ( $_POST ['username'] ) || ! isset ( $_POST ['password'] )) {
	exit ( "Username or Password is unset." );
}

$username = urldecode ( $_POST ['username'] );
$password = urldecode ( $_POST ['password'] );

$query = "SELECT * FROM user WHERE username = '$username' AND password = '$password'";
$result = mysqli_query ( $connection, $query );

if (mysqli_num_rows ( $result ) > 0) {
	echo "Log in successful.";
} else {
	echo "Log in unsuccessful.";
}
?>