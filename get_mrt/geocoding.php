<?php
include "geocoding_function.php";
?>

<?php
// this is the main class that converts the locations of MRT stations into coordinates
$file = "mrt_stations_updated.txt";
$fileOutput = "latlong.txt";
//$i = 1;
if($handle = fopen($file, 'r')){
	while(!feof($handle)){
		// + to indicate space, already done when generating mrt_stations.txt
		$link = "https://maps.googleapis.com/maps/api/geocode/json?address=" . trim(fgets($handle)) . "&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ&components=country:SG";
//		echo $i . "<br>";
		geocode($link, $fileOutput);
//		$i++;
	}		
	fclose($handle);
}
else{
	echo "File not found";
}
?>