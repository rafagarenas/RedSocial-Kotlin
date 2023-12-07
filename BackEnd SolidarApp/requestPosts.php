<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $queryRequestPosts = "SELECT * FROM app_posts";
    $stmtRequestPosts = $dbConnection->prepareStatement($queryRequestPosts);
    $stmtRequestPosts->execute();
    $resultRequestPosts = $stmtRequestPosts->get_result();
    $stmtRequestPosts->close();

    if ($resultRequestPosts->num_rows > 0) {

        $postsList = array();
    
        setlocale(LC_TIME, 'spanish');

        while ($row = mysqli_fetch_assoc($resultRequestPosts)) {

            $postOwner = $row['post_owner'];
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

            $queryRequestPostUserInformation = "SELECT ad_username, ad_picture FROM altruist_donators WHERE ad_email = ?";
            $stmtRequestPostUserInformation = $dbConnection->prepareStatement($queryRequestPostUserInformation);
            $stmtRequestPostUserInformation->bind_param("s", $postOwner);
            $stmtRequestPostUserInformation->execute();
            $resultRequestPostUserInformation = $stmtRequestPostUserInformation->get_result();
            $stmtRequestPostUserInformation->close();
    
            if ($resultRequestPostUserInformation->num_rows > 0) {
                $accountData = $resultRequestPostUserInformation->fetch_assoc();
                $row['post_user_profile_picture'] = $accountData['ad_picture'];
                $row['post_user_name'] = $accountData['ad_username'];
            } else {
                $queryRequestPostUserInformationSocialCenter = "SELECT sc_name, sc_picture FROM social_centers WHERE sc_email = ?";
                $stmtRequestPostUserInformationSocialCenter = $dbConnection->prepareStatement($queryRequestPostUserInformationSocialCenter);
                $stmtRequestPostUserInformationSocialCenter->bind_param("s", $postOwner);
                $stmtRequestPostUserInformationSocialCenter->execute();
                $resultRequestPostUserInformationSocialCenter = $stmtRequestPostUserInformationSocialCenter->get_result();
                $stmtRequestPostUserInformationSocialCenter->close();

                if ($resultRequestPostUserInformationSocialCenter->num_rows > 0) {
                    $accountData = $resultRequestPostUserInformationSocialCenter->fetch_assoc();
                    $row['post_user_profile_picture'] = $accountData['sc_picture'];
                    $row['post_user_name'] = $accountData['sc_name'];
                }
            }

            $postsList[] = $row;
        }

        $postsList = array_reverse($postsList);

        setlocale(LC_TIME, '');

        $response['success'] = true;
        $response['message'] = "Lista Recuperada.";
        $response['posts'] = $postsList;
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
