-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 06-12-2023 a las 23:37:06
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `solidarapp`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `account_locks`
--

CREATE TABLE `account_locks` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `failed_attempt_count` int(11) NOT NULL DEFAULT 0,
  `last_failed_attempt_time` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `account_locks`
--

INSERT INTO `account_locks` (`id`, `email`, `failed_attempt_count`, `last_failed_attempt_time`, `created_at`, `updated_at`) VALUES
(100, '202060215@ucc.mx', 0, NULL, '2023-12-06 22:25:42', '2023-12-06 22:28:29');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `account_tokens`
--

CREATE TABLE `account_tokens` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `token` varchar(100) NOT NULL,
  `generated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_used` tinyint(1) DEFAULT 0,
  `expiration_time` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `account_tokens`
--

INSERT INTO `account_tokens` (`id`, `email`, `token`, `generated_at`, `is_used`, `expiration_time`) VALUES
(99, '202060215@ucc.mx', 'J2%Y52hX', '2023-12-06 22:28:48', 1, '2023-12-07 05:43:48');

--
-- Disparadores `account_tokens`
--
DELIMITER $$
CREATE TRIGGER `set_expiration_time` BEFORE INSERT ON `account_tokens` FOR EACH ROW SET NEW.expiration_time = IFNULL(NEW.expiration_time, CURRENT_TIMESTAMP + INTERVAL 1 DAY)
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `altruist_donators`
--

CREATE TABLE `altruist_donators` (
  `id` int(11) NOT NULL,
  `ad_username` varchar(250) NOT NULL,
  `ad_email` varchar(250) NOT NULL,
  `ad_password` varchar(250) NOT NULL,
  `ad_register_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `ad_picture` varchar(250) NOT NULL DEFAULT 'https://i.imgur.com/bcQL91M.jpg'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `altruist_donators`
--

INSERT INTO `altruist_donators` (`id`, `ad_username`, `ad_email`, `ad_password`, `ad_register_date`, `ad_picture`) VALUES
(21, 'esmeraldasolano', '202060410@ucc.mx', '$2y$10$Yh.mBIm8o2GYZHGZ//dQr.kXC8/Ej0cOGuL5bTIJrBsM.4cYElTm2', '2023-12-04 03:36:30', 'https://i.imgur.com/bcQL91M.jpg'),
(24, 'administrador', 'admin@hotmail.com', '$2y$10$KBVDL821B7pV1iikFvq.J.IB/k.z.hdP.8QRFgDXednOKH5QgGyPG', '2023-12-06 20:06:52', 'https://i.imgur.com/bcQL91M.jpg'),
(26, 'adolfojimenez', '202060215@ucc.mx', '$2y$10$bEx2DOmUTXt6SsfIuyP1qeNEfY8uSVIN943H0ezM3R6dL0JDN0Oxu', '2023-12-06 22:26:45', 'https://i.imgur.com/bcQL91M.jpg');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `app_posts`
--

CREATE TABLE `app_posts` (
  `id` int(11) NOT NULL,
  `post_description` varchar(250) NOT NULL,
  `post_picture` varchar(250) NOT NULL,
  `post_owner` varchar(250) NOT NULL,
  `post_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `post_likes_count` int(11) NOT NULL DEFAULT 0,
  `post_liked_by` varchar(250) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `app_posts`
--

INSERT INTO `app_posts` (`id`, `post_description`, `post_picture`, `post_owner`, `post_date`, `post_likes_count`, `post_liked_by`) VALUES
(10, 'Visitanos en la Feria de Adopcion para encontrarle un hogar a estas criaturas de noble corazon. ♥️', 'https://firebasestorage.googleapis.com/v0/b/solidarapp-34c94.appspot.com/o/images%2F1701797571048?alt=media&token=10f04496-45d6-44ee-b7a2-13f27c1581da', 'centrosocial@calasanz.com', '2023-12-05 17:32:52', 0, '   '),
(11, 'Sumate al voluntariado para la recoleccion de basura. Por que juntos podemos hacer la diferencia. 👍', 'https://firebasestorage.googleapis.com/v0/b/solidarapp-34c94.appspot.com/o/images%2F1701797767982?alt=media&token=8930d4d3-7f24-41df-9250-1b1a0deaf0cb', 'fundacion@grupocice.com', '2023-12-05 17:36:09', 2, '        centrosocial@calasanz.com  admin@hotmail.com  ');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `social_centers`
--

CREATE TABLE `social_centers` (
  `id` int(11) NOT NULL,
  `sc_name` varchar(250) NOT NULL,
  `sc_email` varchar(250) NOT NULL,
  `sc_password` varchar(250) NOT NULL,
  `sc_street` varchar(250) NOT NULL,
  `sc_suburb` varchar(250) NOT NULL,
  `sc_city` varchar(250) NOT NULL,
  `sc_description` varchar(550) NOT NULL,
  `sc_register_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `sc_picture` varchar(250) NOT NULL DEFAULT 'https://i.imgur.com/ghV08gK.jpg'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `social_centers`
--

INSERT INTO `social_centers` (`id`, `sc_name`, `sc_email`, `sc_password`, `sc_street`, `sc_suburb`, `sc_city`, `sc_description`, `sc_register_date`, `sc_picture`) VALUES
(4, 'Centro Social Calasanz', 'centrosocial@calasanz.com', '$2y$10$R0F7UC4aL8hoDVsjStmbC.L78Z2v3dVlf6mni8mx/FlsVjYlTZu0O', 'Diaz Miron', 'Veracruz', 'Veracruz', 'El Centro Social Calasanz esta enfocado en...', '2023-12-04 03:37:38', 'https://i.imgur.com/ghV08gK.jpg	'),
(6, 'Fundacion CICE', 'fundacion@grupocice.com', '$2y$10$Fh4GrS/i.ADFaNQFb3rDIuhveQick3WhD1yaydzu2U/87Iq5L7Te6', 'Independencia', 'Centro', 'Veracruz', 'Fundacion CICE esta orientado en...', '2023-12-05 17:34:54', 'https://i.imgur.com/ghV08gK.jpg');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `account_locks`
--
ALTER TABLE `account_locks`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indices de la tabla `account_tokens`
--
ALTER TABLE `account_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `email` (`email`);

--
-- Indices de la tabla `altruist_donators`
--
ALTER TABLE `altruist_donators`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `app_posts`
--
ALTER TABLE `app_posts`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `social_centers`
--
ALTER TABLE `social_centers`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `account_locks`
--
ALTER TABLE `account_locks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=103;

--
-- AUTO_INCREMENT de la tabla `account_tokens`
--
ALTER TABLE `account_tokens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=100;

--
-- AUTO_INCREMENT de la tabla `altruist_donators`
--
ALTER TABLE `altruist_donators`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT de la tabla `app_posts`
--
ALTER TABLE `app_posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT de la tabla `social_centers`
--
ALTER TABLE `social_centers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
