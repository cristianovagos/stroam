-- phpMyAdmin SQL Dump
-- version 4.8.4-dev
-- https://www.phpmyadmin.net/
--
-- Host: mysqlserver
-- Generation Time: 13-Out-2018 às 01:13
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
-- Estrutura da tabela `Channels`
--

CREATE TABLE `Channels` (
  `Id` int(11) NOT NULL,
  `Path` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Producers`
--

CREATE TABLE `Producers` (
  `Id` int(11) NOT NULL,
  `Name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Producers_Channels`
--

CREATE TABLE `Producers_Channels` (
  `ProducerId` int(11) NOT NULL,
  `ChannelId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Services`
--

CREATE TABLE `Services` (
  `Id` int(11) NOT NULL,
  `Name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `ChannelPrefixId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Services_Producers`
--

CREATE TABLE `Services_Producers` (
  `ServiceId` int(11) NOT NULL,
  `ProducerId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Subscribers`
--

CREATE TABLE `Subscribers` (
  `Id` int(11) NOT NULL,
  `ExternalId` varchar(200) NOT NULL,
  `Name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Subscribers_Channels`
--

CREATE TABLE `Subscribers_Channels` (
  `SubscriberId` int(11) NOT NULL,
  `ChannelId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Channels`
--
ALTER TABLE `Channels`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `Producers`
--
ALTER TABLE `Producers`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `Producers_Channels`
--
ALTER TABLE `Producers_Channels`
  ADD PRIMARY KEY (`ProducerId`,`ChannelId`),
  ADD KEY `ChannelId` (`ChannelId`);

--
-- Indexes for table `Services`
--
ALTER TABLE `Services`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `ChannelPrefixId` (`ChannelPrefixId`);

--
-- Indexes for table `Services_Producers`
--
ALTER TABLE `Services_Producers`
  ADD PRIMARY KEY (`ServiceId`,`ProducerId`),
  ADD KEY `ProducerId` (`ProducerId`);

--
-- Indexes for table `Subscribers`
--
ALTER TABLE `Subscribers`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `Subscribers_Channels`
--
ALTER TABLE `Subscribers_Channels`
  ADD PRIMARY KEY (`SubscriberId`,`ChannelId`),
  ADD KEY `ChannelId` (`ChannelId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Channels`
--
ALTER TABLE `Channels`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Producers`
--
ALTER TABLE `Producers`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Services`
--
ALTER TABLE `Services`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Subscribers`
--
ALTER TABLE `Subscribers`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Limitadores para a tabela `Producers_Channels`
--
ALTER TABLE `Producers_Channels`
  ADD CONSTRAINT `Producers_Channels_ibfk_1` FOREIGN KEY (`ChannelId`) REFERENCES `Channels` (`id`),
  ADD CONSTRAINT `Producers_Channels_ibfk_2` FOREIGN KEY (`ProducerId`) REFERENCES `Producers` (`id`);

--
-- Limitadores para a tabela `Services`
--
ALTER TABLE `Services`
  ADD CONSTRAINT `Services_ibfk_1` FOREIGN KEY (`ChannelPrefixId`) REFERENCES `Channels` (`id`);

--
-- Limitadores para a tabela `Services_Producers`
--
ALTER TABLE `Services_Producers`
  ADD CONSTRAINT `Services_Producers_ibfk_1` FOREIGN KEY (`ServiceId`) REFERENCES `Services` (`id`),
  ADD CONSTRAINT `Services_Producers_ibfk_2` FOREIGN KEY (`ProducerId`) REFERENCES `Producers` (`id`);

--
-- Limitadores para a tabela `Subscribers_Channels`
--
ALTER TABLE `Subscribers_Channels`
  ADD CONSTRAINT `Subscribers_Channels_ibfk_1` FOREIGN KEY (`SubscriberId`) REFERENCES `Subscribers` (`id`),
  ADD CONSTRAINT `Subscribers_Channels_ibfk_2` FOREIGN KEY (`ChannelId`) REFERENCES `Channels` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
