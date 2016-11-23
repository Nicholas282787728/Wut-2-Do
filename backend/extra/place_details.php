
<?php
//converts the place_id into the relevant details
function getPlaceDetails($placeid){

	$file = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ&placeid=" . $placeid;

	// read the webpage
	$handle = file_get_contents($file);
	$data = json_decode($handle, true);	

	$result = $data['result'];

	if($result["address_components"][2]["long_name"] == "Singapore"){
		echo $result['name'] . "<br>";
		echo $result['formatted_address'] . "<br><br>";
	}
}


?>