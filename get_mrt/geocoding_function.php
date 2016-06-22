
<?php
// converts the given address to latitude and longtitude.
function geocode($link, $file){
	// reset the timer as timer times out in 30s
	set_time_limit(30);
	
	// read the webpage
	$handle = file_get_contents($link);
	$data = json_decode($handle, true);	

	$results = $data['results'][0];
	$location = $results['geometry']['location'];
	$latLng = $location['lat'] . ',' . $location['lng'] . "\n";
	file_put_contents($file, $latLng, FILE_APPEND);

	// code to check whether the location is in Singapore.
	// for data verification
/*	if(stristr($results['formatted_address'], "Singapore") == FALSE){
		echo "No Singapore<br>";
	}
	else{
		echo "Singapore found<br>";
	} */
}
?>