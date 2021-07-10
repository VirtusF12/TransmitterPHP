<?php

    function show_json_file($login_dir_file){

        $files = array();
        
        foreach(glob($login_dir_file) as $file){ // glob('images/*.*') as $file
            array_push($files, basename($file));
        }   
          
        echo json_encode($files);
    }

    
    if (isset($_GET['list_files']) and isset($_GET['login'])){ // если данные пришли, тогда выполнить код
    
        $login = $_GET['login'];
        //echo $login;
        
        // проверка существования директории
        $root_dir = "file_user/"; // $root_dir = "images/"; - существует
        $login_dir = $root_dir.$login."/";
        $login_dir_file = $root_dir.$login."/*.*"; // 'images/*.*'
        /*
        echo "<br>".$root_dir;
        echo "<br>".$login_dir;
        echo "<br>".$login_dir_file;
        */
         
        if (is_dir($root_dir)){
            // т.к есть такая директория
            // проверка на существование директории с именем пользователя
            //echo "<br><br>Директория ".$root_dir." есть<br>";
            if (is_dir($login_dir)){
                //echo "<br><br><br>";
                show_json_file($login_dir_file);
                
            }else {
                //echo "<br><br>Директории ".$login_dir." <b>НЕТ</b><br>";
                mkdir($login_dir);
                //echo "<br><br>Директория ".$login_dir." <b>Создана</b><br>";
                show_json_file($login_dir_file);
            }
        }else{
            //echo "<br><br>Директории ".$root_dir." <b>НЕТ</b><br>";
            mkdir($root_dir);
            //echo "<br><br>Директория ".$root_dir." <b>Создана</b><br>";
            
            //echo "<br><br>Директории ".$login_dir." <b>НЕТ</b><br>";
            mkdir($login_dir);
            //echo "<br><br>Директория ".$login_dir." <b>Создана</b><br>";
            show_json_file($login_dir_file);    
        }
        
    }else {
        
        // echo 'GET - запроc, <b>НЕ</b> выполнился';
    }
?>
    
    
    

	
