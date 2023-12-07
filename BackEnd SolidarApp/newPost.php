<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $newPostDescription = $_POST['newPostDescription'];
    $newPostPicture = $_POST['newPostPicture'];
    $newPostOwner = $_POST['newPostOwner'];
    $newPostLikes = 0;

    if(empty($newPostDescription)){
        $response['success'] = false;
        $response['message'] = "Añade una descripción a tu publicación.";
    } else {

        $sql = "INSERT INTO app_posts (post_description, post_picture, post_owner, post_likes_count) VALUES (?, ?, ?, ?)";
        $stmtInsert = $dbConnection->prepareStatement($sql);
        $stmtInsert->bind_param("ssss", $newPostDescription, $newPostPicture, $newPostOwner, $newPostLikes);

        if ($stmtInsert->execute()) {
            $response['success'] = true;
            $response['message'] = "Publicación Realizada exitosamente.";
        } else {
            $response['success'] = false;
            $response['message'] = "Fallo al realizar tu Publicación: " . $stmtInsert->error;
        }

        $stmtInsert->close();

    }

} else {
    http_response_code(405);
    $response['success'] = false;
    $response['message'] = "Error: Método no permitido.";
}

$dbConnection->closeConnection();

header('Content-Type: application/json');
echo json_encode($response);
?>
