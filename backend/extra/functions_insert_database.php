<?php
include "../db_online_backup.php";

// creates 'details_of_activity_located_in', 'building' and 'located_at'
function createMain($shop_name, $unit_no, $address, $postal_code, $cat_id){
	global $connection;

	// insert into 'building'
	$query="INSERT IGNORE INTO building(name, postal_code) ";
	$query.="VALUE('$address', '$postal_code')";
	// input the query
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering building failed' . mysqli_error($connection));
	}

	// grab the id of the building name
	$query="SELECT id FROM building WHERE name = '$address'";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Selecting id failed' . mysqli_error($connection));
	}
	$row = mysqli_fetch_assoc($result);
	$build_id = $row['id'];
	
	// insert into 'details_of_activity_located_in'
	$query="INSERT IGNORE INTO details_of_activity_located_in(shop_name, unit_no, build_id) ";
	$query.="VALUE('$shop_name', '$unit_no', $build_id)";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering details failed' . mysqli_error($connection));
	}

	// insert into 'located_at'
	$query="INSERT IGNORE INTO located_at(cat_id, unit_no, build_id) ";
	$query.="VALUE('$cat_id', '$unit_no', $build_id)";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering details failed' . mysqli_error($connection));
	}
}

// creates 'category'
function createCategory($category){
	global $connection;

	// insert into 'category'
	$query="INSERT IGNORE INTO category(name) ";
	$query.="VALUE('$category')";
	// input the query
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering category failed' . mysqli_error($connection));
	}

	// grab the id of the category
	$query="SELECT id FROM category WHERE name = '$category'";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Selecting category id failed' . mysqli_error($connection));
	}
	else{
		$row = mysqli_fetch_assoc($result);
		// return the category ID
		return $row['id'];
	}
}

function showAllDataByID(){
	global $connection;
	$query="SELECT * FROM users";
	$result = mysqli_query($connection, $query);
	
	if(!$result){
		die('Query FAILED' . mysqli_error($connection));
	}

	while($row = mysqli_fetch_assoc($result)){
		$id = $row['id'];
		echo "<option value ='$id'>$id</option>";
	}
}

function updateTable(){
	global $connection;
	$username = $_POST['username'];
	$password = $_POST['password'];
	$id = $_POST['id'];

	$query = "UPDATE users SET username = '$username', password = '$password' WHERE id = '$id'";

	$result = mysqli_query($connection, $query);
	if(!$result){
		die("QUERY FAILED" . mysqli_error($connection));
	} else{
		echo "Record Updated";
	}
}

function deleteRows(){
	global $connection;
	$username = $_POST['username'];
	$password = $_POST['password'];
	$id = $_POST['id'];

	$query = "DELETE FROM users WHERE id = '$id'";

	$result = mysqli_query($connection, $query);
	if(!$result){
		die("QUERY FAILED" . mysqli_error($connection));
	}else{
		echo "Record Deleted";
	}
}
?>