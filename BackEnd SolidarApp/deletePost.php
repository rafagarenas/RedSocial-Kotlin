<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postId = $_POST['postId'];

    try{
        $queryDeletePost = "DELETE FROM app_posts WHERE id = ?";
        $stmtDeletePost = $dbConnection->prepareStatement($queryDeletePost);
        $stmtDeletePost->bind_param("i", $postId);
        $stmtDeletePost->execute();

    }catch(Exception $e){
        $response['success'] = false;
        $response['message'] = "Hubo un error, favor de contactar al Administrador.";
    }

    if ($stmtDeletePost->affected_rows > 0) {
        $response['success'] = true;
        $response['message'] = "Publicación Eliminada Exitosamente.";
    } else {
        $response['success'] = false;
        $response['message'] = "No se pudo eliminar la Publicación.";
    }

    $stmtDeletePost->close();
    $dbConnection->closeConnection();

} else {
    http_response_code(405);
    $response['success'] = false;
    $response['message'] = "Error: Método no permitido.";
}

header('Content-Type: application/json');
echo json_encode($response);
?>