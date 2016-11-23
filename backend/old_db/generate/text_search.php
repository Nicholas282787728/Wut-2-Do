<?php
include "../db_online_backup.php";

/* pre: receives the category name to be searched
post: returns the results of locations
this is the main function that retrieves the text search link and passes it to text_search_function to retrieve the results */
function textSearch($category){
	// the file that contains latitude and longitude of MRT stations
	$fileLatLong = "../text_files/latlong.txt";
//	$fileOutput = "../../text_files/results_boardgames.txt";
	$allResults = array();
	if($handle = fopen($fileLatLong, 'r')){
		while(!feof($handle)){
			foreach($category as $keyword){
				set_time_limit(30);
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
		echo "lat_long file not found";
	}
}

// for reference
//$link = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=kbox+Singapore&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
//textSearch($link, $allResults);
//print_r($allResults);

// to check through searches that are likely to return < 20 results.
function textSearchShort($category){
	$allResults = array();
		foreach($category as $keyword){
			set_time_limit(30);
			$link = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" . str_replace(' ', '+', $keyword) .
				"+Singapore&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
			textSearchAPIShort($link, $allResults);
		}
	return $allResults;
}

function textSearchAPIShort($link, &$allResults){
	global $connection;
	// read the webpage
	$handle = file_get_contents($link);
	$data = json_decode($handle, true);	
	$results = $data['results'];

	// loop through the 'results' array
	foreach($results as $value){
		// to reset the time limit of 30s, so that program doesn't cut halfway.
		set_time_limit(30);
		// if the address is in singapore, and if the location is not closed.
		if((stristr($value['formatted_address'], "Singapore") != FALSE) && !array_key_exists('permanently_closed', $value)){
			// pass them to various helper functions to retrieve postal code and unit no.
			// $details[0] = reviews, [1] = phone_num, [2] = website.
			// $details[0][1] = array of results, $details[0][2] = avg rating score, $details[0][3] = num reviews 
			$postal_code = findPostal($value['formatted_address']);
			$unit_no = findUnit($value['formatted_address']);
			$lat_long = findCoordinates($postal_code);
			$details = findDetails($value['place_id']);
			array_push($allResults, $value['name'], $value['formatted_address'], $postal_code, $unit_no, $details[1], $details[2], $lat_long, $details[0][0], $details[0][1], $details[0][2]);
		}
	}
}

/* pre: receives website link and array
post: stores the array with results generated
helper class that retrieves all the relevant places from the given link and outputs it into a text file
note that this can only generate up to 60 results */
function textSearchAPI($link, &$allResults){
	$loop = true;
	$loopedOnce = false;

	while($loop){
		$loop = false;
		// read the webpage
		$handle = file_get_contents($link);
		$data = json_decode($handle, true);	
		$results = $data['results'];

		// loop through the 'results' array
		foreach($results as $value){
			// to reset the time limit of 30s, so that program doesn't cut halfway.
			set_time_limit(30);
			// if the address is in singapore, and if the location is not closed.
			if((stristr($value['formatted_address'], "Singapore") != FALSE) && !array_key_exists('permanently_closed', $value)){
				// pass them to various helper functions to retrieve postal code and unit no.
				// $details[0] = reviews, [1] = phone_num, [2] = website.
				// $details[0][1] = array of results, $details[0][2] = avg rating score, $details[0][3] = num reviews 
				$postal_code = findPostal($value['formatted_address']);
				$unit_no = findUnit($value['formatted_address']);
				$lat_long = findCoordinates($postal_code);
				$details = findDetails($value['place_id']);
				array_push($allResults, $value['name'], $value['formatted_address'], $postal_code, $unit_no, $details[1], $details[2], $lat_long, $details[0][0], $details[0][1], $details[0][2]);
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

/* pre: receives address
post: returns postal code 
helper method*/
function findPostal($address){
	$subject = "$address";
	$pattern = "/[0-9]{6}/";
	if(preg_match($pattern, $subject, $matches) == 1)
		return $matches[0];
	else
		return "";
}

/* pre: receives address
post: returns unit no
helper method*/
function findUnit($address){
	$pattern = "/[0-9][0-9]-[0-9][0-9]/";
	if(preg_match($pattern, $address, $matches, PREG_OFFSET_CAPTURE) == 1){
		// move the index to the index after the last digit matched
		$index = $matches[0][1] + 5;
		// length of current string is 5
		$length = 5;
		// while the index isn't a ' ' (blank space indicating end of postal code)
		while($address{$index} != ' '){
			$index++;
			$length++;
		}
		// sometimes address may be "#08-48, Singapore...". So, subtract index and length
		if($address{$index-1} == ','){
			$index--;
			$length--;
		}
		return substr($address, $matches[0][1], $length);
	}
	else
		return "";
}

/* pre: receives postal code
post: returns latitude and longitude
helper method*/
function findCoordinates($postal_code){
	$link = "https://maps.googleapis.com/maps/api/geocode/json?address=" . "$postal_code" . "&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ&components=country:SG";
	$handle = file_get_contents($link);
	$data = json_decode($handle, true);	

	$results = $data['results'][0];
	$location = $results['geometry']['location'];
	$lat_long = $location['lat'] . ',' . $location['lng'] . "\n";
	
	return $lat_long;	
}

/* pre: receives place_id
post: returns an array containing reviews, phone number, website using Place Details API
helper method*/
function findDetails($place_id){
	$link = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" . "$place_id" . "&key=AIzaSyDNXW5YCSJ31sIW1rxg8SW77BQ_b89I6qQ";
	$handle = file_get_contents($link);
	$data = json_decode($handle, true);	
	$result = $data['result'];
	// gets reviews
	if(array_key_exists('reviews', $result))
		$reviews = findReviews($result['reviews']);
	else
		$reviews = array(null, "0", 0);

	// gets phone number
	if(array_key_exists('formatted_phone_number', $result))
		$phone_num = $result['formatted_phone_number'];
	else
		$phone_num = "";

	// gets website
	if(array_key_exists('website', $result))
		$website = $result['website'];
	else
		$website = "";

	return array($reviews, $phone_num, $website);
}

/* pre: receives reviews array
post: returns an array where [0] contains all reviews (name, num_stars, text, date), [1] contains avg review score, [2] contains num_reviews
helper method*/
function findReviews($reviews){
	$conciseResults = array(array());
	$i = 0;
	$repeatReview = false;
	$totalScore = 0;

	// loop through all reviews written
	foreach($reviews as $review){
		// loop through array to check for repeats. if repeated, don't put in array.
		foreach($conciseResults as $result){
			if(in_array($review['text'], $result)){
				$repeatReview = true;
				break;
			}
		}
		// if not repeated
		if($repeatReview == false){
			$conciseResults[$i]['name'] = $review['author_name'];
			$conciseResults[$i]['num_stars'] = $review['rating'];
			$conciseResults[$i]['text'] = $review['text'];
			$conciseResults[$i]['date'] = date('Y-m-d', $review['time']);
			$totalScore += intval($review['rating']);
			$i++;
		}
		$repeatReview = false;
	}
	return array($conciseResults, $totalScore/$i, $i);	
}
?>