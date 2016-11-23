<?php
include "../db_online.php";

global $connection;
$shop_name = "Encounter - Singapore's Real-life Suspense Game";
$shop_name = mysqli_real_escape_string($connection, $shop_name);
$unit_no = '';
$build_id = 219;

$query="SELECT shop_name, tel_num, website, reviews_avg FROM details_of_activity_located_in WHERE shop_name = '$shop_name' AND unit_no = '$unit_no' AND build_id = $build_id";
$result2=mysqli_query($connection, $query);
echo $shop_name . " " . $unit_no . " " . $build_id . "<br>";
// there is only 1 row since the combination of (shop_name, unit_no, build_id) is unique 
$row2=mysqli_fetch_assoc($result2);
echo $row2['shop_name'] . "<br>";
?>