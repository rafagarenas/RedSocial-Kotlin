
# Red Social para Android
Este proyecto fue desarollado en 7 días como requerimiento de un cliente, dandome autorización de compartir el código en mi portafolio. 

La idea de este proyecto es permitir a los usuarios conectarse a una red social en la cual puedan interactuar entre ellos, creando publicaciones, comentarios y proporcionando reacciones.
## Como Ejecutar el Proyecto

#### Versiónes Requeridas

```http
minSdk = 24
targetSdk = 34
```

#### Conexión con el BackEnd

Dirijase al directorio **/res/values** del proyecto de Android Studio y abra el archivo *strings.xml*, allí encontrará un string llamado **API_URL**.

```http
<resources>
    //URL del Servidor de la API.
    <string name="API_URL">http://192.168.1.89/</string>

    //Utilidades.
    <string name="app_name">SolidarApp</string>
    <string name="home" />

    ... RESTO DE LOS STRINGS ...
```

Cambie la dirección que se le asignó al string por la URL donde está levantado el Back-End en su Servidor Web.

#### Conexión con la Base de Datos

En el directorio que puede encontrar en este repositorio **BackEnd SolidarApp**, encontrará dentro un archivo llamado **db_connect.php** el cual deberá modificar.

```http
<?php
class DBConnection {
    private $servername = "localhost";
    private $username = "root";
    private $password = "";
    private $dbname = "solidarapp";
    private $conn;

    ... RESTO DEL CÓDIGO ...
```

Aquí establecerá las credenciales que se utilizarán para realizar todas las consultas necesarias.

Por último, no se olvide de importar la base de datos localizada en la misma carpeta, con el nombre **solidarapp.sql**.
## Screenshot en la App


![App Screenshot](https://i.imgur.com/ZyLDzLa.jpg)
