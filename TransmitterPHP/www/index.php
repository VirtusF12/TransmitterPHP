<?php 
    require_once 'ServerResponse.php';
    
    $part = $_GET["part"];
    $action = $_GET["action"];
    $detalAction = $_GET["detalAction"];
    $typeQuery = 0;


    if ($action == "insert" || $action == "update" ||
        $action == "delete" || $action == "clear"){
        $typeQuery = 0;
    } else {
        $typeQuery = 1;
    }

    switch ($part){
        
        case "users": 
                switch ($action) {
                    case 'get':
                        
                        switch ($detalAction ) {
                            case 'getID':
                                if (isset($_GET["login"]) && isset($_GET["password"])){
                            
                                    $login = $_GET["login"];
                                    $password = $_GET["password"]; 
                                    $query = "SELECT id FROM users WHERE login = \"".$login."\" AND password = \"".$password."\"";
                                    $server = new ServerResponse($query, $typeQuery);
                                    // print($server->getResponse());                         
                                };break;
                            
                            default:
                                $query = "SELECT * FROM ".$part;
                                $server = new ServerResponse($query, $typeQuery);
                                ;break;
                        }
                        ;break;
                    //------------- 
                    case 'insert':

                     if (isset($_GET["login"]) && isset($_GET["password"])){
                        
                        $login = $_GET["login"];
                        $password = $_GET["password"]; 

                        $query = "INSERT INTO users VALUES(NULL, '".$login."','".$password."')";
                        $server = new ServerResponse($query, $typeQuery);

                     };break;
                    //-------------
                    case 'update':
                            
                            if (isset($_GET["login"]) && isset($_GET["password"])){

                                $login = $_GET["login"];
                                $password = $_GET["password"]; 
                            
                                $query = "UPDATE users SET password=".$password." WHERE login=".$login;
                                $server = new ServerResponse($query, $typeQuery);
                            }
                            
                        break;
                    //-------------
                    case 'delete':
                        $query = "DELETE FROM users WHERE id=2";
                        $server = new ServerResponse($query, $typeQuery);
                        break;
                    //-------------
                    case 'clear':
                        $query = "DELETE FROM users";
                        $server = new ServerResponse($query, $typeQuery);
                        break;
                    //-------------
                }
        ;break;
        
    }
?>