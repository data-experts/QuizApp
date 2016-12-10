<?php

	include('./dbconfig.php');

	header('Content-Type: application/json;charset=utf-8');

	$dbconn = pg_connect($connstring)
		or die('{"error":"Connection failed: ' . pg_last_error() . '"}');

	$result = pg_query('SELECT * FROM "CATEGORIES" ORDER BY "ID" ASC')
		or die('{"error":"request failed: ' . pg_last_error() . '"}');

	echo json_encode(pg_fetch_all($result));

?>
