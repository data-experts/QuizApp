<?php

	include('./dbconfig.php');

	header('Content-Type: application/json;charset=utf-8');

	if (!isset($_GET['category'])) {
		die('{"error":"No category specified"}');
	}

    if (!isset($_GET['ignored'])) {
        die('{"error":"No questions to ignore"}');
    }

	$dbconn = pg_connect($connstring)
		or die('{"error":"Connection failed: ' . pg_last_error() . '"}');

    $ignored = $_GET['ignored'];

	$result = pg_query('SELECT * FROM "QUESTIONS" WHERE "CID"=' . intval($_GET['category']) . ' AND "ID" NOT IN ('.to_pg_array($ignored).') ORDER BY random() LIMIT 1')
		or die('{"error":"request failed: ' . pg_last_error() . '"}');

	$cleanResult = pg_fetch_assoc($result);

	if($cleanResult === false){
		include('./get_question.php');
	} else {
		echo json_encode($cleanResult, JSON_UNESCAPED_UNICODE);
	}

    function to_pg_array($arr) {
	$result = '';
	$arr = explode(',', $arr);
       for ($i = 0; $i < sizeof($arr); $i++) {
        	$result = $result . intval($arr[$i]);
		if ($i != sizeof($arr) - 1 ) {
			$result = $result . ',';
		}
	}
       return $result;
    }

?>
