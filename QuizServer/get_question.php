<?php

	include('./dbconfig.php');

	header('Content-Type: application/json;charset=utf-8');

	if (!isset($_GET['category'])) {
		die('{"error":"No category specified"}');
	}

	$dbconn = pg_connect($connstring)
		or die('{"error":"Connection failed: ' . pg_last_error() . '"}');

	$result = pg_query('SELECT * FROM "QUESTIONS" WHERE "CID"=' . $_GET['category'] . ' ORDER BY random() LIMIT 1')
		or die('{"error":"request failed: ' . pg_last_error() . '"}');

		$cleanResult = pg_fetch_assoc($result);

	if($cleanResult === false){
		echo '{"error": "no valid question"}';
	} else {
		echo json_encode($cleanResult, JSON_UNESCAPED_UNICODE);
	}

?>
