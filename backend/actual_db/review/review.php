<?php
include "functions_review.php";
include "../db_online.php";

/* pre: retrieve name, date, num_stars, review, postal code and unit no.
post: Inserts review into database tables 'rating' and 'has_ratings' and returns successful message if successful. else, returns unsuccessful message.
algorithm: input (name, date, num_stars, review) into 'rating'.
get rating_id by selecting == input above
get build_id using postal_code
input (rating_id, unit_no, build_id) into 'has_ratings'
*/

global $connection;

$name = mysqli_real_escape_string($connection, urldecode($_POST['username']));
$date = mysqli_real_escape_string($connection, urldecode($_POST['date']));
$num_stars = urldecode($_POST['num_stars']);
$review = mysqli_real_escape_string($connection, urldecode($_POST['review']));
$shop_name = trim(mysqli_real_escape_string($connection, urldecode($_POST['shop_name'])));
$shop_name = str_replace(array('\r', '\n'), "", $shop_name);
$postal_code = urldecode($_POST['postal_code']);
$unit_no = urldecode($_POST['unit_no']);

// insert into 'rating' 
$query="INSERT INTO rating(name, date, num_stars, review) ";
$query.="VALUE('$name', '$date', $num_stars, '$review')";
// input the query
$result=mysqli_query($connection, $query);
if(!$result){
	die('Entering rating failed' . mysqli_error($connection));
}

// get rating_id
$query="SELECT id FROM rating WHERE name = '$name' AND date = '$date' AND num_stars = $num_stars AND review = '$review'";
$result=mysqli_query($connection, $query);
if(!$result){
	die('Selecting rating_id failed' . mysqli_error($connection));
}
$row = mysqli_fetch_assoc($result);
// only 1 row as the info is unique.
$rating_id = $row['id'];

// get build_id
$query="SELECT id FROM building WHERE postal_code = '$postal_code'";
$result=mysqli_query($connection, $query);
if(!$result){
	die('Selecting build_id failed' . mysqli_error($connection));
}
$row=mysqli_fetch_assoc($result);
// only 1 row as postal_code is unique.
$build_id=$row['id'];

// input into 'has_ratings'
$query="INSERT INTO has_ratings(rating_id, shop_name, unit_no, build_id) ";
$query.="VALUE($rating_id, '$shop_name', '$unit_no', $build_id)";
// input the query
$result=mysqli_query($connection, $query);
if(!$result){
	die('Entering has_ratings failed' . mysqli_error($connection));
}

updateAvgScore($num_stars, $shop_name, $unit_no, $build_id);

// if all successful
echo "Success";
?>