<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userEmail = $_POST['userEmail'];

    if (empty($userEmail)) {
        $response['success'] = false;
        $response['message'] = "El usuario no inició sesión en la aplicación.";
    } else {
        $queryUserIsASocialCenter = "SELECT sc_name, sc_email, sc_register_date, sc_picture FROM social_centers WHERE sc_email = ?";
        $stmtUserIsASocialCenter = $dbConnection->prepareStatement($queryUserIsASocialCenter);
        $stmtUserIsASocialCenter->bind_param("s", $userEmail);
        $stmtUserIsASocialCenter->execute();
        $resultUserIsASocialCenter = $stmtUserIsASocialCenter->get_result();
        $stmtUserIsASocialCenter->close();

        if ($resultUserIsASocialCenter->num_rows > 0) {
            $dataArray = $resultUserIsASocialCenter->fetch_assoc();

            $formattedRegisterDate = date("d/m/y", strtotime($dataArray['sc_register_date']));

            $accountInformation = array(
                'username' => $dataArray['sc_name'],
                'email' => $dataArray['sc_email'],
                'register_date' => "Miembro de SolidarApp desde: $formattedRegisterDate",
                'account_type' => "Centro Social",
                'picture' => $dataArray['sc_picture']
            );

            $response['success'] = true;
            $response['message'] = "El Usuario es un Centro Social.";
            $response['accountInformation'] = $accountInformation;
        } else {
            $queryUserIsAnAltruistDonator = "SELECT ad_username, ad_email, ad_register_date, ad_picture FROM altruist_donators WHERE ad_email = ?";
            $stmtUserIsAnAltruistDonator = $dbConnection->prepareStatement($queryUserIsAnAltruistDonator);
            $stmtUserIsAnAltruistDonator->bind_param("s", $userEmail);
            $stmtUserIsAnAltruistDonator->execute();
            $resultUserIsAnAltruistDonator = $stmtUserIsAnAltruistDonator->get_result();
            $stmtUserIsAnAltruistDonator->close();

            if ($resultUserIsAnAltruistDonator->num_rows > 0) {
                $dataArray = $resultUserIsAnAltruistDonator->fetch_assoc();

                $formattedRegisterDate = date("d/m/y", strtotime($dataArray['ad_register_date']));

                $accountInformation = array(
                    'username' => $dataArray['ad_username'],
                    'email' => $dataArray['ad_email'],
                    'register_date' => "Miembro de SolidarApp desde $formattedRegisterDate.",
                    'account_type' => "Donador Altruista",
                    'picture' => $dataArray['ad_picture']
                );

                $response['success'] = true;
                $response['message'] = "El Usuario es un Donador Altruista.";
                $response['accountInformation'] = $accountInformation;
            }
        }
    }
} else {
    http_response_code(405);
    $response['success'] = false;
    $response['message'] = "Error: Método no permitido.";
}

// Devolver la respuesta como JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
