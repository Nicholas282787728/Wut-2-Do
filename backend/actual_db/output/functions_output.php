<?php
include "../db_online.php";

/* for the 6 return functions: pre: retrieves the relevant user input.
post: returns user input as a string */

// returns genre from Android input
function getAndroidGenre(){return $_GET['genre'];}

// returns category from Android input
function getAndroidCategory(){return $_GET['category'];}

// returns search from Android input
function getAndroidSearch(){return $_GET['search'];}

// returns latlong from Android input
function getAndroidLatLong(){return $_GET['latlong'];}

// returns postal_code from Android input
function getAndroidPostalCode(){return $_GET['postal_code'];}

// returns unit_no from Android input
function getAndroidUnitNo(){return $_GET['unit_no'];}

// returns shop_name from Android input
function getAndroidShopName(){return $_GET['shop_name'];}

/* pre: retrieves genre from user input
post: returns an array containing the names of categories
Algorithm: Retrieves the genre's id and use it to retrieve the relevant categories' id.
Then pass in the categories' id to retrieve the category names. */
function getCategory($genre){
	global $connection;
	// get genre's 'id'
	$query="SELECT id FROM genre WHERE name = '$genre'";
	$result=mysqli_query($connection, $query);
	$row = mysqli_fetch_assoc($result);
	// there should only be 1 result/row as genre is unique
	$id = $row['id'];
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
		// there is only 1 row since $cat_id is unique
		$row2 = mysqli_fetch_assoc($cat_name_result);
		$cat_array[$i]['name'] = $row2['name'];
		$i++;
	}
	$cat_array = sortDistance($cat_array);
	return $cat_array;
}

/* pre: retrieves category from user input and user's coordinates
post: returns a sorted array (descending order, distance from user) that containing all details of activity (i.e results) that includes:
shop_name, address, distance_from_user, tel_num, website, reviews_avg, lat_long
algorithm: get category's id, and get unit_no, build_id.
from unit_no and build_id, get shop_name, tel_num, website, reviews_avg, address and lat_long
calculate the distance from user using user_lat_long and lat_long of location */
function getDetailsOfActivity($category, $user_lat_long){
	global $connection;
	// get the category's 'id'
	$query="SELECT id FROM category WHERE name = '$category'";
	$result=mysqli_query($connection, $query);
	$row = mysqli_fetch_assoc($result);
	// there should only be 1 row since $category is unique
	$id = $row['id'];
	// get the details of the locations under the "$category". Returns multiple values.
	$query="SELECT shop_name, unit_no, build_id FROM located_at WHERE cat_id = $id";
	$result=mysqli_query($connection, $query);

	// loop through (unit_no, build_id) to get the shop names and address
	// and put them inside $details_array
	$details_array = array(array());
	$i = 0;
	while($row = mysqli_fetch_assoc($result)){
		// need to escape shop_name.
		$shop_name = mysqli_real_escape_string($connection, $row['shop_name']);
		$unit_no = $row['unit_no'];
		$build_id = $row['build_id'];

		// selecting shop name
		$query="SELECT shop_name, tel_num, website, reviews_avg FROM details_of_activity_located_in WHERE shop_name = '$shop_name' AND unit_no = '$unit_no' AND build_id = $build_id";
		$result2=mysqli_query($connection, $query);
		//echo $shop_name . " " . $unit_no . " " . $build_id . "<br>";
		// there is only 1 row since the combination of (shop_name, unit_no, build_id) is unique 
		$row2=mysqli_fetch_assoc($result2);

		// selecting building address
		$query="SELECT address, lat_long FROM building WHERE id = $build_id";
		$result2=mysqli_query($connection, $query);
		// there is only 1 row since 'build_id' is unique 
		$row3=mysqli_fetch_assoc($result2);
		// calculating distance from user
		$lat_long=$row3['lat_long'];
		$distance=getDistance($lat_long, $user_lat_long);

		$details_array[$i] = setDetailsArray($row2['shop_name'], $row3['address'], $distance, $row2['tel_num'], $row2['website'], $row2['reviews_avg'], $lat_long, $unit_no, $build_id);

		$i++;
	}
	// sort $details_array according to distance from $user_lat_long in ascending order
	$details_array = sortDistance($details_array);

	return $details_array;
}

/* pre: retrieves user coordinates
post: returns a sorted array (descending order, distance from user) that containing all details of activity (i.e results) that includes:
shop_name, address, distance_from_user, tel_num, website, reviews_avg, lat_long
algorithm: get shop_name, tel_num, website, reviews_avg, address and lat_long
calculate the distance from user using user_lat_long and lat_long of location */
/*function getAllDetails($user_lat_long){
	global $connection;
	// loop through (unit_no, build_id) to get the shop names and address
	// and put them inside $details_array
	$details_array = array(array());
	$i = 0;
	$query="SELECT shop_name, tel_num, website, reviews_avg, build_id FROM details_of_activity_located_in";
	$result=mysqli_query($connection, $query);
	// there are multiple rows and multiple columns (shop_name,...,build_id)
	while($row = mysqli_fetch_assoc($result)){
		set_time_limit(30);
		$shop_name=$row['shop_name'];
		$build_id=$row['build_id'];

		// selecting building address
		$query="SELECT address, lat_long FROM building WHERE id = $build_id";
		$result2=mysqli_query($connection, $query);
		// there is only 1 row since 'build_id' is unique 
		$row2 = mysqli_fetch_assoc($result2);
		$address=$row2['address'];
		$lat_long=$row2['lat_long'];
		$distance=getDistance($lat_long, $user_lat_long);

		$details_array[$i] = setDetailsArray($shop_name, $address, $distance, $row['tel_num'], $row['website'], $row['reviews_avg'], $lat_long);

		$i++;
	}
	// sort $details_array according to distance from $user_lat_long in ascending order
	$details_array = sortDistance($details_array);

	return $details_array;
}*/

/* pre: receives search result from user input and user's coordinates
post: returns a sorted array (descending order, distance from user) that containing all details of activity (i.e results) that includes:
shop_name, address, distance_from_user, tel_num, website, reviews_avg, lat_long. If search has no results, return an empty array.
Algorithm: Assume user entered 'name' (e.g White Sands). Search through building 'name'.
If found, pass through performSearch function and return results
If not found, send search key to text_search
	if postal code found i.e is a building
		put name into the row (everything in small letters w/o white spaces)
		pass through performSearch function and return results
	else if postal code not found i.e not a building
		pass search key into address and search
			if found
				pass through performSearch function and return results
			else
			return empty array*/

			function getSearch($search, $user_lat_long){
				global $connection;

				// remove whitespace and make all to lower case
				$searchName = str_replace(' ', '', $search);
				$searchName = strtolower($searchName);

				// search whether name exists. Only 1 result as building should have unique names
				$query="SELECT id, address, lat_long FROM building WHERE name = '$searchName'";
				$result=mysqli_query($connection, $query);
				// if results found
				if(mysqli_num_rows($result) > 0){
					return performSearch($result, $user_lat_long);
				}
				// else, means not cached yet, so perform search
				else{
					$link = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" . str_replace(' ', '+', $search) .
					"+Singapore&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
					$handle = file_get_contents($link);
					$data = json_decode($handle, true);	
					$results = $data['results'];
					// take the 1st result. just assume that 1st result is the most relevant one
					$value = $results['0'];

					$postal_code = findPostal($value['formatted_address']);
					// if postal code found
					if(strcmp($postal_code, "") != 0){
						// search for postal code in database
						$query="SELECT id, address, lat_long FROM building WHERE postal_code = '$postal_code'"; // only 1 result (unique postal_code)
						$result=mysqli_query($connection, $query);		
						// if results found, update the database (name of building) and return results
						if(mysqli_num_rows($result) > 0){
							$query="UPDATE building SET name='$searchName' WHERE postal_code = '$postal_code'";
							$result2=mysqli_query($connection, $query);
							if(!$result2){
								die('Updating building name failed' . mysqli_error($connection));
							}
							return performSearch($result, $user_lat_long);
						}
						// if results not in database, return empty array
						else{
							return array(array());
						}
					}
					// if postal code not found i.e not a building, but address e.g Pasir Ris.
					else{
						// need to escape $search as the value in 'address' is escaped.
						$search = mysqli_real_escape_string($connection, $search);
						$query="SELECT id, address, lat_long FROM building WHERE LOWER(address) LIKE LOWER('%$search%')"; // possible multiple results
						$result=mysqli_query($connection, $query);
						return performSearch($result, $user_lat_long);
					}
				}
			}

/* pre: retrieves mysqli_query that contains the id, address and lat_long of building location 
post: returns a sorted array (descending order, distance from user) that containing all details of activity (i.e results) that includes:
shop_name, address, distance_from_user, tel_num, website, reviews_avg, lat_long. If mysqli_query returns no results, return an empty array.
algorithm: loop through the result of mysqli_query retrieved to generate all shop_name, tel_num, website and reviews_avg.
helper function*/
function performSearch($result, $user_lat_long){
	global $connection;
	
	$details_array = array(array());
	$i = 0;
	$valid = false;
	// loop through 'id' to get the shop name
	// and put them inside $details_array
	while($row = mysqli_fetch_assoc($result)){
		$valid = true;
		$build_id = $row['id'];
		$address = $row['address'];
		$lat_long = $row['lat_long'];
		$distance=getDistance($lat_long, $user_lat_long);

		// selecting shop name, returns multiple results as build_id is not unique in this table
		$query="SELECT shop_name, unit_no, tel_num, website, reviews_avg FROM details_of_activity_located_in WHERE build_id = $build_id";
		$result2=mysqli_query($connection, $query);
		while($row2=mysqli_fetch_assoc($result2)){
			$details_array[$i] = setDetailsArray($row2['shop_name'], $address, $distance, $row2['tel_num'], $row2['website'], $row2['reviews_avg'], $lat_long, $row2['unit_no'], $build_id);
			$i++;
		}
	}
	// sort $details_array according to distance from $user_lat_long in ascending order
	if($valid){
		$details_array = sortDistance($details_array);
	}
	
	// returns empty array if doesn't enter while loop.
	return $details_array;
}

/* pre: retrieves postal_code and unit_no of location
post: returns an array of reviews in descending order of the review date.
algorithm: get build_id using postal_code
get rating_id using unit_no and build_id
loop through all rating_id and get name, date, num_stars and review and put them into an array*/
function getReviews($postal_code, $unit_no, $shop_name){
	global $connection;
	$reviews_array=array(array());
	$i=0;
	$j=0;

	/*// echo "present name: " . $shop_name . "<br>";
	$firstName = $shop_name;

	$query="SELECT shop_name FROM has_ratings WHERE rating_id = 203";
	$result=mysqli_query($connection, $query);
	$row=mysqli_fetch_assoc($result);
	$shop_name = trim(mysqli_real_escape_string($connection, $row['shop_name']));
	// $shop_name = $row['shop_name'];
	// echo "From database: " .$shop_name . "<br>";
	echo "difference in value: " . strcmp(strtolower($firstName), strtolower($secondName)) . "<br>";
	for($i=0; $i<strlen($firstName); $i++){
		$temp1 = substr($firstName, $i, 1);
		$temp2 = substr($shop_name, $i, 1);
		echo "difference in value: " . strcmp(strtolower($temp1), strtolower($temp2)) . " where i = $i" . "<br>";
	}
	echo $firstName . " " . strlen($firstName) . "<br>"; 
	echo $shop_name . " " . strlen($shop_name) . "<br>";
*/
	$query="SELECT id FROM building WHERE postal_code = '$postal_code'";
	$result=mysqli_query($connection, $query);
	$row=mysqli_fetch_assoc($result);
	// only 1 row as postal_code is unique.
	$build_id=$row['id'];

	// selecting reviews
	$query="SELECT rating_id, shop_name FROM has_ratings WHERE unit_no = '$unit_no' AND build_id = $build_id";
	$result=mysqli_query($connection, $query);
	// echo mysqli_num_rows($result) . "<br>";
	while($row=mysqli_fetch_assoc($result)){
		$isCorrectShop = true;
		if(strlen($row['shop_name']) != strlen($shop_name)){
			$isCorrectShop = false;
			// echo "Uh-oh <br>";
		}
		if($isCorrectShop == true){
			for($j=0; $j<strlen($shop_name); $j++){
				$temp1 = substr($row['shop_name'], $j, 1);
				$temp2 = substr($shop_name, $j, 1);
				if(strcmp(strtolower($temp1), strtolower($temp2)) != 0){
					$isCorrectShop = false;
					// echo "difference in value: " . strcmp(strtolower($temp1), strtolower($temp2)) . " where i = $i" . "<br>";
					break;
				}
			}
			if($isCorrectShop == true){
				$id = $row['rating_id'];
				$query="SELECT name, date, num_stars, review FROM rating WHERE id = $id";
				$result2=mysqli_query($connection, $query);
				$row2=mysqli_fetch_assoc($result2);
				
				$reviews_array[$i]['name'] = $row2['name'];
				$reviews_array[$i]['date'] = $row2['date'];
				$reviews_array[$i]['num_stars'] = $row2['num_stars'];
				$reviews_array[$i]['review'] = $row2['review'];
				$i++;
			}
		}
	}

	usort($reviews_array, 'date_compare');
	return $reviews_array;
}

/* pre: retrieves variables: shop_name, address, distance, telephone_number, website, average_review_score, latitude_longitude
post: returns an array containing all the attributes
helper function */
function setDetailsArray($name, $address, $distance, $tel_num, $website, $reviews_avg, $lat_long, $unit_no, $build_id){
	$details_array=array();

	$details_array['name'] = $name;
	$details_array['address'] = $address;
	$details_array['distance'] = $distance;
	$details_array['tel_num'] = $tel_num;
	$details_array['website'] = $website;
	$details_array['reviews_avg'] = $reviews_avg;
	$details_array['lat_long'] = $lat_long;
	$details_array['unit_no'] = $unit_no;
	$details_array['build_id'] = $build_id;

	return $details_array;
}

/* pre: receives latitude and longitude of location and user
post: returns distance of user from location in km in 2d.p 
helper function */
function getDistance($lat_long, $user_lat_long){
	$link = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $user_lat_long .
	"&destinations=" . $lat_long . "&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
	$handle = file_get_contents($link);
	$data = json_decode($handle, true);	

	// $distance value is in meters
	$distance = $data['rows'][0]['elements'][0]['distance']['value'];

	return number_format($distance/1000, 2);
}

/* pre: receives an array with 'date' attribute
post: returns an array sorted in descending order of date 
helper function */
function date_compare($a, $b){
	$t1 = strtotime($a['date']);
	$t2 = strtotime($b['date']);
	return $t2 - $t1;
}    

/* pre: receives an array with 'distance' attribute
post: returns a sorted array in ascending order of distance from user
helper function */
function sortDistance($array){
	// Obtain a list of columns
	foreach ($array as $key => $row) {
		$distance[$key] = $row['distance'];
	}

	// Sort the data with $distance ascending
	array_multisort($distance, SORT_ASC, $array);

	return $array;
}

/* pre: receives an array with 'distance' attribute
post: returns a sorted array in ascending lexicographical order
helper function */
function sortCategories($array){
	// Obtain a list of columns
	foreach ($array as $key => $row) {
		$category[$key] = $row['distance'];
	}

	// Sort the data with $category ascending
	array_multisort($category, SORT_ASC, $array);

	return $array;
}
//test case
// https://maps.googleapis.com/maps/api/distancematrix/json?origins=1.348796,103.749428&destinations=1.358863,103.751839&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ

/* pre: receives address
post: returns postal code 
helper method*/
function findPostal($address){
	$subject = "$address";
	$pattern = "/[0-9]{6}/";
	if(preg_match($pattern, $subject, $matches) == 1)
		return $matches[0];
	else
		return "";
}
?>