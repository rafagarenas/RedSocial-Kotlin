<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['postId'];
    $likeAccount = $_POST['likeAccount'];

    try{
        $queryCheckLikesFromPost = "SELECT post_likes_count, post_liked_by FROM app_posts WHERE id = ?";
        $stmtCheckLikesFromPost = $dbConnection->prepareStatement($queryCheckLikesFromPost);
        $stmtCheckLikesFromPost->bind_param("i", $postId);
        $stmtCheckLikesFromPost->execute();
        $resultCheckLikesFromPost = $stmtCheckLikesFromPost->get_result();
        

        $row = $resultCheckLikesFromPost->fetch_assoc();

    }catch(Exception $e){
        $response['success'] = false;
        $response['message'] = "Hubo un error, favor de contactar al Administrador.";
    }

    if ($resultCheckLikesFromPost->num_rows > 0) {
        $postLikesCount = $row['post_likes_count'];
        $postLikesCount--;

        $postLikedBy = $row['post_liked_by'];
        $postLikedBy = str_replace($likeAccount, "", $postLikedBy);
    }

    $stmtCheckLikesFromPost->close();

    try{
        $queryUpdateLikesFromPost = "UPDATE app_posts SET post_likes_count = ?, post_liked_by = ? WHERE id = ?";
        $stmtUpdateLikesFromPost = $dbConnection->prepareStatement($queryUpdateLikesFromPost);
        $stmtUpdateLikesFromPost->bind_param("isi", $postLikesCount, $postLikedBy, $postId);
        $stmtUpdateLikesFromPost->execute();

    } catch (Exception $e){
        $response['success'] = false;
        $response['message'] = "Hubo un error, favor de contactar al Administrador.";
    } finally {
        $stmtUpdateLikesFromPost->close();

        $response['success'] = true;
        $response['message'] = "Ya no te gusta esta publicación.";
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