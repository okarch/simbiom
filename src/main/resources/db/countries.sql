-- Countrylist.net SQL Dump
-- http://countrylist.net
--
-- Erstellungszeit: 2015-02-24 00:05:00

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
--
-- table: countrys_int
--

CREATE TABLE IF NOT EXISTS `t_country` ( `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT, `continent` enum('Antarctica','Australia','Africa','North America','South America','Europe','Asia') NOT NULL, `name` varchar(200) NOT NULL, `capital` varchar(200) NOT NULL, `iso-2` varchar(2) NOT NULL, `iso-3` varchar(3) NOT NULL, `ioc` varchar(3) NOT NULL, `tld` varchar(6) NOT NULL, `currency` varchar(5) NOT NULL, `phone` varchar(10) NOT NULL, `utc` mediumint(9) DEFAULT NULL, `wiki` varchar(255) DEFAULT NULL , `name_de` varchar(200) NOT NULL, `capital_de` varchar(200) NOT NULL, `wiki_de` varchar(255) DEFAULT NULL , PRIMARY KEY (`id`), KEY `continent` (`continent`), KEY `name-en` (`name`)) DEFAULT CHARSET=utf8 ;

--
-- data: countrys
--

INSERT INTO `t_country` (`id`,`continent`,`name`,`capital`,`iso-2`,`iso-3`,`ioc`,`tld`,`currency`,`phone`,`utc`,`wiki`,`name_de`,`capital_de`,`wiki_de`) VALUES 
('1', 'Asia', 'Afghanistan', 'Kabul', 'AF', 'AFG', 'AFG', '.af', 'AFN', '+93', '270', NULL, 'Afghanistan', 'Kabul', ''),
('2', 'Africa', 'Egypt', 'Kairo', 'EG', 'EGY', 'EGY', '.eg', 'EGP', '+20', '120', NULL, 'Ägypten', 'Kairo', 'http://de.wikipedia.org/wiki/Ägypten'),
('3', 'Europe', 'Åland Islands', 'Mariehamn', 'AX', 'ALA', '', '.ax', 'EUR', '+35818', '120', NULL, 'Aland', 'Mariehamn', ''),
('4', 'Europe', 'Albania', 'Tirana', 'AL', 'ALB', 'ALB', '.al', 'ALL', '+355', '60', NULL, 'Albanien', 'Tirana', ''),
('5', 'Africa', 'Algeria', 'Algier', 'DZ', 'DZA', 'ALG', '.dz', 'DZD', '+213', '60', NULL, 'Algerien', 'Algier', ''),
('6', 'Australia', 'American Samoa', 'Pago-Pago', 'AS', 'ASM', '', '.as', 'USD', '+1684', '-660', NULL, 'Amerikanisch-Samoa', 'Pago-Pago', ''),
('7', 'South America', 'Virgin Islands, U.s.', 'Charlotte Amalie', 'VI', 'VIR', '', '.vi', 'USD', '+1340', '-240', NULL, 'Amerikanische Jungferninseln', 'Charlotte Amalie', ''),
('8', 'Europe', 'Andorra', 'Andorra la Vella', 'AD', 'AND', 'AND', '.ad', 'EUR', '+376', '60', NULL, 'Andorra', 'Andorra la Vella', ''),
('9', 'Africa', 'Angola', 'Luanda', 'AO', 'AGO', 'ANG', '.ao', 'AOA', '+244', '60', NULL, 'Angola', 'Luanda', ''),
('10', 'North America', 'Anguilla', 'The Valley', 'AI', 'AIA', '', '.ai', 'XCD', '+1264', NULL, NULL, 'Anguilla', 'The Valley', ''),
('11', 'Antarctica', 'Antarctica', 'Juneau', 'AQ', 'ATA', '', '.aq', '', '+672', NULL, NULL, 'Antarktis', 'Juneau', ''),
('12', 'North America', 'Antigua And Barbuda', 'Saint John\'s', 'AG', 'ATG', 'ANT', '.ag', 'XCD', '+1268', NULL, NULL, 'Antigua und Barbuda', 'Saint John\'s', ''),
('13', 'Africa', 'Equatorial Guinea', 'Malabo', 'GQ', 'GNQ', 'GEQ', '.gq', 'XAF', '+240', NULL, NULL, 'Äquatorialguinea, Republik', 'Malabo', ''),
('14', 'South America', 'Argentina', 'Buenos Aires', 'AR', 'ARG', 'ARG', '.ar', 'ARS', '+54', '-180', NULL, 'Argentinien', 'Buenos Aires', ''),
('15', 'Asia', 'Armenia', 'Eriwan', 'AM', 'ARM', 'ARM', '.am', 'AMD', '+374', NULL, NULL, 'Armenien', 'Eriwan', ''),
('16', 'North America', 'Aruba', 'Oranjestad', 'AW', 'ABW', 'ARU', '.aw', 'ANG', '+297', NULL, NULL, 'Aruba', 'Oranjestad', ''),
('17', 'Africa', 'Ascension', '-/-', 'AC', 'ASC', '', '.ac', '', '+247', NULL, NULL, 'Ascension', '-/-', ''),
('18', 'Asia', 'Azerbaijan', 'Baku', 'AZ', 'AZE', 'AZE', '.az', 'AZN', '+994', NULL, NULL, 'Aserbaidschan', 'Baku', ''),
('19', 'Africa', 'Ethiopia', 'Addis Abeba', 'ET', 'ETH', 'ETH', '.et', 'ETB', '+251', '180', NULL, 'Äthiopien', 'Addis Abeba', ''),
('20', 'Australia', 'Australia', 'Canberra', 'AU', 'AUS', 'AUS', '.au', 'AUD', '+61', NULL, NULL, 'Australien', 'Canberra', ''),
('21', 'North America', 'Bahamas', 'Nassau', 'BS', 'BHS', 'BAH', '.bs', 'BSD', '+1242', '-300', NULL, 'Bahamas', 'Nassau', ''),
('22', 'Asia', 'Bahrain', 'Manama', 'BH', 'BHR', 'BRN', '.bh', 'BHD', '+973', '180', NULL, 'Bahrain', 'Manama', ''),
('23', 'Asia', 'Bangladesh', 'Dhaka', 'BD', 'BGD', 'BAN', '.bd', 'BDT', '+880', '360', 'http://en.wikipedia.org/wiki/Bangladesh', 'Bangladesch', 'Dhaka', ''),
('24', 'North America', 'Barbados', 'Bridgetown', 'BB', 'BRB', 'BAR', '.bb', 'BBD', '+1246', '-240', NULL, 'Barbados', 'Bridgetown', ''),
('25', 'Europe', 'Belgium', 'Brüssel', 'BE', 'BEL', 'BEL', '.be', 'EUR', '+32', NULL, NULL, 'Belgien', 'Brüssel', ''),
('26', 'North America', 'Belize', 'Belmopan', 'BZ', 'BLZ', 'BLZ', '.bz', 'BZD', '+51', '-360', NULL, 'Belize', 'Belmopan', ''),
('27', 'Africa', 'Benin', 'Porto Novo', 'BJ', 'BEN', 'BEN', '.bj', 'XOF', '+229', NULL, NULL, 'Benin', 'Porto Novo', ''),
('28', 'North America', 'Bermuda', 'Hamilton', 'BM', 'BMU', 'BER', '.bm', 'BMD', '+1441', NULL, NULL, 'Bermuda', 'Hamilton', ''),
('29', 'Asia', 'Bhutan', 'Thimphu', 'BT', 'BTN', 'BHU', '.bt', 'BTN', '+975', NULL, NULL, 'Bhutan', 'Thimphu', ''),
('30', 'South America', 'Bolivia', 'Sucre', 'BO', 'BOL', 'BOL', '.bo', 'BOB', '+591', NULL, NULL, 'Bolivien', 'Sucre', ''),
('31', 'Europe', 'Bosnia And Herzegovina', 'Sarajevo', 'BA', 'BIH', 'BIH', '.ba', 'BAM', '+387', NULL, NULL, 'Bosnien und Herzegowina', 'Sarajevo', ''),
('32', 'Africa', 'Botswana', 'Gaborone', 'BW', 'BWA', 'BOT', '.bw', 'BWP', '+267', '120', NULL, 'Botswana', 'Gaborone', ''),
('33', 'Antarctica', 'Bouvet Island', '(Forschungsinsel)', 'BV', 'BVT', '', '.bv', 'NOK', '', NULL, NULL, 'Bouvetinsel', '(Forschungsinsel)', ''),
('34', 'South America', 'Brazil', 'Brasília', 'BR', 'BRA', 'BRA', '.br', 'BRL', '+55', NULL, NULL, 'Brasilien', 'Brasília', ''),
('35', 'North America', 'Virgin Islands, British', 'Road Town', 'VG', 'VGB', 'ISB', '.vg', 'USD', '+1284', NULL, NULL, 'Britische Jungferninseln', 'Road Town', ''),
('36', 'Asia', 'British Indian Ocean Territory', '-/-', 'IO', 'IOT', '', '.io', 'USD', '', NULL, NULL, 'Britisches Territorium im Indischen Ozean', '-/-', ''),
('37', 'Asia', 'Brunei Darussalam', 'Bandar Seri Begawan', 'BN', 'BRN', 'BRU', '.bn', 'BND', '+673', NULL, NULL, 'Brunei', 'Bandar Seri Begawan', ''),
('38', 'Europe', 'Bulgaria', 'Sofia', 'BG', 'BGR', 'BUL', '.bg', 'BGN', '+359', NULL, NULL, 'Bulgarien', 'Sofia', ''),
('39', 'Africa', 'Burkina Faso', 'Ouagadougou', 'BF', 'BFA', 'BUR', '.bf', 'XOF', '+226', '0', NULL, 'Burkina Faso', 'Ouagadougou', ''),
('40', 'Africa', 'Burundi', 'Bujumbura', 'BI', 'BDI', 'BDI', '.bi', 'BIF', '+257', NULL, NULL, 'Burundi', 'Bujumbura', ''),
('41', 'South America', 'Chile', 'Santiago', 'CL', 'CHL', 'CHI', '.cl', 'CLP', '+56', NULL, NULL, 'Chile', 'Santiago', ''),
('42', 'Asia', 'China', 'Peking (Beijing)', 'CN', 'CHN', 'CHN', '.cn', 'CNY', '+86', NULL, NULL, 'China, Volksrepublik', 'Peking (Beijing)', ''),
('43', 'Australia', 'Cook Islands', 'Avarua', 'CK', 'COK', 'COK', '.ck', 'NZD', '+682', NULL, NULL, 'Cookinseln', 'Avarua', ''),
('44', 'North America', 'Costa Rica', 'San José', 'CR', 'CRI', 'CRC', '.cr', 'CRC', '+56', NULL, NULL, 'Costa Rica', 'San José', ''),
('45', 'Africa', 'CÔte D\'ivoire', 'Yamoussoukro', 'CI', 'CIV', 'CIV', '.ci', 'XOF', '+225', NULL, NULL, 'Cote d\'Ivoire', 'Yamoussoukro', ''),
('46', 'Europe', 'Denmark', 'Kopenhagen', 'DK', 'DNK', 'DEN', '.dk', 'DKK', '+45', NULL, NULL, 'Dänemark', 'Kopenhagen', ''),
('47', 'Europe', 'Germany', 'Berlin', 'DE', 'DEU', 'GER', '.de', 'EUR', '+49', NULL, NULL, 'Deutschland', 'Berlin', ''),
('48', 'Africa', 'Saint Helena', 'Jamestown', 'SH', 'SHN', '', '.sh', 'SHP', '+290', '0', NULL, 'Die Kronkolonie St. Helena und Nebengebiete', 'Jamestown', ''),
('49', 'Africa', 'Diego Garcia', 'Diego Garcia (Main Island)', 'DG', 'DGA', '', '-/-', '', '+246', '360', NULL, 'Diego Garcia', 'Diego Garcia (Hauptinsel)', ''),
('50', 'North America', 'Dominica', 'Roseau', 'DM', 'DMA', '', '.dm', 'XCD', '+1767', NULL, NULL, 'Dominica', 'Roseau', ''),
('51', 'South America', 'Dominican Republic', 'Santo Domingo', 'DO', 'DOM', 'DOM', '.do', 'DOP', '+1809', NULL, NULL, 'Dominikanische Republik', 'Santo Domingo', ''),
('52', 'Africa', 'Djibouti', 'Dschibuti', 'DJ', 'DJI', 'DJI', 'dj', 'DJF', '+253', NULL, NULL, 'Dschibuti', 'Dschibuti', ''),
('53', 'South America', 'Ecuador', 'Quito', 'EC', 'ECU', 'ECU', '.ec', 'USD', '+593', NULL, NULL, 'Ecuador', 'Quito', ''),
('54', 'North America', 'El Salvador', 'San Salvador', 'SV', 'SLV', 'ESA', '.sv', 'SVC', '+53', NULL, NULL, 'El Salvador', 'San Salvador', ''),
('55', 'Africa', 'Eritrea', 'Asmara (Asmera)', 'ER', 'ERI', 'ERI', '.er', 'ERN', '+291', '180', NULL, 'Eritrea', 'Asmara (Asmera)', ''),
('56', 'Europe', 'Estonia', 'Tallinn (Reval)', 'EE', 'EST', 'EST', '.ee', 'EEK', '+372', NULL, NULL, 'Estland', 'Tallinn (Reval)', ''),
('57', 'Europe', 'European Union', 'Brussels', 'EU', '-/-', '-/-', '.eu', 'EUR', '+3883', NULL, NULL, 'Europäische Union', 'Brüssel', ''),
('58', 'Africa', 'Falkland Islands (malvinas)', 'Port Stanley', 'FK', 'FLK', '', '.fk', 'FLP', '+500', NULL, NULL, 'Falklandinseln', 'Port Stanley', ''),
('59', 'Europe', 'Faroe Islands', 'Tórshavn', 'FO', 'FRO', 'FRO', '.fo', 'DKK', '+298', NULL, NULL, 'Färöer', 'Tórshavn', ''),
('60', 'Australia', 'Fiji', 'Suva', 'FJ', 'FJI', 'FJI', '.fj', 'FJD', '+679', NULL, NULL, 'Fidschi', 'Suva', ''),
('61', 'Europe', 'Finland', 'Helsinki', 'FI', 'FIN', 'FIN', '.fi', 'EUR', '+358', NULL, NULL, 'Finnland', 'Helsinki', ''),
('62', 'Europe', 'France', 'Paris', 'FR', 'FRA', 'FRA', '.fr', 'EUR', '+33', NULL, NULL, 'Frankreich', 'Paris', ''),
('63', 'South America', 'French Guiana', 'Cayenne', 'GF', 'GUF', '', '.gf', 'EUR', '+594', NULL, NULL, 'Französisch-Guayana', 'Cayenne', ''),
('64', 'Australia', 'French Polynesia', 'Papeete', 'PF', 'PYF', '', '.pf', 'XPF', '+689', NULL, NULL, 'Französisch-Polynesien', 'Papeete', ''),
('65', 'Antarctica', 'French Southern Territories', 'Port-aux-Français', 'TF', 'ATF', '', '.tf', 'EUR', '', NULL, NULL, 'Französische Süd- und Antarktisgebiete', 'Port-aux-Français', ''),
('66', 'Africa', 'Gabon', 'Libreville', 'GA', 'GAB', 'GAB', '.ga', 'XAF', '+241', NULL, NULL, 'Gabun', 'Libreville', ''),
('67', 'Africa', 'Gambia', 'Banjul', 'GM', 'GMB', 'GAM', '.gm', 'GMD', '+220', NULL, NULL, 'Gambia', 'Banjul', ''),
('68', 'Europe', 'Georgia', 'Tiflis', 'GE', 'GEO', 'GEO', '.ge', 'GEL', '+995', NULL, NULL, 'Georgien', 'Tiflis', ''),
('69', 'Africa', 'Ghana', 'Accra', 'GH', 'GHA', 'GHA', '.gh', 'GHC', '+233', NULL, NULL, 'Ghana, Republik', 'Accra', ''),
('70', 'Africa', 'Gibraltar', 'Gibraltar (Stadt)', 'GI', 'GIB', '', '.gi', 'GIP', '+350', NULL, NULL, 'Gibraltar', 'Gibraltar (Stadt)', ''),
('71', 'North America', 'Grenada', 'St. George\'s', 'GD', 'GRD', 'GRN', '.gd', 'XCD', '+1473', NULL, NULL, 'Grenada', 'St. George\'s', ''),
('72', 'Europe', 'Greece', 'Athen', 'GR', 'GRC', 'GRE', '.gr', 'EUR', '+30', NULL, NULL, 'Griechenland', 'Athen', ''),
('73', 'North America', 'Greenland', 'Nuuk', 'GL', 'GRL', '', '.gl', 'DKK', '+299', NULL, NULL, 'Grönland', 'Nuuk', ''),
('74', 'North America', 'Guadeloupe', 'Basse-Terre', 'GP', 'GLP', '', '.gp', 'EUR', '+590', NULL, NULL, 'Guadeloupe', 'Basse-Terre', ''),
('75', 'Asia', 'Guam', 'Hagåtña', 'GU', 'GUM', 'GUM', '.gu', 'USD', '+1671', NULL, NULL, 'Guam', 'Hagåtña', ''),
('76', 'North America', 'Guatemala', 'Guatemala-Stadt', 'GT', 'GTM', 'GUA', '.gt', 'GTQ', '+52', NULL, NULL, 'Guatemala', 'Guatemala-Stadt', ''),
('77', 'Europe', 'Guernsey', 'St. Peter Port', 'GG', 'GGY', '', '.gg', 'GGP', '+44', NULL, NULL, 'Guernsey, Vogtei', 'St. Peter Port', ''),
('78', 'Africa', 'Guinea', 'Conakry', 'GN', 'GIN', 'GUI', '.gn', 'GNF', '+224', NULL, NULL, 'Guinea, Republik', 'Conakry', ''),
('79', 'Africa', 'Guinea-bissau', 'Bissau', 'GW', 'GNB', 'GBS', '.gw', 'XOF', '+245', NULL, NULL, 'Guinea-Bissau, Republik', 'Bissau', ''),
('80', 'South America', 'Guyana', 'Georgetown', 'GY', 'GUY', 'GUY', '.gy', 'GYD', '+592', NULL, NULL, 'Guyana', 'Georgetown', ''),
('81', 'North America', 'Haiti', 'Port-au-Prince', 'HT', 'HTI', 'HAI', '.ht', 'USD', '+59', NULL, NULL, 'Haiti', 'Port-au-Prince', ''),
('82', 'Australia', 'Heard Island And Mcdonald Islands', '-/-', 'HM', 'HMD', '', '.hm', 'AUD', '', NULL, NULL, 'Heard und McDonaldinseln', '-/-', ''),
('83', 'North America', 'Honduras', 'Tegucigalpa', 'HN', 'HND', 'HON', '.hn', 'HNL', '+54', NULL, NULL, 'Honduras', 'Tegucigalpa', ''),
('84', 'Asia', 'Hong Kong', '-/-', 'HK', 'HKG', 'HKG', '.hk', 'HNL', '+852', NULL, NULL, 'Hongkong', '-/-', ''),
('85', 'Asia', 'India', 'Neu-Delhi', 'IN', 'IND', 'IND', '.in', 'ISK', '+91', NULL, NULL, 'Indien', 'Neu-Delhi', ''),
('86', 'Asia', 'Indonesia', 'Jakarta', 'ID', 'IDN', 'INA', '.id', 'INR', '+62', NULL, NULL, 'Indonesien', 'Jakarta', ''),
('87', 'Europe', 'Isle Of Man', 'Douglas', 'IM', 'IMN', '', '.im', 'IMP', '+44', NULL, NULL, 'Insel Man', 'Douglas', ''),
('88', 'Asia', 'Iraq', 'Bagdad', 'IQ', 'IRQ', 'IRQ', '.iq', 'IDR', '+964', NULL, NULL, 'Irak', 'Bagdad', ''),
('89', 'Asia', 'Iran, Islamic Republic Of', 'Teheran', 'IR', 'IRN', 'IRI', '.ir', 'IRR', '+98', NULL, NULL, 'Iran', 'Teheran', ''),
('90', 'Europe', 'Ireland', 'Dublin', 'IE', 'IRL', 'IRL', '.ie', 'EUR', '+353', NULL, NULL, 'Irland, Republik', 'Dublin', ''),
('91', 'Europe', 'Iceland', 'Reykjavík', 'IS', 'ISL', 'ISL', '.is', 'HUF', '+354', NULL, NULL, 'Island', 'Reykjavík', ''),
('92', 'Asia', 'Israel', 'Jerusalem', 'IL', 'ISR', 'ISR', '.il', 'ILS', '+972', NULL, NULL, 'Israel', 'Jerusalem', ''),
('93', 'Europe', 'Italy', 'Rom', 'IT', 'ITA', 'ITA', '.it', 'EUR', '+39', NULL, NULL, 'Italien', 'Rom', ''),
('94', 'North America', 'Jamaica', 'Kingston', 'JM', 'JAM', 'JAM', '.jm', 'JMD', '+1876', NULL, NULL, 'Jamaika', 'Kingston', ''),
('95', 'Asia', 'Japan', 'Tokio', 'JP', 'JPN', 'JPN', '.jp', 'JPY', '+81', NULL, NULL, 'Japan', 'Tokio', ''),
('96', 'Asia', 'Yemen', 'Sanaa', 'YE', 'YEM', 'YEM', '.ye', 'YER', '+967', NULL, NULL, 'Jemen', 'Sanaa', ''),
('97', 'Europe', 'Jersey', 'Saint Helier', 'JE', 'JEY', '', '.je', 'JEP', '+44', NULL, NULL, 'Jersey', 'Saint Helier', ''),
('98', 'Asia', 'Jordan', 'Amman', 'JO', 'JOR', 'JOR', '.jo', 'JOD', '+962', NULL, NULL, 'Jordanien', 'Amman', ''),
('99', 'North America', 'Cayman Islands', 'George Town', 'KY', 'CYM', 'CAY', '.ky', 'KYD', '+1345', NULL, NULL, 'Kaimaninseln', 'George Town', ''),
('100', 'Asia', 'Cambodia', 'Phnom Penh', 'KH', 'KHM', 'CAM', '.kh', 'KHR', '+855', NULL, NULL, 'Kambodscha', 'Phnom Penh', ''),
('101', 'Africa', 'Cameroon', 'Yaoundé', 'CM', 'CMR', 'CMR', '.cm', 'XAF', '+237', NULL, NULL, 'Kamerun', 'Yaoundé', ''),
('102', 'North America', 'Canada', 'Ottawa', 'CA', 'CAN', 'CAN', '.ca', 'CAD', '+1NXX', NULL, NULL, 'Kanada', 'Ottawa', ''),
('103', 'Europe', 'Canary Islands', 'Santa Cruz', 'IC', '', '', '', '', '', NULL, NULL, 'Kanarische Inseln', 'Santa Cruz de Tenerife', ''),
('104', 'Africa', 'Cape Verde', 'Praia', 'CV', 'CPV', 'CPV', '.cv', 'CVE', '+238', NULL, NULL, 'Kap Verde, Republik', 'Praia', ''),
('105', 'Asia', 'Kazakhstan', 'Astana', 'KZ', 'KAZ', 'KAZ', '.kz', 'KZT', '+7', NULL, NULL, 'Kasachstan', 'Astana', ''),
('106', 'Asia', 'Qatar', 'Doha', 'QA', 'QAT', 'QAT', '.qa', 'QAR', '+974', NULL, NULL, 'Katar', 'Doha', ''),
('107', 'Africa', 'Kenya', 'Nairobi', 'KE', 'KEN', 'KEN', '.ke', 'KES', '+254', NULL, NULL, 'Kenia', 'Nairobi', ''),
('108', 'Asia', 'Kyrgyzstan', 'Bischkek', 'KG', 'KGZ', 'KGZ', '.kg', 'KGS', '+996', NULL, NULL, 'Kirgisistan', 'Bischkek', ''),
('109', 'Australia', 'Kiribati', 'Bairiki', 'KI', 'KIR', '', '.ki', 'AUD', '+686', NULL, NULL, 'Kiribati', 'Bairiki', ''),
('110', 'Asia', 'Cocos (keeling) Islands', 'West Island', 'CC', 'CCK', '', '.cc', 'AUD', '', NULL, NULL, 'Kokosinseln', 'West Island', ''),
('111', 'South America', 'Colombia', 'Santa Fé de Bogotá', 'CO', 'COL', 'COL', '.co', 'COP', '+57', NULL, NULL, 'Kolumbien', 'Santa Fé de Bogotá', ''),
('112', 'Africa', 'Comoros', 'Moroni', 'KM', 'COM', 'COM', '.km', 'KMF', '+269', '180', NULL, 'Komoren', 'Moroni', ''),
('113', 'Africa', 'Congo, The Democratic Republic Of The', 'Kinshasa', 'CD', 'COD', 'COD', '.cd', 'CDF', '+243', NULL, NULL, 'Kongo, Demokratische Republik', 'Kinshasa', ''),
('114', 'Africa', 'Congo', 'Brazzaville', 'CG', 'COG', 'CGO', '.cg', 'XAF', '+242', NULL, NULL, 'Kongo, Republik', 'Brazzaville', ''),
('115', 'Asia', 'Korea, Democratic People\'s Republic Of', 'Pjöngjang', 'KP', 'PRK', 'PRK', '.kp', 'KPW', '+850', NULL, NULL, 'Korea, Demokratische Volkrepublik', 'Pjöngjang', ''),
('116', 'Asia', 'Korea, Republic Of', 'Seoul', 'KR', 'KOR', 'KOR', '.kr', 'KRW', '+82', NULL, NULL, 'Korea, Republik', 'Seoul', ''),
('117', 'Europe', 'Croatia', 'Zagreb', 'HR', 'HRV', 'CRO', '.hr', 'HRK', '+385', NULL, NULL, 'Kroatien', 'Zagreb', ''),
('118', 'North America', 'Cuba', 'Havanna', 'CU', 'CUB', 'CUB', '.cu', 'CUP', '+53', NULL, NULL, 'Kuba', 'Havanna', ''),
('119', 'Asia', 'Kuwait', 'Kuwait', 'KW', 'KWT', 'KUW', '.kw', 'KWD', '+965', NULL, NULL, 'Kuwait', 'Kuwait', ''),
('120', 'Asia', 'Lao People\'s Democratic Republic', 'Vientiane', 'LA', 'LAO', 'LAO', '.la', 'LAK', '+856', NULL, NULL, 'Laos', 'Vientiane', ''),
('121', 'Africa', 'Lesotho', 'Maseru', 'LS', 'LSO', 'LES', '.ls', 'LSL', '+266', NULL, NULL, 'Lesotho', 'Maseru', ''),
('122', 'Europe', 'Latvia', 'Rīga', 'LV', 'LVA', 'LAT', '.lv', 'LVL', '+371', NULL, NULL, 'Lettland', 'Rīga', ''),
('123', 'Asia', 'Lebanon', 'Beirut', 'LB', 'LBN', 'LIB', '.lb', 'LBP', '+961', NULL, NULL, 'Libanon', 'Beirut', ''),
('124', 'Africa', 'Liberia', 'Monrovia', 'LR', 'LBR', 'LBR', '.lr', 'LRD', '+231', NULL, NULL, 'Liberia, Republik', 'Monrovia', ''),
('125', 'Africa', 'Libyan Arab Jamahiriya', 'Tripolis', 'LY', 'LBY', 'LBA', '.ly', 'LYD', '+218', NULL, NULL, 'Libyen', 'Tripolis', ''),
('126', 'Europe', 'Liechtenstein', 'Vaduz', 'LI', 'LIE', 'LIE', '.li', 'CHF', '+423', NULL, NULL, 'Liechtenstein, Fürstentum', 'Vaduz', ''),
('127', 'Europe', 'Lithuania', 'Wilna', 'LT', 'LTU', 'LTU', '.lt', 'LTL', '+370', NULL, NULL, 'Litauen', 'Wilna', ''),
('128', 'Europe', 'Luxembourg', 'Luxemburg', 'LU', 'LUX', 'LUX', '.lu', 'EUR', '+352', NULL, NULL, 'Luxemburg', 'Luxemburg', ''),
('129', 'Asia', 'Macao', '-/-', 'MO', 'MAC', '', '.mo', 'MOP', '+853', NULL, NULL, 'Macao', '-/-', ''),
('130', 'Africa', 'Madagascar', 'Antananarivo', 'MG', 'MDG', 'MAD', '.mg', 'MGA', '+261', NULL, NULL, 'Madagaskar, Republik', 'Antananarivo', ''),
('131', 'Africa', 'Malawi', 'Lilongwe', 'MW', 'MWI', 'MAW', '.mw', 'MWK', '+265', NULL, NULL, 'Malawi, Republik', 'Lilongwe', ''),
('132', 'Asia', 'Malaysia', 'Kuala Lumpur', 'MY', 'MYS', 'MAS', '.my', 'MYR', '+60', NULL, NULL, 'Malaysia', 'Kuala Lumpur', ''),
('133', 'Asia', 'Maldives', 'Malé', 'MV', 'MDV', 'MDV', '.mv', 'MVR', '+960', NULL, NULL, 'Malediven', 'Malé', ''),
('134', 'Africa', 'Mali', 'Bamako', 'ML', 'MLI', 'MLI', '.ml', 'XOF', '+223', NULL, NULL, 'Mali, Republik', 'Bamako', ''),
('135', 'Europe', 'Malta', 'Valletta', 'MT', 'MLT', 'MLT', '.mt', 'EUR', '+356', NULL, NULL, 'Malta', 'Valletta', ''),
('136', 'Africa', 'Morocco', 'Rabat', 'MA', 'MAR', 'MAR', '.ma', 'MAD', '+211', NULL, NULL, 'Marokko', 'Rabat', ''),
('137', 'Australia', 'Marshall Islands', 'Delap-Uliga-Darrit', 'MH', 'MHL', '', '.mh', 'USD', '+692', NULL, NULL, 'Marshallinseln', 'Delap-Uliga-Darrit', ''),
('138', 'North America', 'Martinique', 'Fort-de-France', 'MQ', 'MTQ', '', '.mq', 'EUR', '+596', NULL, NULL, 'Martinique', 'Fort-de-France', ''),
('139', 'Africa', 'Mauritania', 'Nouakchott', 'MR', 'MRT', 'MTN', '.mr', 'MRO', '+222', NULL, NULL, 'Mauretanien', 'Nouakchott', ''),
('140', 'Africa', 'Mauritius', 'Port Louis', 'MU', 'MUS', 'MRI', '.mu', 'MUR', '+230', NULL, NULL, 'Mauritius, Republik', 'Port Louis', ''),
('141', 'Africa', 'Mayotte', 'Mamoudzou', 'YT', 'MYT', '', '.yt', 'EUR', '+269', NULL, NULL, 'Mayotte', 'Mamoudzou', ''),
('142', 'Europe', 'Macedonia, The Former Yugoslav Republic Of', 'Skopje', 'MK', 'MKD', 'MKD', '.mk', 'MKD', '+389', NULL, NULL, 'Mazedonien', 'Skopje', ''),
('143', 'North America', 'Mexico', 'Mexiko-Stadt', 'MX', 'MEX', 'MEX', '.mx', 'MXN', '+52', NULL, NULL, 'Mexiko', 'Mexiko-Stadt', ''),
('144', 'Australia', 'Micronesia, Federated States Of', 'Palikir', 'FM', 'FSM', 'FSM', '.fm', 'USD', '+691', NULL, NULL, 'Mikronesien, Föderierte Staaten von', 'Palikir', ''),
('145', 'Europe', 'Moldova', 'Chişinău', 'MD', 'MDA', 'MDA', '.md', 'MDL', '+373', NULL, NULL, 'Moldawien', 'Chişinău', ''),
('146', 'Europe', 'Monaco', 'Monaco', 'MC', 'MCO', 'MON', '.mc', 'EUR', '+377', NULL, NULL, 'Monaco', 'Monaco', ''),
('147', 'Asia', 'Mongolia', 'Ulaanbaatar', 'MN', 'MNG', 'MGL', '.mn', 'MNT', '+976', NULL, NULL, 'Mongolei', 'Ulaanbaatar', ''),
('148', 'North America', 'Montserrat', 'Plymouth', 'MS', 'MSR', '', '.ms', 'XCD', '+1664', NULL, NULL, 'Montserrat', 'Plymouth', ''),
('149', 'Africa', 'Mozambique', 'Maputo', 'MZ', 'MOZ', 'MOZ', '.mz', 'MZM', '+258', NULL, NULL, 'Mosambik', 'Maputo', ''),
('150', 'Asia', 'Myanmar', 'Rangun', 'MM', 'MMR', 'MYA', '.mm', 'MMK', '+95', NULL, NULL, 'Myanmar', 'Rangun', ''),
('151', 'Africa', 'Namibia', 'Windhoek', 'NA', 'NAM', 'NAM', '.na', 'ZAR', '+264', NULL, NULL, 'Namibia, Republik', 'Windhoek', ''),
('152', 'Australia', 'Nauru', 'Yaren', 'NR', 'NRU', 'NRU', '.nr', 'AUD', '+674', NULL, NULL, 'Nauru', 'Yaren', ''),
('153', 'Asia', 'Nepal', 'Kathmandu', 'NP', 'NPL', 'NEP', '.np', 'NPR', '+977', NULL, NULL, 'Nepal', 'Kathmandu', ''),
('154', 'Australia', 'New Caledonia', 'Nouméa', 'NC', 'NCL', '', '.nc', 'XPF', '+687', NULL, NULL, 'Neukaledonien', 'Nouméa', ''),
('155', 'Australia', 'New Zealand', 'Wellington', 'NZ', 'NZL', 'NZL', '.nz', 'NZD', '+64', NULL, NULL, 'Neuseeland', 'Wellington', ''),
('156', 'Asia', 'Saudi–Iraqi neutral zone', '-/-', 'NT', 'NTZ', '', '.nt', '', '', NULL, NULL, 'Neutrale Zone (Irak)', '-/-', ''),
('157', 'North America', 'Nicaragua', 'Managua', 'NI', 'NIC', 'NCA', '.ni', 'NIO', '+55', NULL, NULL, 'Nicaragua', 'Managua', ''),
('158', 'Europe', 'Netherlands', 'Amsterdam', 'NL', 'NLD', 'NED', '.nl', 'EUR', '+31', NULL, NULL, 'Niederlande', 'Amsterdam', ''),
('159', 'North America', 'Netherlands Antilles', 'Willemstad', 'AN', 'ANT', 'AHO', '.an', 'ANG', '+599', NULL, NULL, 'Niederländische Antillen', 'Willemstad', ''),
('160', 'Africa', 'Niger', 'Niamey', 'NE', 'NER', 'NIG', '.ne', 'XOF', '+227', NULL, NULL, 'Niger', 'Niamey', ''),
('161', 'Africa', 'Nigeria', 'Abuja', 'NG', 'NGA', 'NGR', '.ng', 'NGN', '+234', NULL, NULL, 'Nigeria', 'Abuja', ''),
('162', 'Australia', 'Niue', 'Alofi', 'NU', 'NIU', '', '.nu', 'NZD', '+683', NULL, NULL, 'Niue', 'Alofi', ''),
('163', 'Australia', 'Northern Mariana Islands', 'Saipan', 'MP', 'MNP', '', '.mp', 'USD', '+1670', NULL, NULL, 'Nördliche Marianen', 'Saipan', ''),
('164', 'Australia', 'Norfolk Island', 'Kingston', 'NF', 'NFK', '', '.nf', 'AUD', '+6723', NULL, NULL, 'Norfolkinsel', 'Kingston', ''),
('165', 'Europe', 'Norway', 'Oslo', 'NO', 'NOR', 'NOR', '.no', 'NOK', '+47', NULL, NULL, 'Norwegen', 'Oslo', ''),
('166', 'Asia', 'Oman', 'Maskat', 'OM', 'OMN', 'OMA', '.om', 'OMR', '+968', NULL, NULL, 'Oman', 'Maskat', ''),
('167', 'Europe', 'Austria', 'Wien', 'AT', 'AUT', 'AUT', '.at', 'EUR', '+43', NULL, NULL, 'Österreich', 'Wien', ''),
('168', 'Asia', 'Pakistan', 'Islamabad', 'PK', 'PAK', 'PAK', '.pk', 'PKR', '+92', '300', NULL, 'Pakistan', 'Islamabad', ''),
('169', 'Asia', 'Palestinian Territory, Occupied', 'Ramallah', 'PS', 'PSE', 'PLE', '.ps', '', '+970', NULL, NULL, 'Palästinensische Autonomiegebiete', 'Ramallah', ''),
('170', 'Australia', 'Palau', 'Melekeok', 'PW', 'PLW', 'PLW', '.pw', 'USD', '+680', NULL, NULL, 'Palau', 'Melekeok', ''),
('171', 'South America', 'Panama', 'Panama-Stadt', 'PA', 'PAN', 'PAN', '.pa', 'USD', '+57', NULL, NULL, 'Panama', 'Panama-Stadt', ''),
('172', 'Australia', 'Papua New Guinea', 'Port Moresby', 'PG', 'PNG', '', '.pg', 'PGK', '+675', NULL, NULL, 'Papua-Neuguinea', 'Port Moresby', ''),
('173', 'South America', 'Paraguay', 'Asunción', 'PY', 'PRY', 'PAR', '.py', 'PYG', '+595', NULL, NULL, 'Paraguay', 'Asunción', ''),
('174', 'South America', 'Peru', 'Lima', 'PE', 'PER', 'PER', '.pe', 'PEN', '+51', NULL, NULL, 'Peru', 'Lima', ''),
('175', 'Asia', 'Philippines', 'Manila', 'PH', 'PHL', 'PHI', '.ph', 'PHP', '+63', NULL, NULL, 'Philippinen', 'Manila', ''),
('176', 'Australia', 'Pitcairn', 'Adamstown', 'PN', 'PCN', '', '.pn', 'NZD', '+649', NULL, NULL, 'Pitcairninseln', 'Adamstown', ''),
('177', 'Europe', 'Poland', 'Warschau', 'PL', 'POL', 'POL', '.pl', 'PLN', '+48', NULL, NULL, 'Polen', 'Warschau', ''),
('178', 'Europe', 'Portugal', 'Lissabon', 'PT', 'PRT', 'POR', '.pt', 'EUR', '+351', NULL, NULL, 'Portugal', 'Lissabon', ''),
('179', 'North America', 'Puerto Rico', 'San Juan', 'PR', 'PRI', 'PUR', '.pr', 'USD', '+1939', NULL, NULL, 'Puerto Rico', 'San Juan', ''),
('180', 'Africa', 'RÉunion', 'Saint-Denis', 'RE', 'REU', '', '.re', 'EUR', '+262', NULL, NULL, 'Réunion', 'Saint-Denis', ''),
('181', 'Africa', 'Rwanda', 'Kigali', 'RW', 'RWA', 'RWA', '.rw', 'RWF', '+250', NULL, NULL, 'Ruanda, Republik', 'Kigali', ''),
('182', 'Europe', 'Romania', 'Bukarest', 'RO', 'ROU', 'ROM', '.ro', 'RON', '+40', NULL, NULL, 'Rumänien', 'Bukarest', ''),
('183', 'Asia', 'Russian Federation', 'Moskau', 'RU', 'RUS', 'RUS', '.ru', 'RUB', '+7', NULL, NULL, 'Russische Föderation', 'Moskau', ''),
('184', 'Australia', 'Solomon Islands', 'Honiara', 'SB', 'SLB', 'SOL', '.sb', 'SBD', '+677', NULL, NULL, 'Salomonen', 'Honiara', ''),
('185', 'Africa', 'Zambia', 'Lusaka', 'ZM', 'ZMB', 'ZAM', '.zm', 'ZMK', '+260', NULL, NULL, 'Sambia, Republik', 'Lusaka', ''),
('186', 'Australia', 'Samoa', 'Apia', 'WS', 'WSM', 'SAM', '.ws', 'WST', '', NULL, NULL, 'Samoa', 'Apia', ''),
('187', 'Europe', 'San Marino', 'San Marino', 'SM', 'SMR', 'SMR', '.sm', 'EUR', '+378', NULL, NULL, 'San Marino', 'San Marino', ''),
('188', 'Africa', 'Sao Tome And Principe', 'São Tomé', 'ST', 'STP', 'STP', '.st', 'STD', '+239', NULL, NULL, 'São Tomé und Príncipe', 'São Tomé', ''),
('189', 'Asia', 'Saudi Arabia', 'Riad', 'SA', 'SAU', 'KSA', '.sa', 'SAR', '+966', NULL, NULL, 'Saudi-Arabien, Königreich', 'Riad', ''),
('190', 'Europe', 'Sweden', 'Stockholm', 'SE', 'SWE', 'SWE', '.se', 'SEK', '+46', NULL, NULL, 'Schweden', 'Stockholm', ''),
('191', 'Europe', 'Switzerland', 'Bern', 'CH', 'CHE', 'SUI', '.ch', 'CHF', '+41', NULL, NULL, 'Schweiz', 'Bern', ''),
('192', 'Africa', 'Senegal', 'Dakar', 'SN', 'SEN', 'SEN', '.sn', 'XOF', '+221', '0', NULL, 'Senegal', 'Dakar', ''),
('193', 'Europe', 'Serbien und Montenegro', 'Belgrad', 'CS', 'SCG', 'YUG', '.cs', '', '+381', NULL, NULL, 'Serbien und Montenegro', 'Belgrad', ''),
('194', 'Africa', 'Seychelles', 'Victoria', 'SC', 'SYC', 'SEY', '.sc', 'SCR', '+248', NULL, NULL, 'Seychellen, Republik der', 'Victoria', ''),
('195', 'Africa', 'Sierra Leone', 'Freetown', 'SL', 'SLE', 'SLE', '.sl', 'SLL', '+232', NULL, NULL, 'Sierra Leone, Republik', 'Freetown', ''),
('196', 'Africa', 'Zimbabwe', 'Harare', 'ZW', 'ZWE', 'ZIM', '.zw', 'ZWD', '+263', NULL, NULL, 'Simbabwe, Republik', 'Harare', ''),
('197', 'Asia', 'Singapore', 'Singapur', 'SG', 'SGP', 'SIN', '.sg', 'SGD', '+65', NULL, NULL, 'Singapur', 'Singapur', ''),
('198', 'Europe', 'Slovakia', 'Bratislava', 'SK', 'SVK', 'SVK', '.sk', 'SKK', '+421', NULL, NULL, 'Slowakei', 'Bratislava', ''),
('199', 'Europe', 'Slovenia', 'Ljubljana', 'SI', 'SVN', 'SLO', '.si', 'SIT', '+386', NULL, NULL, 'Slowenien', 'Ljubljana', ''),
('200', 'Africa', 'Somalia', 'Mogadischu', 'SO', 'SOM', 'SOM', '.so', 'SOS', '+252', NULL, NULL, 'Somalia, Demokratische Republik', 'Mogadischu', ''),
('201', 'Europe', 'Spain', 'Madrid', 'ES', 'ESP', 'ESP', '.es', 'EUR', '+34', NULL, NULL, 'Spanien', 'Madrid', ''),
('202', 'Asia', 'Sri Lanka', 'Colombo', 'LK', 'LKA', 'SRI', '.lk', 'LKR', '+94', NULL, NULL, 'Sri Lanka', 'Colombo', ''),
('203', 'North America', 'Saint Kitts And Nevis', 'Basseterre', 'KN', 'KNA', 'SKN', '.kn', 'XCD', '+1869', NULL, NULL, 'St. Kitts und Nevis', 'Basseterre', ''),
('204', 'South America', 'Saint Lucia', 'Castries', 'LC', 'LCA', 'LCA', '.lc', 'XCD', '+1758', NULL, NULL, 'St. Lucia', 'Castries', ''),
('205', 'North America', 'Saint Pierre And Miquelon', 'Saint-Pierre', 'PM', 'SPM', '', '.pm', 'EUR', '+508', NULL, NULL, 'St. Pierre und Miquelon', 'Saint-Pierre', ''),
('206', 'South America', 'Saint Vincent And The Grenadines', 'Kingstown', 'VC', 'VCT', 'VIN', '.vc', 'XCD', '+1784', NULL, NULL, 'St. Vincent und die Grenadinen (GB)', 'Kingstown', ''),
('207', 'Africa', 'South Africa', 'Tshwane / Pretoria', 'ZA', 'ZAF', 'RSA', '.za', 'ZAR', '+27', NULL, NULL, 'Südafrika, Republik', 'Tshwane / Pretoria', ''),
('208', 'Africa', 'Sudan', 'Khartum', 'SD', 'SDN', 'SUD', '.sd', 'SDD', '+249', NULL, NULL, 'Sudan', 'Khartum', ''),
('209', 'South America', 'South Georgia And The South Sandwich Islands', 'Grytviken', 'GS', 'SGS', '', '', 'GBP', '', NULL, NULL, 'Südgeorgien und die Südlichen Sandwichinseln', 'Grytviken', ''),
('210', 'South America', 'Suriname', 'Paramaribo', 'SR', 'SUR', 'SUR', '.sr', 'SRD', '+597', NULL, NULL, 'Suriname', 'Paramaribo', ''),
('211', 'Europe', 'Svalbard And Jan Mayen', 'Longyearbyen', 'SJ', 'SJM', '', '.sj', 'NOK', '', NULL, NULL, 'Svalbard und Jan Mayen', 'Longyearbyen', ''),
('212', 'Africa', 'Swaziland', 'Mbabane', 'SZ', 'SWZ', 'SWZ', '.sz', 'SZL', '+268', NULL, NULL, 'Swasiland', 'Mbabane', ''),
('213', 'Asia', 'Syrian Arab Republic', 'Damaskus', 'SY', 'SYR', 'SYR', '.sy', 'SYP', '+963', NULL, NULL, 'Syrien', 'Damaskus', ''),
('214', 'Asia', 'Tajikistan', 'Duschanbe', 'TJ', 'TJK', 'TJK', '.tj', 'RUB', '+992', NULL, NULL, 'Tadschikistan', 'Duschanbe', ''),
('215', 'Asia', 'Taiwan', 'Taipeh', 'TW', 'TWN', 'TPE', '.tw', 'TWD', '+886', NULL, NULL, 'Taiwan', 'Taipeh', ''),
('216', 'Africa', 'Tanzania, United Republic Of', 'Dodoma', 'TZ', 'TZA', 'TAN', '.tz', 'TZS', '+255', NULL, NULL, 'Tansania, Vereinigte Republik', 'Dodoma', ''),
('217', 'Asia', 'Thailand', 'Bangkok', 'TH', 'THA', 'THA', '.th', 'THB', '+66', NULL, NULL, 'Thailand', 'Bangkok', ''),
('218', 'Australia', 'Timor-leste', 'Dili', 'TL', 'TLS', '', '.tl', 'IDR', '+670', NULL, NULL, 'Timor-Leste, Demokratische Republik', 'Dili', ''),
('219', 'Africa', 'Togo', 'Lomé', 'TG', 'TGO', 'TOG', '.tg', 'XOF', '+228', NULL, NULL, 'Togo, Republik', 'Lomé', ''),
('220', 'Australia', 'Tokelau', '-/-', 'TK', 'TKL', '', '.tk', 'NZD', '+690', NULL, NULL, 'Tokelau', '-/-', ''),
('221', 'Australia', 'Tonga', 'Nuku’alofa', 'TO', 'TON', 'TGA', '.to', 'TOP', '+676', NULL, NULL, 'Tonga', 'Nuku’alofa', ''),
('222', 'South America', 'Trinidad And Tobago', 'Port-of-Spain', 'TT', 'TTO', 'TRI', '.tt', 'TTD', '+1868', NULL, NULL, 'Trinidad und Tobago', 'Port-of-Spain', ''),
('223', 'Africa', 'Tristan da Cunha', 'Jamestown', 'TA', 'TAA', '', '', '', '+290', NULL, NULL, 'Tristan da Cunha', 'Jamestown', ''),
('224', 'Africa', 'Chad', 'N\'Djamena', 'TD', 'TCD', 'CHA', '.td', 'XAF', '+235', NULL, NULL, 'Tschad, Republik', 'N\'Djamena', ''),
('225', 'Europe', 'Czech Republic', 'Prag', 'CZ', 'CZE', 'CZE', '.cz', 'CZK', '+420', NULL, NULL, 'Tschechische Republik', 'Prag', ''),
('226', 'Africa', 'Tunisia', 'Tunis', 'TN', 'TUN', 'TUN', '.tn', 'TND', '+216', '60', NULL, 'Tunesien', 'Tunis', ''),
('227', 'Asia', 'Turkey', 'Ankara', 'TR', 'TUR', 'TUR', '.tr', 'TRY', '+90', NULL, NULL, 'Türkei', 'Ankara', ''),
('228', 'Asia', 'Turkmenistan', 'Aşgabat', 'TM', 'TKM', 'TKM', '.tm', 'TMM', '+993', NULL, NULL, 'Turkmenistan', 'Aşgabat', ''),
('229', 'North America', 'Turks And Caicos Islands', 'Cockburn Town auf Grand Turk', 'TC', 'TCA', '', '.tc', 'USD', '+1649', NULL, NULL, 'Turks- und Caicosinseln', 'Cockburn Town auf Grand Turk', ''),
('230', 'Australia', 'Tuvalu', 'Funafuti', 'TV', 'TUV', '', '.tv', 'TVD', '+688', NULL, NULL, 'Tuvalu', 'Funafuti', ''),
('231', 'Africa', 'Uganda', 'Kampala', 'UG', 'UGA', 'UGA', '.ug', 'UGX', '+256', NULL, NULL, 'Uganda, Republik', 'Kampala', ''),
('232', 'Europe', 'Ukraine', 'Kiew', 'UA', 'UKR', 'UKR', '.ua', 'UAH', '+380', NULL, NULL, 'Ukraine', 'Kiew', ''),
('233', 'Europe', 'Soviet Union', 'Moskau', 'SU', 'SUN', 'URS', '.su', '', '', NULL, NULL, 'Union der Sozialistischen Sowjetrepubliken', 'Moskau', ''),
('234', 'South America', 'Uruguay', 'Montevideo', 'UY', 'URY', 'URU', '.uy', 'UYU', '+598', NULL, NULL, 'Uruguay', 'Montevideo', ''),
('235', 'Asia', 'Uzbekistan', 'Taschkent', 'UZ', 'UZB', 'UZB', '.uz', 'UZS', '+998', '300', NULL, 'Usbekistan', 'Taschkent', ''),
('236', 'Australia', 'Vanuatu', 'Port Vila', 'VU', 'VUT', 'VAN', '.vu', 'VUV', '+678', NULL, NULL, 'Vanuatu', 'Port Vila', ''),
('237', 'Europe', 'Holy See (vatican City State)', 'Vatikanstadt', 'VA', 'VAT', '', '.va', 'EUR', '+3906', NULL, NULL, 'Vatikanstadt', 'Vatikanstadt', ''),
('238', 'South America', 'Venezuela', 'Caracas', 'VE', 'VEN', 'VEN', '.ve', 'VEB', '+58', NULL, NULL, 'Venezuela', 'Caracas', ''),
('239', 'Asia', 'United Arab Emirates', 'Abu Dhabi', 'AE', 'ARE', 'UAE', '.ae', 'AED', '+971', NULL, NULL, 'Vereinigte Arabische Emirate', 'Abu Dhabi', ''),
('240', 'North America', 'United States', 'Washington, D.C.', 'US', 'USA', 'USA', '.us', 'USD', '+1', NULL, NULL, 'Vereinigte Staaten von Amerika', 'Washington, D.C.', ''),
('241', 'Europe', 'United Kingdom', 'London', 'GB', 'GBR', 'GBR', '.gb', 'GBP', '+44', NULL, NULL, 'Vereinigtes Königreich von Großbritannien und Nordirland', 'London', ''),
('242', 'Asia', 'Viet Nam', 'Hà Nội', 'VN', 'VNM', 'VIE', '.vn', 'VND', '+84', NULL, NULL, 'Vietnam', 'Hà Nội', ''),
('243', 'Australia', 'Wallis And Futuna', 'Mata-Utu', 'WF', 'WLF', '', '.wf', 'XPF', '+681', '720', 'http://en.wikipedia.org/wiki/Wallis_and_Futuna', 'Wallis und Futuna', 'Mata-Utu', ''),
('244', 'Asia', 'Christmas Island', 'Flying Fish Cove', 'CX', 'CXR', '', '.cx', 'AUD', '', NULL, NULL, 'Weihnachtsinsel', 'Flying Fish Cove', ''),
('245', 'Europe', 'Belarus', 'Minsk', 'BY', 'BLR', 'BLR', '.by', 'BYR', '+375', NULL, NULL, 'Weißrussland', 'Minsk', ''),
('246', 'Africa', 'Western Sahara', 'El Aaiún', 'EH', 'ESH', '', '.eh', 'MAD', '', NULL, NULL, 'Westsahara', 'El Aaiún', ''),
('247', 'Africa', 'Central African Republic', 'Bangui', 'CF', 'CAF', 'CAF', '.cf', 'XAF', '+236', NULL, NULL, 'Zentralafrikanische Republik', 'Bangui', ''),
('248', 'Asia', 'Cyprus', 'Nikosia', 'CY', 'CYP', 'CYP', '.cy', 'CYP', '+357', NULL, NULL, 'Zypern, Republik', 'Nikosia', ''),
('249', 'Europe', 'Hungary', 'Budapest', 'HU', 'HUN', 'HUN', '.hu', 'HUF', '+36', NULL, NULL, 'Ungarn', 'Budapest', ''),
('250', 'Europe', 'Montenegro', 'Podgorica', 'ME', 'MNE', 'MNE', '.me', '', '+382', NULL, NULL, 'Montenegro', 'Podgorica', '');
