<?php
// this is the main class that retrieves the text search link and passes it to text_search_function to retrieve the results
function textSearch($category){
	$fileLatLong = "../../text_files/latlong.txt";
//	$fileOutput = "../../text_files/results_boardgames.txt";
	$allResults = array();
	if($handle = fopen($fileLatLong, 'r')){
		while(!feof($handle)){
			foreach($category as $keyword){
				$link = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" . str_replace(' ', '+', $keyword) .
				"+Singapore&location=" . trim(fgets($handle)) . "&radius=1500&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
				textSearchAPI($link, $allResults);
			}
		}

		fclose($handle);

/*		file_put_contents($fileOutput, $allResults[0], FILE_APPEND);
		$array_size = count($allResults);
		for($i=1; $i<$array_size; $i++){
			file_put_contents($fileOutput, "\n" . $allResults[i], FILE_APPEND);
		}	*/
		return $allResults;
	}
	else{
		echo "File not found";
	}
}

// for reference
//$link = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=kbox+Singapore&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
//textSearch($link, $allResults);
//print_r($allResults);


// helper class that retrieves all the relevant places from the given file and outputs it into a text file
// note that this can only generate up to 60 results
function textSearchAPI($file, &$allResults){
	set_time_limit(30);
	$loop = true;
	$loopedOnce = false;

	while($loop){
		$loop = false;

	// read the webpage
		$handle = file_get_contents($file);
		$data = json_decode($handle, true);	

		$results = $data['results'];

	// loop through the 'results' array
		foreach($results as $value){
			// if the address is in singapore, and if the address is not a duplicate, and if the location is not closed.
			if((stristr($value['formatted_address'], "Singapore") != FALSE) && !in_array($value['formatted_address'], $allResults) && !array_key_exists('permanently_closed', $value)){
				// pass them to various functions to retrieve postal code and unit no.
				$postal_code = findPostal($value['formatted_address']);
				$unit_no = findUnit($value['formatted_address']);
				$lat_long = findCoordinates($postal_code);
				array_push($allResults, $value['name'], $value['formatted_address'], $postal_code, $unit_no, $lat_long);
			}
		}

/*		sleep(2);

	// if there exists a next page
		if(array_key_exists('next_page_token', $data)){
		// if first loop
			if(!$loopedOnce){
				$file .= "&pagetoken=";
				$file .= $data["next_page_token"];
				$loop = true;
				$loopedOnce = true;
			}
		// if second loop
			else{
				$pos = strpos($file, "&pagetoken=") + 11;
				$file = substr($file, 0, $pos);
				$file .= $data["next_page_token"];
				$loop = true;
			}
		}*/
	}
}

// returns postal code from the given address
function findPostal($address){
	$subject = "$address";
	$pattern = "/[0-9]{6}/";
	if(preg_match($pattern, $subject, $matches) == 1)
		return $matches[0];
	else
		return "";
}

// returns unit no from the given address
function findUnit($address){
	$pattern = "/[0-9][0-9]-[0-9][0-9]/";
	if(preg_match($pattern, $address, $matches, PREG_OFFSET_CAPTURE) == 1){
		// move the index to the index after the last digit matched
		$index = $matches[0][1] + 5;
		// length of current string is 5
		$length = 5;
		// while the index isn't a ' '
		while($address{$index} != ' '){
			$index++;
			$length++;
		}
		if($address{$index-1} == ','){
			$index--;
			$length--;
		}
		return substr($address, $matches[0][1], $length);
	}
	else
		return "";
}

// returns coordinates from the given postal code
function findCoordinates($postal_code){
	$link = "https://maps.googleapis.com/maps/api/geocode/json?address=" . "$postal_code" . "&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ&components=country:SG";
	$handle = file_get_contents($link);
	$data = json_decode($handle, true);	

	$results = $data['results'][0];
	$location = $results['geometry']['location'];
	$lat_long = $location['lat'] . ',' . $location['lng'] . "\n";
	
	return $lat_long;	
}
?>