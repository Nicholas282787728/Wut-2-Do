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
	"Movie" => array (
		"Cinema" => array(
			"Shaw", "Cathay", "Golden Village", "Cinema"
			),
		),
	"Sports" => array (
		"Gym" => array(
			"Gym", "ActiveSG Gym", "Fitness First", "Anytime Fitness"
			),
		"Swim" => array(
			"Ang Mo Kio Swimming Complex", "Bedok Swimming Complex", "Bishan Swimming Complex", "Bukit Batok Swimming Complex", "Bukit Panjang Swimming Complex", "Buona Vista Swimming Complex", "CCA Branch Swimming Complex", "Clementi Swimming Complex", "Choa Chu Kang Swimming Complex", "Delta Swimming Complex", "Geylang East Swimming Complex", "Hougang Swimming Complex", "Jalan Besar Swimming Complex", "Jurong East Swimming Complex", "Jurong West Swimming Complex", "Kallang Basin Swimming Complex", "Katong Swimming Complex", "Queenstown Swimming Complex", "Pasir Ris Swimming Complex", "Sengkang Swimming Complex", "Serangoon Swimming Complex", "Tampines Swimming Complex", "Toa Payoh Swimming Complex", "Woodlands Swimming Complex", "Yio Chu Kang Swimming Complex", "Yishun Swimming Complex"
			),
		"Bowling" => array(
			"Bowling", "Orchid Bowl", "Super Bowl", "Kallang Bowl"
			),
		"Badminton" => array(
			"Badminton", "Sports Hall", "Sports Centre"
			),
		"Laser Tag" => array(
			"Laser Tag", "Laser Quest", "Combat Skirmish", "Battle"
			),
		"Paintball" => array(
			"Paintball"
			),
		),
	"Games" => array (
		"Game Cafe" => array(
			"Console Game Cafe", "Cybercafe", "Lan Shop", "Colosseum"
			),
		"Board Games" => array(
			"Board Games"
			),
		"Arcade" => array(
			"Time Zone", "Zone X"
			),
		),
	"Puzzle" => array (
		"Escape Room" => array(
			"Escape Room", "Breakout Escape", "Captivate Escape", "Encounter", "Escape Hunt", "Exit Plan", "U Escape", "Unravel", "Room Raider", "Lost SG", "Freeing SG"
			),
		),
	"Music" => array (
		"Karaoke" => array(
			"Karaoke", "Kbox", "Teo Heng", "KTV", "Ten Dollar Club" 
			),
		),

	"History" => array (
		"Museum" => array(
			"Art Museum", "SAM 8Q", "Asian Civilisations Museum", "National Gallery", "National Museum", "Peranakan Museum", "Lee Kong Chian Museum", "NUS Museum"
			),
		"Heritage" => array(
			"Malay Heritage", "National Archives", "Reflections Bukit Chandu", "City Gallery", "Sun Yat Sen"
			)
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