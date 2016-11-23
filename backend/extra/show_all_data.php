<?php
include "db_online_backup.php";
?>
<?php
// array for JSON response
$response = array();

global $connection;
$query="SELECT * FROM details_of_activity_located_in";
$result = mysqli_query($connection, $query);

if(!$result){
	die('Query FAILED' . mysqli_error($connection));
}

while($row = mysqli_fetch_assoc($result)){
	$location = array();
	$location['shop_name'] = $row['shop_name'];
	$location['unit_no'] = $row['unit_no'];
	$location['build_id'] = $row['build_id'];

	array_push($response, $location);
}
$json = json_encode($response);
echo $json;
?>