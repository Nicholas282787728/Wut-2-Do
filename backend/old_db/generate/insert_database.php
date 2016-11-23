<?php
/* Algorithm: Create rows for the table 'building' first. Then get their ids, then check for the respective ids
to create rows for the table 'details_of_activity_located_in'.
*/

include "../db_online_backup.php";

function insertDatabase($results, $gen_id, $category){
	global $connection;
	$cat_id = createCategory($category);
	createContains($gen_id, $cat_id);
	$i = 0;
	while($i < count($results)){
		set_time_limit(30);
		$shop_name = mysqli_real_escape_string($connection, trim($results[$i]));
		$i++;
		$address = mysqli_real_escape_string($connection, trim($results[$i]));
		$i++;
		$postal_code = trim($results[$i]);
		$i++;
		$unit_no = trim($results[$i]);
		$i++;
		$phone_num = str_replace(' ', '', $results[$i]);
		$i++;
		$website = mysqli_real_escape_string($connection, $results[$i]);
		$i++;
		$lat_long = trim($results[$i]);
		$i++;
		$reviews = $results[$i];
		$i++;
		$avg_score = $results[$i];
		$i++;
		$num_reviews = $results[$i];
		$i++;
		createMain($shop_name, $address, $postal_code, $unit_no, $phone_num, $website, $lat_long, $cat_id, $reviews, $avg_score, $num_reviews);
	}
}

/* algorithm: check if postal_code exists (unique) by grabbing id. 
if exists, continue. if not, create.
*/
// creates 'details_of_activity_located_in', 'building', 'located_at'.
function createMain($shop_name, $address, $postal_code, $unit_no, $phone_num, $website, $lat_long, $cat_id, $reviews, $avg_score, $num_reviews){
	global $connection;

	// check if postal_code exists.
	$query="SELECT id FROM building WHERE postal_code = '$postal_code'";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Selecting id ONE failed' . mysqli_error($connection));
	}
	$row=mysqli_fetch_assoc($result);
	$build_id = 0;

	// building doesn't exist yet, must create
	if(is_null($row)){
		// insert into 'building'
		$query="INSERT IGNORE INTO building(address, postal_code, lat_long) ";
		$query.="VALUE('$address', '$postal_code', '$lat_long')";
		// input the query
		$result=mysqli_query($connection, $query);
		if(!$result){
			die('Entering building failed' . mysqli_error($connection));
		}

		// grab the id of the building name
		$query="SELECT id FROM building WHERE postal_code = '$postal_code'";
		$result=mysqli_query($connection, $query);
		if(!$result){
			die('Selecting id TWO failed' . mysqli_error($connection));
		}
		$row = mysqli_fetch_assoc($result);
		$build_id = $row['id'];
	}
	// building exists; get the build_id
	else{
		$build_id = $row['id'];
	}

	set_time_limit(30);

	/* algo: check if repeated record exists inside details_of_activity_located_in. 
	use insert ignore: if cat_id exists, don't insert. else, insert.
	*/
	$query="SELECT * FROM details_of_activity_located_in WHERE shop_name = '$shop_name' AND unit_no = '$unit_no' AND build_id = $build_id";
	$result=mysqli_query($connection, $query);
	// if record exists in details_of_activity_located_in already
	if(mysqli_num_rows($result) > 0){
		// insert into 'located_at'
		$query="INSERT IGNORE INTO located_at(cat_id, shop_name, unit_no, build_id) ";
		$query.="VALUE('$cat_id', '$shop_name', '$unit_no', $build_id)";
		$result=mysqli_query($connection, $query);
		if(!$result){
			die('Entering located_at failed' . mysqli_error($connection));
		}
		return;
	}

	// else if record doesn't exist yet, insert into 'details_of_activity_located_in'
	$query="INSERT INTO details_of_activity_located_in(shop_name, tel_num, website, unit_no, build_id, reviews_avg, total_reviews) ";
	$query.="VALUE('$shop_name', '$phone_num', '$website', '$unit_no', $build_id, '$avg_score', $num_reviews)";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering details failed' . mysqli_error($connection));
	}

	// insert into 'located_at'
	$query="INSERT IGNORE INTO located_at(cat_id, shop_name, unit_no, build_id) ";
	$query.="VALUE('$cat_id', '$shop_name', '$unit_no', $build_id)";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering located_at failed' . mysqli_error($connection));
	}

	// reviews
	if(!is_null($reviews)){
		createReviews($reviews, $shop_name, $unit_no, $build_id);
	}
}

// creates 'has_ratings' and 'rating'.
function createReviews($reviews, $shop_name, $unit_no, $build_id){
	global $connection; 
	$array = array();
	$totalScore = 0;

	foreach($reviews as $review){		
		$name = mysqli_real_escape_string($connection, $review['name']);
		$num_stars = intval($review['num_stars']);
		$text = mysqli_real_escape_string($connection, trim($review['text']));
		$date = $review['date'];

		// insert into 'rating'
		$query="INSERT IGNORE INTO rating(name, num_stars, review, date) ";
		$query.="VALUE('$name', $num_stars, '$text', '$date')";
		$result=mysqli_query($connection, $query);
		if(!$result){
			die('Entering rating failed' . mysqli_error($connection));
		}

		// grab the id of the rating 
		$query="SELECT id FROM rating WHERE name = '$name' AND num_stars = $num_stars AND review = '$text' AND date = '$date'";
		$result=mysqli_query($connection, $query);
		if(!$result){
			die('Selecting id failed' . mysqli_error($connection));
		}
		$row = mysqli_fetch_assoc($result);
		$rating_id = $row['id'];

		// insert into 'has_ratings'
		$query="INSERT IGNORE INTO has_ratings(rating_id, shop_name, unit_no, build_id) ";
		$query.="VALUE('$rating_id', '$shop_name', '$unit_no', $build_id)";
		$result=mysqli_query($connection, $query);
		if(!$result){
			die('Entering has_ratings failed' . mysqli_error($connection));
		}
	}
}

// creates 'genre' and returns the id
function createGenre($genre){
	global $connection;

	// insert into 'genre'
	$query="INSERT IGNORE INTO genre(name) ";
	$query.="VALUE('$genre')";
	// input the query
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering category failed' . mysqli_error($connection));
	}

	// grab the id of the category
	$query="SELECT id FROM genre WHERE name = '$genre'";
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

// creates 'category' and returns the id
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

// creates 'contains' and returns the id
function createContains($gen_id, $cat_id){
	global $connection;

	// insert into 'category'
	$query="INSERT IGNORE INTO contains(gen_id, cat_id) ";
	$query.="VALUE($gen_id, $cat_id)";
	// input the query
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering category failed' . mysqli_error($connection));
	}
}
?>