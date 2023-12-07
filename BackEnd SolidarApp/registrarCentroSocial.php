<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $socialCenterName = $_POST['socialCenterName'];
    $socialCenterEmail = $_POST['socialCenterEmail'];
    $socialCenterPassword = $_POST['socialCenterPassword'];
    $socialCenterConfirmPassword = $_POST['socialCenterConfirmPassword'];

    $socialCenterStreet = $_POST['socialCenterStreet'];
    $socialCenterSuburb = $_POST['socialCenterSuburb'];
    $socialCenterCity = $_POST['socialCenterCity'];
    $socialCenterDescription = $_POST['socialCenterDescription'];

    $minPasswordLength = 8;

    if (empty($socialCenterName) || empty($socialCenterEmail) || empty($socialCenterPassword) || empty($socialCenterConfirmPassword) || empty($socialCenterStreet) || empty($socialCenterSuburb) || empty($socialCenterCity) || empty($socialCenterDescription)) {
        $response['success'] = false;
        $response['message'] = "Todos los campos son obligatorios. Por favor, complete todos los campos.";
    } elseif (strlen($socialCenterPassword) < $minPasswordLength) {
        $response['success'] = false;
        $response['message'] = "La contraseña debe tener al menos $minPasswordLength caracteres.";
    } elseif ($socialCenterPassword != $socialCenterConfirmPassword) {
        $response['success'] = false;
        $response['message'] = "Las contraseñas no coinciden.";
    } elseif (!filter_var($socialCenterEmail, FILTER_VALIDATE_EMAIL)) {
        $response['success'] = false;
        $response['message'] = "El formato del correo electrónico no es válido.";
    } else {

        $userExistsQuery = "SELECT * FROM social_centers WHERE sc_name = ?";
        $stmtUserExists = $dbConnection->prepareStatement($userExistsQuery);
        $stmtUserExists->bind_param("s", $socialCenterName);
        $stmtUserExists->execute();
        $resultUserExists = $stmtUserExists->get_result();
        $stmtUserExists->close();

        if ($resultUserExists->num_rows === 0) {
            $userExistsQueryAlt = "SELECT * FROM altruist_donators WHERE ad_username = ?";
            $stmtUserExistsAlt = $dbConnection->prepareStatement($userExistsQueryAlt);
            $stmtUserExistsAlt->bind_param("s", $socialCenterName);
            $stmtUserExistsAlt->execute();
            $resultUserExistsAlt = $stmtUserExistsAlt->get_result();
            $stmtUserExistsAlt->close();
        }

        $emailExistsQuery = "SELECT * FROM social_centers WHERE sc_email = ?";
        $stmtEmailExists = $dbConnection->prepareStatement($emailExistsQuery);
        $stmtEmailExists->bind_param("s", $socialCenterEmail);
        $stmtEmailExists->execute();
        $resultEmailExists = $stmtEmailExists->get_result();
        $stmtEmailExists->close();

        if ($resultEmailExists->num_rows === 0) {
            $emailExistsQueryAlt = "SELECT * FROM altruist_donators WHERE ad_email = ?";
            $stmtEmailExistsAlt = $dbConnection->prepareStatement($emailExistsQueryAlt);
            $stmtEmailExistsAlt->bind_param("s", $socialCenterEmail);
            $stmtEmailExistsAlt->execute();
            $resultEmailExistsAlt = $stmtEmailExistsAlt->get_result();
            $stmtEmailExistsAlt->close();
        }

        if ($resultUserExists->num_rows > 0 || $resultUserExistsAlt->num_rows > 0) {
            $response['success'] = false;
            $response['message'] = "El nombre de usuario ya está registrado. Por favor, elija otro nombre de usuario.";
        } elseif ($resultEmailExists->num_rows > 0 || $resultEmailExistsAlt->num_rows > 0) {
            $response['success'] = false;
            $response['message'] = "El correo electrónico ya está registrado. Por favor, utilice otro correo electrónico.";
        } else {
            $hashedPassword = password_hash($socialCenterPassword, PASSWORD_BCRYPT);

            $sql = "INSERT INTO social_centers (sc_name, sc_email, sc_password, sc_street, sc_suburb, sc_city, sc_description) VALUES (?, ?, ?, ?, ?, ?, ?)";
            $stmtInsert = $dbConnection->prepareStatement($sql);
            $stmtInsert->bind_param("sssssss", $socialCenterName, $socialCenterEmail, $hashedPassword, $socialCenterStreet, $socialCenterSuburb, $socialCenterCity, $socialCenterDescription);

            if ($stmtInsert->execute()) {
                $response['success'] = true;
                $response['message'] = "Centro Social Registrado exitosamente.";
            } else {
                $response['success'] = false;
                $response['message'] = "Error al Registrarse: " . $stmtInsert->error;
            }

            $stmtInsert->close();
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
