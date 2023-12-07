<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $filterBy = $_POST['filterBy'];

    if($filterBy == 'Social Center'){
        $queryFilterPosts = "SELECT sc_email FROM social_centers";
        $stmtFilterPosts = $dbConnection->prepareStatement($queryFilterPosts);
        $stmtFilterPosts->execute();
        $resultFilterPosts = $stmtFilterPosts->get_result();
        $stmtFilterPosts->close();

        $emails = [];
        while ($row = $resultFilterPosts->fetch_assoc()) {
            $emails[] = $row['sc_email'];
        }

        if (!empty($emails)) {
            $whereClause = "post_owner IN ('" . implode("','", $emails) . "')";

            $queryRequestPosts = "SELECT * FROM app_posts WHERE $whereClause";
            $stmtRequestPosts = $dbConnection->prepareStatement($queryRequestPosts);
            $stmtRequestPosts->execute();
            $resultRequestPosts = $stmtRequestPosts->get_result();
            $stmtRequestPosts->close();

            if ($resultRequestPosts->num_rows > 0) {

                $postsList = array();
    
                setlocale(LC_TIME, 'spanish');
    
                while ($row = mysqli_fetch_assoc($resultRequestPosts)) {
                    $postOwner = $row['post_owner'];

                    $queryAccountInformation = "SELECT sc_picture, sc_name FROM social_centers WHERE sc_email = ?";
                    $stmtAccountInformation = $dbConnection->prepareStatement($queryAccountInformation);
                    $stmtAccountInformation->bind_param("s", $postOwner);
                    $stmtAccountInformation->execute();
                    $resultAccountInformation = $stmtAccountInformation->get_result();
                    $stmtAccountInformation->close();

                    if ($resultAccountInformation->num_rows > 0) {
                        $accountData = $resultAccountInformation->fetch_assoc();
                        $row['post_user_profile_picture'] = $accountData['sc_picture'];
                        $row['post_user_name'] = $accountData['sc_name'];
                    }
                    
                    $postDate = $row['post_date'];
    
                    $postDateParsed = strftime("%d de %B del %Y a las %I:%M %p", strtotime($postDate));
    
                    $row['post_date'] = $postDateParsed;
    
                    $postLikes = $row['post_likes_count'];

                    if ($postLikes > 1){
                        $postLikesParsed = "A $postLikes personas les gusta esto.";
                    } elseif ($postLikes == 0){
                        $postLikesParsed = "Esta Publicación no tiene reacciones.";
                    } elseif ($postLikes == 1){
                        $postLikesParsed = "A 1 persona le gusta esto.";
                    }
                    
                    $row['post_likes_count'] = $postLikesParsed;
    
                    $postsList[] = $row;
                }
    
                $postsList = array_reverse($postsList);
    
                setlocale(LC_TIME, '');
    
                $response['success'] = true;
                $response['message'] = "Mostrando Publicaciones realizadas por Centros Sociales.";
                $response['posts'] = $postsList;
    
            } else {
                $response['success'] = true;
                $response['message'] = "No hay publicaciones realizadas por Centros Sociales.";
            }
        } else {
            echo "No hay Centros Sociales Registrados para filtrar.";
        }
    }elseif($filterBy == 'Altruist Donator'){
        $queryFilterPosts = "SELECT ad_email FROM altruist_donators";
        $stmtFilterPosts = $dbConnection->prepareStatement($queryFilterPosts);
        $stmtFilterPosts->execute();
        $resultFilterPosts = $stmtFilterPosts->get_result();
        $stmtFilterPosts->close();

        $emails = [];
        while ($row = $resultFilterPosts->fetch_assoc()) {
            $emails[] = $row['ad_email'];
        }

        if (!empty($emails)) {
            $whereClause = "post_owner IN ('" . implode("','", $emails) . "')";

            $queryRequestPosts = "SELECT * FROM app_posts WHERE $whereClause";
            $stmtRequestPosts = $dbConnection->prepareStatement($queryRequestPosts);
            $stmtRequestPosts->execute();
            $resultRequestPosts = $stmtRequestPosts->get_result();
            $stmtRequestPosts->close();

            if ($resultRequestPosts->num_rows > 0) {

                $postsList = array();
    
                setlocale(LC_TIME, 'spanish');
    
                while ($row = mysqli_fetch_assoc($resultRequestPosts)) {
                    $postOwner = $row['post_owner'];

                    $queryAccountInformation = "SELECT ad_picture, ad_username FROM altruist_donators WHERE ad_email = ?";
                    $stmtAccountInformation = $dbConnection->prepareStatement($queryAccountInformation);
                    $stmtAccountInformation->bind_param("s", $postOwner);
                    $stmtAccountInformation->execute();
                    $resultAccountInformation = $stmtAccountInformation->get_result();
                    $stmtAccountInformation->close();

                    if ($resultAccountInformation->num_rows > 0) {
                        $accountData = $resultAccountInformation->fetch_assoc();
                        $row['post_user_profile_picture'] = $accountData['ad_picture'];
                        $row['post_user_name'] = $accountData['ad_username'];
                    }
                    
                    $postDate = $row['post_date'];
    
                    $postDateParsed = strftime("%d de %B del %Y a las %I:%M %p", strtotime($postDate));
    
                    $row['post_date'] = $postDateParsed;
    
                    $postLikes = $row['post_likes_count'];

                    if ($postLikes > 1){
                        $postLikesParsed = "A $postLikes personas les gusta esto.";
                    } elseif ($postLikes == 0){
                        $postLikesParsed = "Esta Publicación no tiene reacciones.";
                    } elseif ($postLikes == 1){
                        $postLikesParsed = "A 1 persona le gusta esto.";
                    }
                    
                    $row['post_likes_count'] = $postLikesParsed;
    
                    $postsList[] = $row;
                }
    
                $postsList = array_reverse($postsList);
    
                setlocale(LC_TIME, '');
    
                $response['success'] = true;
                $response['message'] = "Mostrando Publicaciones realizadas por Donadores Altruistas.";
                $response['posts'] = $postsList;
    
            } else {
                $response['success'] = false;
                $response['message'] = "No hay publicaciones realizadas por Donadores Altruistas.";
            }

        } else {
            echo "No hay Donadores Altruistas Registrados para filtrar.";
        }
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
