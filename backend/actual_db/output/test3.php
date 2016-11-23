<?php
include "../db_online.php";

global $connection;
$build_id = 10;

$query="SELECT shop_name FROM has_ratings WHERE rating_id = 219";
$result=mysqli_query($connection, $query);
$row=mysqli_fetch_assoc($result);
$firstName = trim(mysqli_real_escape_string($connection, $row['shop_name']));

$query="SELECT shop_name FROM has_ratings WHERE rating_id = 269";
$result=mysqli_query($connection, $query);
$row=mysqli_fetch_assoc($result);
$secondName = trim(mysqli_real_escape_string($connection, $row['shop_name']));
$secondName = str_replace(array('\r', '\n'), "", $secondName);

echo strcmp(strtolower($firstName), strtolower($secondName)) . "<br>";
echo $firstName . "<br>"; 
echo $secondName . "<br>";
?>