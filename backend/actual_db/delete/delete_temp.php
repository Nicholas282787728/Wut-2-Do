<?php
include "../db_online.php";
?>

<?php
/* Algorithm: Read each line from text file as unit_no and build_id. Format: unit_no\nbuild_id, then use readline.
Delete this row in details_of_activity
check if build_id still exists in details_of_activity. if doesn't exist, delete building row.
delete row with unit_no, build_id in located_at.
delete rows which tallies with unit_no, build_id in has_ratings. Grab all the ids of rows deleted
delete rows in rating that tallies with the ids.
*/	
global $connection;
$deleteFile = "../text_files/todelete.txt";

if($handle = fopen($deleteFile, 'r')){
	while(!feof($handle)){
		$shop_name = trim(fgets($handle));
		$build_id = trim(fgets($handle));
		deleteDetailsOfActivity($shop_name, $build_id);

		$query="SELECT * FROM details_of_activity_located_in WHERE build_id = $build_id";
		$result=mysqli_query($connection, $query);
		if(mysqli_num_rows($result) > 0){
			deleteBuilding($build_id);
		}
		deleteLocatedAt($shop_name, $build_id);
		deleteRatings($shop_name, $build_id);
	}
	fclose($handle);
}
else{
	echo "todelete file not found";
}

function deleteDetailsOfActivity($shop_name, $build_id){
	global $connection;

	$query="DELETE FROM details_of_activity_located_in WHERE shop_name = '$shop_name' AND build_id = $build_id";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Delete details_of_activity failed' . mysqli_error($connection));
	}
}

function deleteBuilding($build_id){
	global $connection;

	$query="DELETE FROM building WHERE build_id = $build_id";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Delete building failed' . mysqli_error($connection));
	}
}

function deleteLocatedAt($shop_name, $build_id){
	global $connection;

	$query="DELETE FROM located_at WHERE shop_name = '$shop_name' AND build_id = $build_id";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Delete located_at failed' . mysqli_error($connection));
	}
}

function deleteRatings($shop_name, $build_id){
	global $connection;

	$query="SELECT rating_id FROM has_ratings WHERE shop_name = '$shop_name' AND build_id = $build_id";
	$result=mysqli_query($connection, $query);
	if(!$result){
		die('Select rating_id from has_ratings failed' . mysqli_error($connection));
	}
	while($row = mysqli_fetch_assoc($result)){
		$id = $row['rating_id'];
		$query="DELETE FROM has_ratings WHERE rating_id = $id";
		$result2=mysqli_query($connection, $query);
		if(!$result2){
			die('Delete rating_id from has_ratings failed' . mysqli_error($connection));
		}
		
		$query="DELETE FROM rating WHERE id = $id";
		$result2=mysqli_query($connection, $query);
		if(!$result2){
			die('Delete id from rating failed' . mysqli_error($connection));
		}
	}
}
?>