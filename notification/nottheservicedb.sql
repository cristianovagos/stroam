-- phpMyAdmin SQL Dump
-- version 4.8.4-dev
-- https://www.phpmyadmin.net/
--
-- Host: mysqlserver
-- Generation Time: 22-Out-2018 às 22:59
-- Versão do servidor: 8.0.12
-- versão do PHP: 7.2.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `nottheservicedb`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `Logs`
--

CREATE TABLE `Logs` (
  `Id` int(11) NOT NULL,
  `Timestamp` timestamp NOT NULL,
  `Message` varchar(500) DEFAULT NULL,
  `StackTrace` varchar(5000) NOT NULL,
  `SeverityId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `LogSeverities`
--

CREATE TABLE `LogSeverities` (
  `Id` int(11) NOT NULL,
  `Name` varchar(200) NOT NULL,
  `Description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `QueuedMessages`
--

CREATE TABLE `QueuedMessages` (
  `Id` int(11) NOT NULL,
  `Channel` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Payload` longblob NOT NULL,
  `Timestamp` timestamp NOT NULL,
  `Processed` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Services`
--

CREATE TABLE `Services` (
  `Id` varchar(36) NOT NULL,
  `Name` varchar(200) DEFAULT NULL,
  `Active` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Services_Users`
--

CREATE TABLE `Services_Users` (
  `ServiceId` varchar(36) NOT NULL,
  `UserId` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Users`
--

CREATE TABLE `Users` (
  `Id` varchar(36) NOT NULL,
  `ExternalId` varchar(500) NOT NULL,
  `Name` varchar(200) DEFAULT NULL,
  `EmailAddress` varchar(200) DEFAULT NULL,
  `PhoneNumber` varchar(200) DEFAULT NULL,
  `PushNotification` tinyint(1) NOT NULL DEFAULT '1',
  `EmailNotification` tinyint(1) NOT NULL DEFAULT '0',
  `PhoneNotification` tinyint(1) NOT NULL DEFAULT '0',
  `Active` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Logs`
--
ALTER TABLE `Logs`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `SeverityId` (`SeverityId`);

--
-- Indexes for table `LogSeverities`
--
ALTER TABLE `LogSeverities`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `QueuedMessages`
--
ALTER TABLE `QueuedMessages`
  ADD PRIMARY KEY (`Id`) USING BTREE;

--
-- Indexes for table `Services`
--
ALTER TABLE `Services`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `Services_Users`
--
ALTER TABLE `Services_Users`
  ADD PRIMARY KEY (`ServiceId`,`UserId`),
  ADD KEY `UserId` (`UserId`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`Id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Logs`
--
ALTER TABLE `Logs`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `LogSeverities`
--
ALTER TABLE `LogSeverities`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `QueuedMessages`
--
ALTER TABLE `QueuedMessages`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Limitadores para a tabela `Logs`
--
ALTER TABLE `Logs`
  ADD CONSTRAINT `Logs_ibfk_1` FOREIGN KEY (`SeverityId`) REFERENCES `LogSeverities` (`id`);

--
-- Limitadores para a tabela `Services_Users`
--
ALTER TABLE `Services_Users`
  ADD CONSTRAINT `Services_Users_ibfk_1` FOREIGN KEY (`ServiceId`) REFERENCES `Services` (`id`),
  ADD CONSTRAINT `Services_Users_ibfk_2` FOREIGN KEY (`UserId`) REFERENCES `Users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
