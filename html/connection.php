<?php
	
	class DBAccess{

		private $host = 'localhost'; // адрес сервера 
		private $database = 'transmitter'; // имя базы данных
		private $user = 'root'; // имя пользователя
		private $password = 'sde9rebka435'; // пароль

		function getHost(){
			return $this->host;
		}
		function getDataBase(){
			return $this->database;
		}
		function getUser(){
			return $this->user;
		}
		function getPassword(){
			return $this->password;
		}
	}
	
?>


