<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $newUserEmail = $_POST['userEmail'];
    $newUserName = $_POST['userName'];
    $lastUserEmail = $_POST['lastUserEmail'];
    $pictureTrigger = $_POST['pictureTrigger'];

    $minUserNameLength = 6;

    if (empty($newUserEmail) || empty($newUserName)) {
        $response['success'] = false;
        $response['message'] = "El cambio no es correcto. Completa todos los campos.";
    } elseif (strlen($newUserName) < $minUserNameLength) {
        $response['success'] = false;
        $response['message'] = "El Nombre de Usuario debe tener al menos $minUserNameLength caracteres.";
    } elseif (!filter_var($newUserEmail, FILTER_VALIDATE_EMAIL)) {
        $response['success'] = false;
        $response['message'] = "El formato del correo electrónico no es válido.";
    } else {
        $queryUserIsASocialCenter = "SELECT * FROM social_centers WHERE sc_email = ?";
        $stmtUserIsASocialCenter = $dbConnection->prepareStatement($queryUserIsASocialCenter);
        $stmtUserIsASocialCenter->bind_param("s", $lastUserEmail);
        $stmtUserIsASocialCenter->execute();
        $resultUserIsASocialCenter = $stmtUserIsASocialCenter->get_result();
        $stmtUserIsASocialCenter->close();

        if ($resultUserIsASocialCenter->num_rows > 0) {
            $accountData = $resultUserIsASocialCenter->fetch_assoc();
            $accountID = $accountData['id'];
            $accountUserName = $accountData['sc_name'];

            if($newUserName == $accountUserName && $newUserEmail == $lastUserEmail){
                $response['success'] = false;
                $response['message'] = "No realizaste ningún cambio.";
            }else{
                try{

                    $queryChangeUserInformation = "UPDATE social_centers SET sc_email = ?, sc_name = ? WHERE sc_email = ?";
                    $stmtChangeUserInformation = $dbConnection->prepareStatement($queryChangeUserInformation);
                    $stmtChangeUserInformation->bind_param("sss", $newUserEmail, $newUserName, $lastUserEmail);
            
                    if ($stmtChangeUserInformation->execute()){
                        $response['success'] = true;
                        $response['message'] = "Cambios Aplicados Exitosamente.";
                    }

                }catch(Exception $e){
                    $response['success'] = false;
                    $response['message'] = "Ha habido un Error: $e";
                }
            }

        } else {
            $queryUserIsAnAltruistDonator = "SELECT * FROM altruist_donators WHERE ad_email = ?";
            $stmtUserIsAnAltruistDonator = $dbConnection->prepareStatement($queryUserIsAnAltruistDonator);
            $stmtUserIsAnAltruistDonator->bind_param("s", $lastUserEmail);
            $stmtUserIsAnAltruistDonator->execute();
            $resultUserIsAnAltruistDonator = $stmtUserIsAnAltruistDonator->get_result();
            $stmtUserIsAnAltruistDonator->close();

            if ($resultUserIsAnAltruistDonator->num_rows > 0) {
                $accountData = $resultUserIsAnAltruistDonator->fetch_assoc();
                $accountID = $accountData['id'];
                $accountUserName = $accountData['ad_username'];

                if($newUserName == $accountUserName && $newUserEmail == $lastUserEmail && $pictureTrigger == "false"){
                    $response['success'] = false;
                    $response['message'] = "No realizaste ningún cambio.";
                }else{
                    try{
                        $queryChangeUserInformation = "UPDATE altruist_donators SET ad_email = ?, ad_username = ? WHERE id = ?";
                        $stmtChangeUserInformation = $dbConnection->prepareStatement($queryChangeUserInformation);
                        $stmtChangeUserInformation->bind_param("sss", $newUserEmail, $newUserName, $accountID);
                
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
