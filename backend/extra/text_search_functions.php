<?php
// retrieves all the relevant places from the given file and outputs it into a text file
// note that this can only generate up to 60 results
function textSearch($file, &$allResults){
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
				array_push($allResults, $value['name'], $value['formatted_address']);
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
?>