<?php
include "place_details.php";
?>

<?php
// retrieves all the relevant places and outputs into a file
// note that this can only generate up to 200 results
$file = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location=1.3521,103.8198&radius=25000&keyword=cinema&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";

	// read the webpage
$handle = file_get_contents($file);
$data = json_decode($handle, true);	

$results = $data['results'];

foreach($results as $value){
	getPlaceDetails($value['place_id']);
}

?>