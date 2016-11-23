<?php 
/* this file is the main function.
Running this file will run text_search.php to generate all results for a particular category, which can have different names i.e cybercafe + lan shops so as to generate more results.
These results will be passed to insert_database.php to insert the results into database tables.
*/

include "text_search.php";
include "insert_database.php";
?>

<?php
$genres = array( 
	"Nature" => array (
		"Park" => array(
			"Park", "Nature Reserve", "Labrador Nature Reserve", "East Coast Park", "Fort Canning Park", "Gardens By The Bay", "MacRitchie Reservoir", "Mount Faber Park", "Botanic Gardens", "Henderson Waves", "Jurong Lake Park", "Pasir Ris Park", "Punggol Waterway Park"
			),
		"Beach" => array(
			" Beach", "Siloso Beach", "East Coast Park", "Pasir Ris Park", "Lazarus Island", "Kusu Island", "Changi Beach Park", "St John Island"
			),
		"Zoo" => array(
			"Zoo", "River Safari", "Night Safari", "Jurong Bird Park"
			),
		"Aquarium" => array(
			"S.E.A Aquarium"
			),
		),
	"Games" => array (
		"Theme Park" => array(
			"Theme Park", "Waterpark", "Aquatic Centre", "Wild Wild Wet", "Port of Lost Wonder", "Kidz Amaze", "Rainforest Kidzworld", "Birds of Play", "Adventure Water Cove", "Universal Studios"
			)
		)
	);

foreach($genres as $genre => $categories){
	$gen_ID = createGenre($genre);
	foreach($categories as $category => $keywords){
		// pass an array $category which contains keywords to search
		$results = textSearchShort($keywords);
		insertDatabase($results, $gen_ID, $category);
	}
}
?>