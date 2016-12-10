<?php

	include('./dbconfig.php');

	header('Content-Type: application/json;charset=utf-8');

	if (!isset($_GET['name']) || !isset($_GET['score'])) {
		die('{"error":"No name or score specified"}');
	}

	$dbconn = pg_connect($connstring)
		or die('{"error":"Connection failed: ' . pg_last_error() . '"}');

	$result = pg_query('INSERT INTO "HIGHSCORES" ("USERNAME", "SCORE") VALUES (\'' . $_GET['name'] . '\', ' . $_GET['score'] . ')')
		or die('{"error":"request failed: ' . pg_last_error() . '"}');

?>