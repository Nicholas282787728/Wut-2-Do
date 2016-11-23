<?php
include "../db_online.php";

/* pre: retrieves num_stars, unit_no and build_id from user input
post: updates reviews_avg in "details_of_activity_located_in"
Algorithm: retrieve total_reviews using unit_no and build_id.
use total_score = (int) reviews_avg * total_reviews 
total_score increase by new score, and increment total_reviews by 1
divide total_score by total_reviews and update to reviews_avg
helper method
*/
function updateAvgScore($num_stars, $shop_name, $unit_no, $build_id){
	global $connection;
	// get total_reviews
	$query="SELECT total_reviews, reviews_avg FROM details_of_activity_located_in WHERE shop_name = '$shop_name' AND unit_no = '$unit_no' AND build_id = $build_id";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Entering has_ratings failed' . mysqli_error($connection));
	}
	$row=mysqli_fetch_assoc($result);
	// only 1 row as (unit_no and build_id) is unique.
	$total_reviews=$row['total_reviews'] + 1;
	$reviews_avg=$row['reviews_avg'];
	$new_avg=($reviews_avg * $total_reviews + $num_stars) / ($total_reviews);

	// update the table
	$query = "UPDATE details_of_activity_located_in SET reviews_avg = '$new_avg', total_reviews = $total_reviews WHERE shop_name = '$shop_name' AND unit_no = '$unit_no' AND build_id = $build_id";
	$result = mysqli_query($connection, $query);
	if(!$result){
		die("Update failed" . mysqli_error($connection));
	}
}
?>