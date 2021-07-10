<?php
	require_once 'connection.php';
	require_once 'IHttpRequest.php';
	require_once 'TypeQueryEnum.php';

	class ServerResponse implements IHttpRequest {


		private $request;
		private $typeQuery;
		private $response;
		private $conn;

		function __construct($request, $typeQuery){


			$dbAccess = new DBAccess;
			$this->request = $request;
			$this->typeQuery = $typeQuery;

			$this->conn = mysqli_connect($dbAccess->getHost(), $dbAccess->getUser(), $dbAccess->getPassword(), $dbAccess->getDataBase()) 
	    	or die("Ошибка -> " . mysqli_error($this->conn)); 
		
			if ($this->typeQuery == 0){
				
				$this->common();
			
			} else if ($this->typeQuery == 1){
				
				$this->get();
			}

			mysqli_close($this->conn);
		}

		function getResponse(){
			return $this->response;
		}

		function get(){

			$result = mysqli_query($this->conn, $this->request) or die("Ошибка ->" . mysqli_error($this->conn)); 
           
			if($result) {

           		$rows = mysqli_num_rows($result); // количество строк

           		for ($i = 0; $i < $rows; $i++){
                    
                    $row = mysqli_fetch_row($result); // элементы в строке
                    $this->response .= '('; 
                    for ($j = 0; $j < count($row); $j++){
                    	$this->response .= '\''.$row[$j].'\'';
                    }
                    $this->response .= ')'; 
                }
                // mysqli_free_result($result);
			}
		}
		
		function common(){

			$result = mysqli_query($this->conn, $this->request) or die("Ошибка " . mysqli_error($this->conn)); 
            if ($result) {
                echo "<span style='color:blue;'>запрос выполнен</span>";
            } 
		}

	}
?>	



