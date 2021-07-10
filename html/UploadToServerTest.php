<?php

    // http://transmitter.000webhostapp.com/UploadToServerTest.php/?login=Lex2
    // по умолчанию права на папку были установлены 755
    // а надо установить 777

    function upload_file_on_server($uploads_d){
        
        if (is_uploaded_file($_FILES['bill']['tmp_name'])) {
        
        	$uploads_dir = $uploads_d;
        	$tmp_name = $_FILES['bill']['tmp_name'];
        	$pic_name = $_FILES['bill']['name'];
         	move_uploaded_file($tmp_name, $uploads_dir.$pic_name);
      	
      	} else {
           	echo "File not uploaded successfully.";
      	}
    }
    
    
    if (isset($_GET['login'])) {
        
        $login = $_GET['login'];
        
        $root_dir = "file_user/"; 
        $upload_dir = $root_dir.$login."/";

        
        if (is_dir($root_dir)) {

        	echo 'такая директория есть: '.$root_dir.'<br><br>';

            if (is_dir($upload_dir)){

				echo 'такая директория есть: '.$upload_dir;
                upload_file_on_server($upload_dir);
            
            } else {
            
                echo "директории ".$upload_dir." нет";
                mkdir($upload_dir);
                echo "<br>директория ".$upload_dir." была создана";
                upload_file_on_server($upload_dir);
            }

        } else {
            mkdir($root_dir);
        }
    }



        /*
        $dir1 = mkdir($root_dir."testfile1/");
        if ($dir1){

            echo '<br>Директория 1 создана 111';
        }
        $dir2 = mkdir($root_dir."testfile2");
        
        if ($dir2){

            echo '<br>Директория 2 создана 222';
        }
        */
?>
    
    
    

	
