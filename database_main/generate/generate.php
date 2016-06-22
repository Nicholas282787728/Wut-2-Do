<?php 
/* begins with text_search.php to generate all results for a particular category, which can have different names i.e cybercafe + lan shops
then insert_database.php to insert the results into database tables
*/

include "text_search.php";
include "insert_database.php";
?>

<?php
$genres = array( 
/*	"Movie" => array (
		"Cinema" => array(
			"Cinema"
			),
		),*/
	"Sports" => array (
		"Gym" => array(
			"Gym"
			),
		"Swim" => array(
			"Swim"
			),
		"Bowling" => array(
			"Bowling"
			),
		"Snorkel" => array(
			"Snorkel"
			),
		),
/*	"Games" => array (
		"Cybercafe" => array(
			"Cybercafe", "Lan Shop"
			),
		"Board Games" => array(
			"Board Games"
			),
		),*/
	"Puzzle" => array (
		"Escape Room" => array(
			"Escape Room"
			),
		),
	"Music" => array (
		"Karaoke" => array(
			"Karaoke"
			),
		)
	);

foreach($genres as $genre => $categories){
	$gen_ID = createGenre($genre);
	foreach($categories as $category => $keywords){
		// pass an array $category which contains keywords to search
		$results = textSearch($keywords);
		insertDatabase($results, $gen_ID, $category);
	}
}
?>