<?php

	include('./dbconfig.php');

	header('Content-Type: application/json;charset=utf-8');

	$dbconn = pg_connect($connstring)
		or die('{"error":"Connection failed: ' . pg_last_error() . '"}');

    $page = 0;
    
    // We want to set the offset page to the given value.
    if (! empty($_GET['page'])){
        $page = $_GET['page'] * 10;
    }
        
	$result = pg_query('SELECT * FROM "HIGHSCORES" ORDER BY "SCORE" DESC LIMIT 10 OFFSET ' . $page)
		or die('{"error":"request failed: ' . pg_last_error() . '"}');

	$return = pg_fetch_all($result);

	if (!$return) {
		die('{"error":"database error: ' . pg_last_error() . '"}');
	}

	echo json_encode($return);

?>