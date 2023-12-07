<?php
require 'mailSender.php';
require 'db_connection.php';
require 'generateCode.php';

$response = array();

$dbConnection = new DBConnection();

$correo = new CorreoElectronico();

$codigoAleatorio = CodigoAleatorio::generarCodigo();
$codigoAleatorioEscapado = htmlspecialchars($codigoAleatorio, ENT_QUOTES, 'UTF-8');

$htmlCode = <<<HTML
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperación de Contraseña - SolidarApp</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #F0F8FF; 
        }

        .container {
            max-width: 600px;
            margin: 20px auto;
            border: 5px solid #3498db; 
            border-radius: 8px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
            background-color: #FFFFFF; 
        }

        .logo {
            text-align: center;
            padding: 20px;
            background-color: #87CEEB; 
            border-top-left-radius: 8px;
            border-top-right-radius: 8px;
        }

        .logo img {
            width: 120px;
            height: auto;
        }

        .content {
            padding: 30px;
            background-color: #87CEEB; 
        }

        .content p {
            color: #333; 
            line-height: 1.6;
            margin-bottom: 15px;
        }

        .footer {
            text-align: center;
            color: #555; 
            padding: 20px;
            background-color: #87CEEB; 
            border-bottom-left-radius: 8px;
            border-bottom-right-radius: 8px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="logo">
            <img src="https://i.imgur.com/l7ynno9.jpg" alt="SolidarApp Logo">
        </div>
        <div class="content">
            <p><strong>Estimado Usuario,</strong></p>
            <p style="text-align:justify">Se solicitó la <strong>restauración de la contraseña</strong> de la cuenta SolidarApp asociada a esta dirección de correo electrónico. <strong>Si no fuiste tú, por favor, ignora este mensaje.</strong></p>
            <p style="text-align:center"><strong>Código de Reestablecimiento:</strong></p>
            <p style="text-align:center; font-weight: 10px"><strong>$codigoAleatorioEscapado</strong></p>
        </div>
        <div class="footer">
            <p>Este correo ha sido enviado automáticamente. Por favor, no respondas a este mensaje.</p>
        </div>
    </div>
</body>
</html>
HTML;

define('CUERPO_CORREO', $htmlCode);
define('ASUNTO_CORREO', 'Reestablecimiento de Contraseña');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userRecoveryEmail = $_POST['userRecoveryEmail'];

    if (filter_var($userRecoveryEmail, FILTER_VALIDATE_EMAIL)) {
        list($usuario, $dominioCompleto) = explode('@', $userRecoveryEmail, 2);

        list($dominio, $extension) = explode('.', $dominioCompleto, 2);

        if (strpos($dominioCompleto, '.') !== false) {
            if (empty($userRecoveryEmail)) {
                $response['success'] = false;
                $response['message'] = "Ingresa el Correo Electrónico asociado a tu Cuenta para restablecerla.";
            } else {
                $checkIfMailExistsQuery = "SELECT ad_username FROM altruist_donators WHERE ad_email = ? UNION SELECT sc_name FROM social_centers WHERE sc_email = ?";
                $stmtCheckIfMailExists = $dbConnection->prepareStatement($checkIfMailExistsQuery);
                $stmtCheckIfMailExists->bind_param("ss", $userRecoveryEmail, $userRecoveryEmail);
                $stmtCheckIfMailExists->execute();

                $resultCheckIfMailExists = $stmtCheckIfMailExists->get_result();

                if ($resultCheckIfMailExists->num_rows > 0) {

                    $timestampActual = time();
                    $timestampResultado = $timestampActual + (15 * 60);

                    $fechaExpiracion = date("Y-m-d H:i:s", $timestampResultado);

                    $isUsed = FALSE;

                    $setAccountTokenQuery = "INSERT INTO account_tokens (email, token, is_used, expiration_time) VALUES (?, ?, ?, ?)";
                    $stmtSetAccountToken = $dbConnection->prepareStatement($setAccountTokenQuery);
                    $stmtSetAccountToken->bind_param("ssss", $userRecoveryEmail, $codigoAleatorio, $isUsed, $fechaExpiracion);
                    $stmtSetAccountToken->execute();

                    try {
                        $envioCodigoReestablecimiento = $correo->enviarCorreo($userRecoveryEmail, ASUNTO_CORREO, CUERPO_CORREO);
                    } catch (Exception $e) {
                        $response['success'] = false;
                        $response['message'] = "El Envío del Código de Recuperación falló.";
                    } finally {
                        $response['success'] = true;
                        $response['message'] = "Revise su Correo Electrónico e ingrese el Código de Reestablecimiento.";
                    }
                    
                } else {
                    $response['success'] = false;
                    $response['message'] = "Revise su Correo Electrónico e ingrese el Código de Reestablecimiento.";
                }
            }

        } else {
            $response['success'] = false;
            $response['message'] = "El correo electrónico no es válido. Falta el punto en el dominio.";
        }
    } else {
        $response['success'] = false;
        $response['message'] = "El correo electrónico no es válido. Por favor, introduce un correo válido.";
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
