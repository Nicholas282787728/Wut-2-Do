<?php
include "../db_online_backup.php";

// returns genre from Android input
function getAndroidGenre(){
	return $_GET['genre'];
}

// returns category from Android input
function getAndroidCategory(){
	return $_GET['category'];
}

function getAndroidSearch(){
	return $_GET['search'];
}

function getAndroidLatLong(){
	return $_GET['latlong'];
}

// returns categories from 'genre' Android input
function getCategory($genre){
	global $connection;
	// get the genre's 'id'
	$query="SELECT id FROM genre WHERE name = '$genre'";
	$result=mysqli_query($connection, $query);
	$row = mysqli_fetch_row($result);
	// there should only be 1 result, as such getting [0] is equivalent to getting the id 
	$id = $row[0];
	// get the categories' 'id' (cat_id). Returns multiple values.
	$query="SELECT cat_id FROM contains WHERE gen_id = $id";
	$result=mysqli_query($connection, $query);

	// loop through each cat_id to get the category names
	$cat_array = array(array());
	$i = 0;
	while($row = mysqli_fetch_assoc($result)){
		$cat_id = $row['cat_id'];
		$query="SELECT name FROM category WHERE id = $cat_id";
		$cat_name_result=mysqli_query($connection, $query);
		// there is only 1 row since $cat_id is unique, and only 1 column since selecting only 'name'
		$row2 = mysqli_fetch_row($cat_name_result);
		$cat_array[$i]['name'] = $row2[0];
		$i++;
	}
	/*	Alternative method - longer method
	$id_array = array();
	// add the cat_ids into $id_array
	while($row = mysqli_fetch_assoc($result)){
		array_push($id_array, $row['cat_id']);
	}
	
	$cat_array = array();
	foreach($id_array as $cat_id){
		$query="SELECT name FROM category WHERE id = $cat_id";
		$result=mysqli_query($connection, $query);
		$row = mysqli_fetch_array($result);
		// there should only be one result as well since $cat_id is unique.
		array_push($cat_array, $row[0]);
	}*/
	return $cat_array;
}

/* algorithm:
Upon receiving the category (by user click), return all the shop names + addresses
Table 'category' get 'id' from 'name', using 'cat_id' to get all 'unit_no' and 
'build_id' where 'cat_id'=='id', pass the 'unit_no' and 'build_id' into 
'details_of_activity_located_in' to get the shop name, and pass 'build_id' into
'building' to get 'name'.
returns an array that stores 'shop_name' and 'building_name', given 'category' Android input
*/
function getDetailsOfActivity($category, $user_lat_long){
	global $connection;
	// get the category's 'id'
	$query="SELECT id FROM category WHERE name = '$category'";
	$result=mysqli_query($connection, $query);
	$row = mysqli_fetch_row($result);
	// there should only be 1 result since $category is unique, as such getting [0] is equivalent to getting the id 
	$id = $row[0];
	// get the details of the locations under the "$category". Returns multiple values.
	// format of array: $row[i]["unit_no"] or $row[i]["build_id"]
	$query="SELECT unit_no, build_id FROM located_at WHERE cat_id = $id";
	$result=mysqli_query($connection, $query);

	// loop through (unit_no, build_id) to get the shop names and address
	// and put them inside $details_array
	$details_array = array(array());
	$i = 0;
	while($row = mysqli_fetch_assoc($result)){
		$unit_no = $row['unit_no'];
		$build_id = $row['build_id'];
		// selecting shop name
		$query="SELECT shop_name FROM details_of_activity_located_in WHERE unit_no = '$unit_no' AND build_id = $build_id";
		$result2=mysqli_query($connection, $query);
		// there is only 1 row since the combination of (unit_no, build_id) is unique 
		// and only 1 column since selecting only 'shop_name'
		$row2=mysqli_fetch_row($result2);
		$shop_name=$row2[0];

		// selecting building address
		$query="SELECT name, lat_long FROM building WHERE id = $build_id";
		$result2=mysqli_query($connection, $query);
		// there is only 1 row since 'build_id' is unique 
		// and only 1 column since selecting only 'name'
		$row2 = mysqli_fetch_assoc($result2);
		$address=$row2['name'];
		$lat_long=$row2['lat_long'];
		$distance=getDistance($lat_long, $user_lat_long);

		$details_array[$i]['name'] = $shop_name;
		$details_array[$i]['address'] = $address;
		$details_array[$i]['distance'] = $distance;
		$details_array[$i]['lat_long'] = $lat_long;

		$i++;
	}
	// sort $details_array according to distance from $user_lat_long in ascending order
	$details_array = sortDistance($details_array);

	return $details_array;
}

function getAllDetails($user_lat_long){
	global $connection;
	// loop through (unit_no, build_id) to get the shop names and address
	// and put them inside $details_array
	$details_array = array(array());
	$i = 0;
	$query="SELECT shop_name, build_id FROM details_of_activity_located_in";
	// result is an array that contains multiple rows and 2 columns. i.e multiple shop_name and build_id
	$result=mysqli_query($connection, $query);
	while($row = mysqli_fetch_assoc($result)){
		$shop_name=$row['shop_name'];
		$build_id=$row['build_id'];

		// selecting building address
		$query="SELECT name, lat_long FROM building WHERE id = $build_id";
		$result2=mysqli_query($connection, $query);
		// there is only 1 row since 'build_id' is unique 
		// and only 1 column since selecting only 'name'
		$row2 = mysqli_fetch_assoc($result2);
		$address=$row2['name'];
		$lat_long=$row2['lat_long'];
		$distance=getDistance($lat_long, $user_lat_long);

		$details_array[$i]['name'] = $shop_name;
		$details_array[$i]['address'] = $address;
		$details_array[$i]['distance'] = $distance;
		$details_array[$i]['lat_long'] = $lat_long;

		$i++;
	}
	// sort $details_array according to distance from $user_lat_long in ascending order
	$details_array = sortDistance($details_array);

	return $details_array;
}

function getSearch($search, $user_lat_long){
	global $connection;
	// get 'id' and 'name' of the building from database
	$query="SELECT id, name, lat_long FROM building WHERE LOWER(name) LIKE LOWER('%$search%')";
	$result=mysqli_query($connection, $query);
	// if no results, return empty array
	if($result == FALSE){
		return array();
	}
	else{
		$details_array = array(array());
		$i = 0;
		// loop through 'id' to get the shop name
		// and put them inside $details_array
		while($row = mysqli_fetch_assoc($result)){
			$build_id = $row['id'];
			$address = $row['name'];
			$lat_long = $row['lat_long'];
			$distance=getDistance($lat_long, $user_lat_long);

			// selecting shop name, returns multiple results as build_id is not unique in this table
			$query="SELECT shop_name FROM details_of_activity_located_in WHERE build_id = $build_id";
			$result2=mysqli_query($connection, $query);
			while($row2=mysqli_fetch_row($result2)){
			//$row2 only has a column ('shop_name')
				$details_array[$i]['name'] = $row2[0];
				$details_array[$i]['address'] = $address;
				$details_array[$i]['distance'] = $distance;
				$details_array[$i]['lat_long'] = $lat_long;
				$i++;
			}
		}
		// sort $details_array according to distance from $user_lat_long in ascending order
		$details_array = sortDistance($details_array);
		return $details_array;
	}
}

// returns the distance given the latitude and longitude of the location and the user
function getDistance($lat_long, $user_lat_long){
	$link = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $user_lat_long .
	"&destinations=" . $lat_long . "&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
	$handle = file_get_contents($link);
	$data = json_decode($handle, true);	

	// $distance value is in meters
	$distance = $data['rows'][0]['elements'][0]['distance']['value'];

	return number_format($distance/1000, 2);
}

//test case
// https://maps.googleapis.com/maps/api/distancematrix/json?origins=1.348796,103.749428&destinations=1.358863,103.751839&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ

// sorts the array in ascending order of distance from user
function sortDistance($array){
	// Obtain a list of columns
	foreach ($array as $key => $row) {
		$distance[$key] = $row['distance'];
	}

	// Sort the data with $distance ascending
	// Add $array as the last parameter, to sort by the common key
	array_multisort($distance, SORT_ASC, $array);

	return $array;
}