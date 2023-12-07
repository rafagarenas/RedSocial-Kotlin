<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userEmail = $_POST['userEmail'];
    $newUserProfilePicture = $_POST['userProfilePicture'];

    $queryUserIsASocialCenter = "SELECT * FROM social_centers WHERE sc_email = ?";
    $stmtUserIsASocialCenter = $dbConnection->prepareStatement($queryUserIsASocialCenter);
    $stmtUserIsASocialCenter->bind_param("s", $userEmail);
    $stmtUserIsASocialCenter->execute();
    $resultUserIsASocialCenter = $stmtUserIsASocialCenter->get_result();
    $stmtUserIsASocialCenter->close();

    if ($resultUserIsASocialCenter->num_rows > 0) {
        $accountData = $resultUserIsASocialCenter->fetch_assoc();
        $accountID = $accountData['id'];

        try{

            $queryChangeUserInformation = "UPDATE social_centers SET sc_picture = ? WHERE id = ?";
            $stmtChangeUserInformation = $dbConnection->prepareStatement($queryChangeUserInformation);
            $stmtChangeUserInformation->bind_param("ss", $newUserProfilePicture, $accountID);
    
            if ($stmtChangeUserInformation->execute()){
                $response['success'] = true;
                $response['message'] = "Cambios Aplicados Exitosamente.";
            }

        }catch(Exception $e){
            $response['success'] = false;
            $response['message'] = "Ha habido un Error: $e";
        }

    } else {
        $queryUserIsAnAltruistDonator = "SELECT * FROM altruist_donators WHERE ad_email = ?";
        $stmtUserIsAnAltruistDonator = $dbConnection->prepareStatement($queryUserIsAnAltruistDonator);
        $stmtUserIsAnAltruistDonator->bind_param("s", $userEmail);
        $stmtUserIsAnAltruistDonator->execute();
        $resultUserIsAnAltruistDonator = $stmtUserIsAnAltruistDonator->get_result();
        $stmtUserIsAnAltruistDonator->close();

        if ($resultUserIsAnAltruistDonator->num_rows > 0) {
            $accountData = $resultUserIsAnAltruistDonator->fetch_assoc();
            $accountID = $accountData['id'];
            $accountUserName = $accountData['ad_username'];

            try{
                $queryChangeUserInformation = "UPDATE altruist_donators SET ad_picture = ? WHERE id = ?";
                $stmtChangeUserInformation = $dbConnection->prepareStatement($queryChangeUserInformation);
                $stmtChangeUserInformation->bind_param("ss", $newUserProfilePicture, $accountID);
        
                if ($stmtChangeUserInformation->execute()){
                    $response['success'] = true;
                    $response['message'] = "Cambios Aplicados Exitosamente.";
                }
            } catch (Exception $e){
                $response['success'] = false;
                $response['message'] = "Ha habido un Error: $e";
            }
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
