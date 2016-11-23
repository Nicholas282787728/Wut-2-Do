<?php
include "../db_online.php";

$reviews_array=array(array());
$i=0;

$postal_code = "098070";
$unit_no = '';

// get shop_name as per database
$query="SELECT shop_name FROM details_of_activity_located_in WHERE build_id = 30";
$result=mysqli_query($connection, $query);
$row=mysqli_fetch_assoc($result);
$shop_name = trim(mysqli_real_escape_string($connection, $row['shop_name']));

$query="SELECT id FROM building WHERE postal_code = '$postal_code'";
$result=mysqli_query($connection, $query);
$row=mysqli_fetch_assoc($result);
	// only 1 row as postal_code is unique.
$build_id=$row['id'];
	// selecting reviews
$query="SELECT rating_id FROM has_ratings WHERE LOWER(shop_name) = '$shop_name' AND unit_no = '$unit_no' AND build_id = $build_id";
$result=mysqli_query($connection, $query);
while($row=mysqli_fetch_assoc($result)){
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
print_r($reviews_array);


/*$query="SELECT shop_name FROM has_ratings WHERE rating_id = 147";
$result=mysqli_query($connection, $query);
$row=mysqli_fetch_assoc($result);
$secondName = trim(mysqli_real_escape_string($connection, $row['shop_name']));*/
/*
echo strcmp(strtolower($firstName), strtolower($secondName)) . "<br>";
echo $firstName . "<br>"; 
echo $secondName . "<br>";*/
?>