<?php
include "../db_online_backup.php";

global $connection;
$reviews_array=array(array());
$i=0;
$shop_name = "east Coast Park";
$shop_name = mysqli_real_escape_string($connection, $shop_name);
$unit_no = '';
$build_id = 22;

$query="SELECT rating_id FROM has_ratings WHERE shop_name = '$shop_name' AND unit_no = '$unit_no' AND build_id = $build_id";
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
?>